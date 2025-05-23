package kg.ivy.exchangerates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kg.ivy.legacy.IvyWalletPreview
import kg.ivy.ui.R
import kg.ivy.wallet.ui.theme.Blue
import kg.ivy.wallet.ui.theme.components.BackBottomBar
import kg.ivy.wallet.ui.theme.components.IvyButton

@Composable
internal fun BoxWithConstraintsScope.ExchangeRatesBottomBar(
    onClose: () -> Unit,
    onAddRate: () -> Unit
) {
    BackBottomBar(onBack = onClose) {
        IvyButton(
            text = stringResource(R.string.add_manual_exchange_rate),
            iconStart = R.drawable.ic_plus
        ) {
            onAddRate()
        }
    }
}

@Preview
@Composable
private fun PreviewBottomBar() {
    IvyWalletPreview {
        Column(
            Modifier
                .fillMaxSize()
                .background(Blue)
        ) {
        }

        ExchangeRatesBottomBar(
            onAddRate = {},
            onClose = {}
        )
    }
}
