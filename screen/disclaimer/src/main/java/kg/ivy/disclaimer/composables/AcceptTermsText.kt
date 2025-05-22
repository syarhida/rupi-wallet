package kg.ivy.disclaimer.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AcceptTermsText(
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = "Harap baca dan setujui persyaratan berikut sebelum menggunakan Rupi:",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
    )
}