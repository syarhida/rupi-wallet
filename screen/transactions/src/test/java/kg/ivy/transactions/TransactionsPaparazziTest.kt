package kg.ivy.transactions

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import kg.ivy.ui.testing.PaparazziScreenshotTest
import kg.ivy.ui.testing.PaparazziTheme
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class TransactionsPaparazziTest(
    @TestParameter
    private val theme: PaparazziTheme,
) : PaparazziScreenshotTest() {
    @Test
    fun `snapshot Transactions Screen`() {
        snapshot(theme) {
            TransactionsUiTest(theme == PaparazziTheme.Dark)
        }
    }
}