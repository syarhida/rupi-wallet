package kg.ivy.transactions

import androidx.compose.runtime.Immutable
import kg.ivy.base.legacy.Transaction
import kg.ivy.base.legacy.TransactionHistoryItem
import kg.ivy.data.model.Category
import kg.ivy.legacy.data.model.TimePeriod
import kg.ivy.legacy.datamodel.Account
import kg.ivy.wallet.ui.theme.modal.ChoosePeriodModalData
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class TransactionsState(
    val period: TimePeriod,
    val baseCurrency: String,
    val currency: String,
    val categories: ImmutableList<Category>,
    val accounts: ImmutableList<Account>,
    val account: Account?,
    val category: Category?,
    val balance: Double,
    val balanceBaseCurrency: Double?,
    val income: Double,
    val expenses: Double,
    val initWithTransactions: Boolean,
    val treatTransfersAsIncomeExpense: Boolean,
    val history: ImmutableList<TransactionHistoryItem>,
    val upcoming: ImmutableList<Transaction>,
    val upcomingExpanded: Boolean,
    val upcomingIncome: Double,
    val upcomingExpenses: Double,
    val overdue: ImmutableList<Transaction>,
    val overdueExpanded: Boolean,
    val overdueIncome: Double,
    val overdueExpenses: Double,
    val enableDeletionButton: Boolean,
    val skipAllModalVisible: Boolean,
    val deleteModal1Visible: Boolean,
    val choosePeriodModal: ChoosePeriodModalData?,
    val showAccountColorsInTransactions: Boolean
)
