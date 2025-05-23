package kg.ivy.wallet.ui.theme.modal

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kg.ivy.design.l0_system.UI
import kg.ivy.design.l0_system.style
import kg.ivy.legacy.utils.hideKeyboard
import kg.ivy.ui.R
import kg.ivy.wallet.ui.theme.Red
import kg.ivy.wallet.ui.theme.components.IvyNameTextField
import java.util.UUID

@SuppressLint("ComposeModifierMissing")
@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun BoxWithConstraintsScope.DeleteModal(
    title: String,
    description: String,
    visible: Boolean,
    dismiss: () -> Unit,
    id: UUID = UUID.randomUUID(),
    buttonText: String = stringResource(R.string.delete),
    iconStart: Int = R.drawable.ic_delete,
    onDelete: () -> Unit,
) {
    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalNegativeButton(
                text = buttonText,
                iconStart = iconStart
            ) {
                onDelete()
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = title,
            style = UI.typo.b1.style(
                color = Red,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = description,
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(Modifier.height(48.dp))
    }
}

@SuppressLint("ComposeModifierMissing")
@Composable
fun BoxWithConstraintsScope.DeleteConfirmationModal(
    title: String,
    description: String,
    visible: Boolean,
    enableDeletionButton: Boolean,
    onAccountNameChange: (String) -> Unit,
    dismiss: () -> Unit,
    id: UUID = UUID.randomUUID(),
    hint: String = stringResource(id = R.string.account_name),
    buttonText: String = stringResource(R.string.delete),
    iconStart: Int = R.drawable.ic_delete,
    onDelete: () -> Unit,
) {
    var deletionTextFieldValue by remember(this) {
        mutableStateOf(TextFieldValue(""))
    }
    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalNegativeButton(
                text = buttonText,
                iconStart = iconStart,
                enabled = enableDeletionButton
            ) {
                onDelete()
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = title,
            style = UI.typo.b1.style(
                color = Red,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = description,
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(Modifier.height(12.dp))

        val view = LocalView.current

        IvyNameTextField(
            modifier = Modifier
                .padding(start = 28.dp, end = 36.dp),
            underlineModifier = Modifier.padding(start = 24.dp, end = 32.dp),
            value = deletionTextFieldValue,
            hint = hint,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text,
                autoCorrect = true
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    hideKeyboard(view)
                }
            ),
        ) { newValue ->
            deletionTextFieldValue = newValue
            onAccountNameChange(newValue.text)
        }

        Spacer(Modifier.height(48.dp))
    }
}
