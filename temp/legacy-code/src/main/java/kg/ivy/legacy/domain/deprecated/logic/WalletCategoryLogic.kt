package kg.ivy.wallet.domain.deprecated.logic

import kg.ivy.base.legacy.Transaction
import kg.ivy.base.legacy.TransactionHistoryItem
import kg.ivy.base.model.TransactionType
import kg.ivy.base.time.TimeConverter
import kg.ivy.base.time.TimeProvider
import kg.ivy.data.db.dao.read.AccountDao
import kg.ivy.data.db.dao.read.SettingsDao
import kg.ivy.data.model.Category
import kg.ivy.data.model.CategoryId
import kg.ivy.data.repository.TransactionRepository
import kg.ivy.data.repository.mapper.TransactionMapper
import kg.ivy.legacy.data.model.filterOverdue
import kg.ivy.legacy.data.model.filterOverdueLegacy
import kg.ivy.legacy.data.model.filterUpcoming
import kg.ivy.legacy.data.model.filterUpcomingLegacy
import kg.ivy.legacy.datamodel.temp.toLegacy
import kg.ivy.legacy.datamodel.temp.toLegacyDomain
import kg.ivy.legacy.domain.pure.transaction.LegacyTrnDateDividers
import kg.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import kg.ivy.wallet.domain.deprecated.logic.currency.sumInBaseCurrency
import java.util.UUID
import javax.inject.Inject

