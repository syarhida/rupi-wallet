package kg.ivy.domain.usecase.wallet

import arrow.core.Option
import kg.ivy.data.model.PositiveValue
import kg.ivy.data.model.primitive.AssetCode
import kg.ivy.data.repository.TransactionRepository
import kg.ivy.domain.model.StatSummary
import kg.ivy.domain.model.TimeRange
import kg.ivy.domain.usecase.exchange.ExchangeUseCase
import javax.inject.Inject

@Suppress("UnusedPrivateProperty", "UnusedParameter")
class WalletStatsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val exchangeUseCase: ExchangeUseCase,
) {
    /**
     * Calculates the stats for Ivy Wallet including excluded accounts.
     * It ignores transfers and focuses only on income and expenses.
     * Stats that can't be exchanged in [outCurrency] are skipped
     * and accumulated as [ExchangedWalletStats.exchangeErrors].
     */
    suspend fun calculate(
        range: TimeRange,
        outCurrency: AssetCode
    ): ExchangedWalletStats {
        // Use the StatSummaryBuilder
        TODO("Not implemented")
    }

    /**
     * Calculates the stats for Ivy Wallet including excluded accounts.
     * It ignores transfers and focuses only on income and expenses.
     */
    suspend fun calculate(
        timeRange: TimeRange
    ): WalletStats {
        // Use the StatSummaryBuilder
        TODO("Not implemented")
    }
}

data class WalletStats(
    val income: StatSummary,
    val expense: StatSummary,
)

data class ExchangedWalletStats(
    val income: Option<PositiveValue>,
    val expense: Option<PositiveValue>,
    val exchangeErrors: Set<AssetCode>,
)