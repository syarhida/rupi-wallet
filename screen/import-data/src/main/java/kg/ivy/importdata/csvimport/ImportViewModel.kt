package kg.ivy.importdata.csvimport

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kg.ivy.frp.test.TestIdlingResource
import kg.ivy.legacy.domain.deprecated.logic.csv.CSVImporter
import kg.ivy.legacy.domain.deprecated.logic.csv.model.ImportType
import kg.ivy.data.backup.BackupDataUseCase
import kg.ivy.legacy.utils.asLiveData
import kg.ivy.base.legacy.getFileName
import kg.ivy.navigation.ImportScreen
import kg.ivy.navigation.Navigation
import kg.ivy.onboarding.viewmodel.OnboardingViewModel
import kg.ivy.wallet.domain.deprecated.logic.csv.CSVMapper
import kg.ivy.wallet.domain.deprecated.logic.csv.CSVNormalizer
import kg.ivy.data.file.FileSystem
import kg.ivy.data.backup.ImportResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val ivyContext: kg.ivy.legacy.IvyWalletCtx,
    private val nav: Navigation,
    private val fileReader: FileSystem,
    private val csvNormalizer: CSVNormalizer,
    private val csvMapper: CSVMapper,
    private val csvImporter: CSVImporter,
    private val backupDataUseCase: BackupDataUseCase
) : ViewModel() {
    private val _importStep = MutableLiveData<ImportStep>()
    val importStep = _importStep.asLiveData()

    private val _importType = MutableLiveData<ImportType>()
    val importType = _importType.asLiveData()

    private val _importProgressPercent = MutableLiveData<Int>()
    val importProgressPercent = _importProgressPercent.asLiveData()

    private val _importResult = MutableLiveData<ImportResult>()
    val importResult = _importResult.asLiveData()

    fun start(screen: ImportScreen) {
        nav.onBackPressed[screen] = {
            when (importStep.value) {
                ImportStep.IMPORT_FROM -> false
                ImportStep.INSTRUCTIONS -> {
                    _importStep.value = ImportStep.IMPORT_FROM
                    true
                }

                ImportStep.LOADING -> {
                    // do nothing, disable back
                    true
                }

                ImportStep.RESULT -> {
                    _importStep.value = ImportStep.IMPORT_FROM
                    true
                }

                null -> false
            }
        }
    }

    @ExperimentalStdlibApi
    fun uploadFile(context: Context) {
        val importType = importType.value ?: return

        ivyContext.openFile { fileUri ->
            viewModelScope.launch {
                TestIdlingResource.increment()

                _importStep.value = ImportStep.LOADING

                _importResult.value = if (hasCSVExtension(context, fileUri)) {
                    restoreCSVFile(fileUri = fileUri, importType = importType)
                } else {
                    backupDataUseCase.importBackupFile(
                        backupFileUri = fileUri
                    ) { progressPercent ->
                        kg.ivy.legacy.utils.uiThread {
                            _importProgressPercent.value =
                                (progressPercent * 100).roundToInt()
                        }
                    }
                }

                _importStep.value = ImportStep.RESULT

                TestIdlingResource.decrement()
            }
        }
    }

    @ExperimentalStdlibApi
    private suspend fun restoreCSVFile(fileUri: Uri, importType: ImportType): ImportResult {
        return kg.ivy.legacy.utils.ioThread {
            val rawCSV = fileReader.read(
                uri = fileUri,
                charset = when (importType) {
                    ImportType.IVY -> Charsets.UTF_16
                    else -> Charsets.UTF_8
                }
            ).getOrNull()
            if (rawCSV.isNullOrBlank()) {
                return@ioThread ImportResult(
                    rowsFound = 0,
                    transactionsImported = 0,
                    accountsImported = 0,
                    categoriesImported = 0,
                    failedRows = persistentListOf()
                )
            }

            val normalizedCSV = csvNormalizer.normalize(
                rawCSV = rawCSV,
                importType = importType
            )

            val mapping = csvMapper.mapping(
                type = importType,
                headerRow = normalizedCSV.split("\n").getOrNull(0)
            )

            return@ioThread try {
                val result = csvImporter.import(
                    csv = normalizedCSV,
                    rowMapping = mapping,
                    onProgress = { progressPercent ->
                        kg.ivy.legacy.utils.uiThread {
                            _importProgressPercent.value =
                                (progressPercent * 100).roundToInt()
                        }
                    }
                )

                if (result.failedRows.isNotEmpty()) {
                    Timber.e("Import failed rows: ${result.failedRows}")
                }

                result
            } catch (e: Exception) {
                e.printStackTrace()
                ImportResult(
                    rowsFound = 0,
                    transactionsImported = 0,
                    accountsImported = 0,
                    categoriesImported = 0,
                    failedRows = persistentListOf()
                )
            }
        }
    }

    fun setImportType(importType: ImportType) {
        _importType.value = importType
        _importStep.value = ImportStep.INSTRUCTIONS
    }

    fun skip(
        screen: ImportScreen,
        onboardingViewModel: OnboardingViewModel
    ) {
        if (screen.launchedFromOnboarding) {
            onboardingViewModel.importSkip()
        }

        nav.back()
        resetState()
    }

    fun finish(
        screen: ImportScreen,
        onboardingViewModel: OnboardingViewModel
    ) {
        if (screen.launchedFromOnboarding) {
            val importSuccess = importResult.value?.transactionsImported?.let { it > 0 } ?: false
            onboardingViewModel.importFinished(
                success = importSuccess
            )
        }

        nav.back()
        resetState()
    }

    private fun resetState() {
        _importStep.value = ImportStep.IMPORT_FROM
    }

    private suspend fun hasCSVExtension(
        context: Context,
        fileUri: Uri
    ): Boolean = kg.ivy.legacy.utils.ioThread {
        val fileName = context.getFileName(fileUri)
        fileName?.endsWith(suffix = ".csv", ignoreCase = true) ?: false
    }
}
