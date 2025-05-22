package kg.ivy.disclaimer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kg.ivy.data.repository.LegalRepository
import kg.ivy.navigation.Navigation
import kg.ivy.ui.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DisclaimerViewModel @Inject constructor(
    private val navigation: Navigation,
    private val legalRepo: LegalRepository,
) : ComposeViewModel<DisclaimerViewState, DisclaimerViewEvent>() {

    private var checkboxes by mutableStateOf(LegalCheckboxes)

    @Composable
    override fun uiState(): DisclaimerViewState {
        return DisclaimerViewState(
            checkboxes = checkboxes,
            agreeButtonEnabled = checkboxes.all(CheckboxViewState::checked),
        )
    }

    override fun onEvent(event: DisclaimerViewEvent) {
        when (event) {
            DisclaimerViewEvent.OnAgreeClick -> handleAgreeClick()
            is DisclaimerViewEvent.OnCheckboxClick -> handleCheckboxClick(event)
        }
    }

    private fun handleAgreeClick() {
        viewModelScope.launch {
            legalRepo.setDisclaimerAccepted(accepted = true)
            navigation.back()
        }
    }

    private fun handleCheckboxClick(event: DisclaimerViewEvent.OnCheckboxClick) {
        checkboxes = checkboxes.mapIndexed { index, item ->
            if (index == event.index) {
                item.copy(
                    checked = !item.checked
                )
            } else {
                item
            }
        }.toImmutableList()
    }

    companion object {
        // LEGAL text - do NOT extract or translate
        val LegalCheckboxes = listOf(
            CheckboxViewState(
                text = "Saya mengakui bahwa aplikasi ini bersifat sumber terbuka dan disediakan 'apa adanya' " +
                        "tanpa jaminan, tersurat maupun tersirat. " +
                        "Saya sepenuhnya menerima semua risiko kesalahan, kerusakan, atau kegagalan, " +
                        "menggunakan aplikasi ini sepenuhnya atas risiko saya sendiri.",
                checked = false,
            ),
            CheckboxViewState(
                text = "Saya mengerti bahwa tidak ada jaminan atas keakuratan, " +
                        "keandalan, atau kelengkapan data saya. " +
                        "Pencadangan data manual adalah tanggung jawab saya, dan saya setuju untuk tidak menuntut " +
                        "aplikasi ini atas kehilangan data apa pun.",
                checked = false,
            ),
            CheckboxViewState(
                text = "Saya dengan ini membebaskan pengembang aplikasi, kontributor, " +
                        "dan perusahaan yang mendistribusikan dari segala tanggung jawab atas " +
                        "klaim, kerusakan, biaya hukum, atau kerugian, termasuk yang diakibatkan " +
                        "oleh pelanggaran keamanan atau ketidakakuratan data.",
                checked = false,
            ),
            CheckboxViewState(
                text = "Saya menyadari dan menerima bahwa aplikasi ini mungkin menampilkan informasi yang menyesatkan " +
                        "atau mengandung ketidakakuratan. " +
                        "Saya memikul tanggung jawab penuh untuk memverifikasi integritas " +
                        "data dan perhitungan keuangan sebelum membuat " +
                        "keputusan apa pun berdasarkan data aplikasi.",
                checked = false,
            ),
        ).toImmutableList()
    }
}