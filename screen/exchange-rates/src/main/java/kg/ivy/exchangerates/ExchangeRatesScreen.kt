package kg.ivy.exchangerates

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kg.ivy.base.legacy.Theme
import kg.ivy.design.l0_system.UI
import kg.ivy.design.l0_system.style
import kg.ivy.design.l1_buildingBlocks.ColumnRoot
import kg.ivy.design.l1_buildingBlocks.DividerW
import kg.ivy.design.l1_buildingBlocks.SpacerHor
import kg.ivy.design.l1_buildingBlocks.SpacerVer
import kg.ivy.exchangerates.component.RateItem
import kg.ivy.exchangerates.data.RateUi
import kg.ivy.exchangerates.modal.AddRateModal
import kg.ivy.legacy.IvyWalletPreview
import kg.ivy.legacy.ui.SearchInput
import kg.ivy.legacy.utils.selectEndTextFieldValue
import kg.ivy.navigation.navigation
import kg.ivy.wallet.ui.theme.modal.edit.AmountModal
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID

@Composable
fun BoxWithConstraintsScope.ExchangeRatesScreen() {
    val viewModel: ExchangeRatesViewModel = viewModel()
    val state = viewModel.uiState()

    UI(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    state: RatesState,
    onEvent: (RatesEvent) -> Unit,
) {
    val nav = navigation()
    var amountModalVisible by remember {
        mutableStateOf(false)
    }
    var rateToUpdate by remember {
        mutableStateOf<RateUi?>(null)
    }
    var amountModalId by remember {
        mutableStateOf(UUID.randomUUID())
    }
    val onRateClick = { rate: RateUi ->
        rateToUpdate = rate
        amountModalId = UUID.randomUUID()
        amountModalVisible = true
    }

    ColumnRoot {
        SpacerVer(height = 16.dp)
        SearchField(onSearch = { onEvent(RatesEvent.Search(it)) })
        SpacerVer(height = 4.dp)
        LazyColumn {
            ratesSection(text = "Manual")
            items(items = state.manual) { rate ->
                SpacerVer(height = 4.dp)
                RateItem(
                    rate = rate,
                    onDelete = { onEvent(RatesEvent.RemoveOverride(rate)) },
                    onClick = { onRateClick(rate) }
                )
            }
            ratesSection(text = "Automatic")
            items(items = state.automatic) { rate ->
                SpacerVer(height = 4.dp)
                RateItem(
                    rate = rate,
                    onDelete = null,
                    onClick = { onRateClick(rate) }
                )
            }
            item(key = "last_item_spacer") {
                SpacerVer(height = 480.dp)
            }
        }
    }

    var addRateModalVisible by remember {
        mutableStateOf(false)
    }

    ExchangeRatesBottomBar(
        onClose = { nav.back() },
        onAddRate = { addRateModalVisible = true }
    )

    AddRateModal(
        visible = addRateModalVisible,
        baseCurrency = state.baseCurrency,
        dismiss = {
            addRateModalVisible = false
        },
        onAdd = onEvent
    )

    AmountModal(
        id = amountModalId,
        visible = amountModalVisible,
        currency = "",
        initialAmount = rateToUpdate?.rate,
        dismiss = {
            amountModalVisible = false
        },
        decimalCountMax = 12,
        Header = {
            rateToUpdate?.let {
                SpacerVer(height = 24.dp)
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = "${it.from}-${it.to}",
                    style = UI.typo.nH2.style(
                        textAlign = TextAlign.Center,
                        color = UI.colors.primary
                    )
                )
            }
        },
        onAmountChanged = { newRate ->
            rateToUpdate?.let {
                onEvent(RatesEvent.UpdateRate(rateToUpdate!!, newRate))
            }
        }
    )
}

private fun LazyListScope.ratesSection(
    text: String
) {
    item {
        SpacerVer(height = 24.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            DividerW()
            SpacerHor(width = 16.dp)
            Text(
                text = text,
                style = UI.typo.h2
            )
            SpacerHor(width = 16.dp)
            DividerW()
        }
    }
}

@Composable
private fun SearchField(
    onSearch: (String) -> Unit,
) {
    var searchQueryTextFieldValue by remember {
        mutableStateOf(selectEndTextFieldValue(""))
    }

    SearchInput(
        searchQueryTextFieldValue = searchQueryTextFieldValue,
        hint = "Search currency",
        focus = false,
        showClearIcon = searchQueryTextFieldValue.text.isNotEmpty(),
        onSetSearchQueryTextField = {
            searchQueryTextFieldValue = it
            onSearch(it.text)
        }
    )
}

@Preview
@Composable
private fun Preview(theme: Theme = Theme.LIGHT) {
    IvyWalletPreview(theme) {
        UI(
            state = RatesState(
                baseCurrency = "BGN",
                manual = persistentListOf(
                    RateUi("BGN", "USD", 1.85),
                    RateUi("BGN", "EUR", 1.96),
                ),
                automatic = persistentListOf(
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                    RateUi("XXX", "YYY", 1.23),
                )
            ),
            onEvent = {}
        )
    }
}

/** For screenshot testing */
@Composable
fun ExchangeRatesScreenUiTest(isDark: Boolean) {
    val theme = when (isDark) {
        true -> Theme.DARK
        false -> Theme.LIGHT
    }
    Preview(theme)
}