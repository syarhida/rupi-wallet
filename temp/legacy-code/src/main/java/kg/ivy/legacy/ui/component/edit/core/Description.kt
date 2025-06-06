package kg.ivy.legacy.ui.component.edit.core

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kg.ivy.design.l0_system.UI
import kg.ivy.design.l0_system.style
import kg.ivy.legacy.IvyWalletComponentPreview
import kg.ivy.legacy.ui.component.edit.PrimaryAttributeColumn
import kg.ivy.legacy.utils.isNotNullOrBlank
import kg.ivy.ui.R
import kg.ivy.wallet.ui.theme.components.AddPrimaryAttributeButton

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun Description(
    description: String?,
    onAddDescription: () -> Unit,
    onEditDescription: (String) -> Unit
) {
    if (description.isNotNullOrBlank()) {
        DescriptionText(
            description = description!!,
            onClick = {
                onEditDescription(description)
            }
        )
    } else {
        AddPrimaryAttributeButton(
            icon = R.drawable.ic_description,
            text = stringResource(R.string.add_description),
            onClick = onAddDescription
        )
    }
}

@Composable
private fun DescriptionText(
    description: String,
    onClick: () -> Unit,
) {
    PrimaryAttributeColumn(
        icon = R.drawable.ic_description,
        title = stringResource(R.string.description),
        onClick = onClick
    ) {
        Spacer(Modifier.height(12.dp))

        Text(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .testTag("trn_description"),
            text = description,
            style = UI.typo.nB2.style(
                textAlign = TextAlign.Left
            ),
        )

        Spacer(Modifier.height(20.dp))
    }
}

@Preview
@Composable
private fun PreviewDescription_Empty() {
    IvyWalletComponentPreview {
        Description(
            description = null,
            onAddDescription = {}
        ) {
        }
    }
}

@Preview
@Composable
private fun PreviewDescription_withText() {
    IvyWalletComponentPreview {
        Description(
            description = "This is my sample description.",
            onAddDescription = {}
        ) {
        }
    }
}