@Deprecated("Migrate to FP Style")
class WalletCategoryLogic @Inject constructor(
    private val accountDao: AccountDao,
    private val settingsDao: SettingsDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val transactionRepository: TransactionRepository,
    private val transactionMapper: TransactionMapper,
    private val timeProvider: TimeProvider,
    private val timeConverter: TimeConverter,
) {

    suspend fun calculateCategoryBalance(
        category: Category,
        range: kg.ivy.legacy.data.model.FromToTimeRange,
        accountFilterSet: Set<UUID> = emptySet(),
        transactions: List<Transaction> = emptyList()
    ): Double {
        val baseCurrency = settingsDao.findFirst().currency
        val accounts = accountDao.findAll().map { it.toLegacyDomain() }

        return historyByCategory(
            category,
            range = range,
            accountFilterSet = accountFilterSet,
            transactions = transactions
        )
            .sumOf {
                val amount = exchangeRatesLogic.amountBaseCurrency(
                    transaction = it,
                    baseCurrency = baseCurrency,
                    accounts = accounts
                )

                when (it.type) {
                    TransactionType.INCOME -> amount
                    TransactionType.EXPENSE -> -amount
                    TransactionType.TRANSFER -> 0.0 // TODO: Transfer zero operation
                }
            }
    }

    suspend fun calculateCategoryIncome(
        category: Category,
        range: kg.ivy.legacy.data.model.FromToTimeRange,
        accountFilterSet: Set<UUID> = emptySet(),
    ): Double {
        return transactionRepository
            .findAllByCategoryAndTypeAndBetween(
                categoryId = category.id.value,
                type = TransactionType.INCOME,
                startDate = range.from(),
                endDate = range.to()
            ).map { it.toLegacy(transactionMapper) }
            .filter {
                accountFilterSet.isEmpty() || accountFilterSet.contains(it.accountId)
            }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateCategoryIncome(
        incomeTransaction: List<Transaction>,
        accountFilterSet: Set<UUID> = emptySet()
    ): Double {
        return incomeTransaction
            .filter {
                accountFilterSet.isEmpty() || accountFilterSet.contains(it.accountId)
            }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateCategoryExpenses(
        category: Category,
        range: kg.ivy.legacy.data.model.FromToTimeRange,
        accountFilterSet: Set<UUID> = emptySet(),
    ): Double {
        return transactionRepository
            .findAllByCategoryAndTypeAndBetween(
                categoryId = category.id.value,
                type = TransactionType.EXPENSE,
                startDate = range.from(),
                endDate = range.to()
            )
            .map {
                it.toLegacy(transactionMapper)
            }
            .filter {
                accountFilterSet.isEmpty() || accountFilterSet.contains(it.accountId)
            }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateCategoryExpenses(
        expenseTransactions: List<Transaction>,
        accountFilterSet: Set<UUID> = emptySet()
    ): Double {
        return expenseTransactions
            .filter {
                accountFilterSet.isEmpty() || accountFilterSet.contains(it.accountId)
            }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateUnspecifiedBalance(range: kg.ivy.legacy.data.model.FromToTimeRange): Double {
        return calculateUnspecifiedIncome(range) - calculateUnspecifiedExpenses(range)
    }

    suspend fun calculateUnspecifiedIncome(range: kg.ivy.legacy.data.model.FromToTimeRange): Double {
        return transactionRepository
            .findAllUnspecifiedAndTypeAndBetween(
                type = TransactionType.INCOME,
                startDate = range.from(),
                endDate = range.to()
            ).map { it.toLegacy(transactionMapper) }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateUnspecifiedExpenses(range: kg.ivy.legacy.data.model.FromToTimeRange): Double {
        return transactionRepository
            .findAllUnspecifiedAndTypeAndBetween(
                type = TransactionType.EXPENSE,
                startDate = range.from(),
                endDate = range.to()
            ).map { it.toLegacy(transactionMapper) }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun historyByCategoryAccountWithDateDividers(
        category: Category,
        range: kg.ivy.legacy.data.model.FromToTimeRange,
        accountFilterSet: Set<UUID>,
        transactions: List<Transaction> = emptyList()
    ): List<TransactionHistoryItem> {
        return with(LegacyTrnDateDividers) {
            historyByCategory(category, range, transactions = transactions)
                .filter {
                    accountFilterSet.isEmpty() || accountFilterSet.contains(it.accountId)
                }
                .withDateDividers(
                    exchangeRatesLogic = exchangeRatesLogic,
                    settingsDao = settingsDao,
                    accountDao = accountDao,
                    timeConverter = timeConverter,
                )
        }
    }

    suspend fun historyByCategory(
        category: Category,
        range: kg.ivy.legacy.data.model.FromToTimeRange,
        accountFilterSet: Set<UUID> = emptySet(),
        transactions: List<Transaction> = emptyList()
    ): List<Transaction> {
        val trans = transactions.ifEmpty {
            transactionRepository
                .findAllByCategoryAndBetween(
                    categoryId = category.id.value,
                    startDate = range.from(),
                    endDate = range.to()
                ).map { it.toLegacy(transactionMapper) }
        }

        return trans.filter {
            accountFilterSet.isEmpty() || accountFilterSet.contains(it.accountId)
        }
    }

    suspend fun historyUnspecified(range: kg.ivy.legacy.data.model.FromToTimeRange): List<TransactionHistoryItem> {
        return with(LegacyTrnDateDividers) {
            transactionRepository
                .findAllUnspecifiedAndBetween(
                    startDate = range.from(),
                    endDate = range.to()
                ).map { it.toLegacy(transactionMapper) }
                .withDateDividers(
                    exchangeRatesLogic = exchangeRatesLogic,
                    settingsDao = settingsDao,
                    accountDao = accountDao,
                    timeConverter = timeConverter,
                )
        }
    }

    suspend fun calculateUpcomingIncomeByCategory(
        category: Category,
        range: kg.ivy.legacy.data.model.FromToTimeRange
    ): Double {
        return upcomingByCategoryLegacy(category = category, range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateUpcomingExpensesByCategory(
        category: Category,
        range: kg.ivy.legacy.data.model.FromToTimeRange
    ): Double {
        return upcomingByCategoryLegacy(category = category, range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateUpcomingIncomeUnspecified(range: kg.ivy.legacy.data.model.FromToTimeRange): Double {
        return upcomingUnspecifiedLegacy(range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateUpcomingExpensesUnspecified(range: kg.ivy.legacy.data.model.FromToTimeRange): Double {
        return upcomingUnspecifiedLegacy(range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    @Deprecated("Uses legacy Transaction")
    suspend fun upcomingByCategoryLegacy(
        category: Category,
        range: kg.ivy.legacy.data.model.FromToTimeRange
    ): List<Transaction> {
        return transactionRepository.findAllDueToBetweenByCategory(
            categoryId = CategoryId(category.id.value),
            startDate = range.upcomingFrom(timeProvider),
            endDate = range.to()
        )
            .map {
                it.toLegacy(transactionMapper)
            }
            .filterUpcomingLegacy(timeProvider, timeConverter)
    }

    suspend fun upcomingByCategory(
        category: Category,
        range: kg.ivy.legacy.data.model.FromToTimeRange
    ): List<kg.ivy.data.model.Transaction> {
        return transactionRepository.findAllDueToBetweenByCategory(
            categoryId = CategoryId(category.id.value),
            startDate = range.upcomingFrom(timeProvider),
            endDate = range.to()
        ).filterUpcoming()
    }

    @Deprecated("Uses legacy Transaction")
    suspend fun upcomingUnspecifiedLegacy(range: kg.ivy.legacy.data.model.FromToTimeRange): List<Transaction> {
        return transactionRepository.findAllDueToBetweenByCategoryUnspecified(
            startDate = range.upcomingFrom(timeProvider),
            endDate = range.to()
        )
            .map {
                it.toLegacy(transactionMapper)
            }
            .filterUpcomingLegacy(timeProvider, timeConverter)
    }

    suspend fun upcomingUnspecified(
        range: kg.ivy.legacy.data.model.FromToTimeRange
    ): List<kg.ivy.data.model.Transaction> {
        return transactionRepository.findAllDueToBetweenByCategoryUnspecified(
            startDate = range.upcomingFrom(timeProvider),
            endDate = range.to()
        ).filterUpcoming()
    }

    suspend fun calculateOverdueIncomeByCategory(
        category: Category,
        range: kg.ivy.legacy.data.model.FromToTimeRange
    ): Double {
        return overdueByCategoryLegacy(category, range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateOverdueExpensesByCategory(
        category: Category,
        range: kg.ivy.legacy.data.model.FromToTimeRange
    ): Double {
        return overdueByCategoryLegacy(category, range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateOverdueIncomeUnspecified(range: kg.ivy.legacy.data.model.FromToTimeRange): Double {
        return overdueUnspecifiedLegacy(range = range)
            .filter { it.type == TransactionType.INCOME }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun calculateOverdueExpensesUnspecified(range: kg.ivy.legacy.data.model.FromToTimeRange): Double {
        return overdueUnspecifiedLegacy(range = range)
            .filter { it.type == TransactionType.EXPENSE }
            .sumInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    @Deprecated("Uses legacy Transaction")
    suspend fun overdueByCategoryLegacy(
        category: Category,
        range: kg.ivy.legacy.data.model.FromToTimeRange
    ): List<Transaction> {
        return transactionRepository.findAllDueToBetweenByCategory(
            categoryId = CategoryId(category.id.value),
            startDate = range.from(),
            endDate = range.overdueTo(timeProvider)
        )
            .map {
                it.toLegacy(transactionMapper)
            }
            .filterOverdueLegacy(timeProvider, timeConverter)
    }

    suspend fun overdueByCategory(
        category: Category,
        range: kg.ivy.legacy.data.model.FromToTimeRange
    ): List<kg.ivy.data.model.Transaction> {
        return transactionRepository.findAllDueToBetweenByCategory(
            categoryId = CategoryId(category.id.value),
            startDate = range.from(),
            endDate = range.overdueTo(timeProvider)
        )
            .filterOverdue()
    }

    @Deprecated("Uses legacy Transaction")
    suspend fun overdueUnspecifiedLegacy(range: kg.ivy.legacy.data.model.FromToTimeRange): List<Transaction> {
        return transactionRepository.findAllDueToBetweenByCategoryUnspecified(
            startDate = range.from(),
            endDate = range.overdueTo(timeProvider)
        )
            .map {
                it.toLegacy(transactionMapper)
            }
            .filterOverdueLegacy(timeProvider, timeConverter)
    }

    suspend fun overdueUnspecified(
        range: kg.ivy.legacy.data.model.FromToTimeRange
    ): List<kg.ivy.data.model.Transaction> {
        return transactionRepository.findAllDueToBetweenByCategoryUnspecified(
            startDate = range.from(),
            endDate = range.overdueTo(timeProvider)
        ).filterOverdue()
    }
}
