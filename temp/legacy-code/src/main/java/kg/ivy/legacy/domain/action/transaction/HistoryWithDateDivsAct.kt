package kg.ivy.wallet.domain.action.transaction

import kg.ivy.base.legacy.TransactionHistoryItem
import kg.ivy.frp.action.FPAction
import kg.ivy.frp.then
import kg.ivy.wallet.domain.pure.data.ClosedTimeRange
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class HistoryWithDateDivsAct @Inject constructor(
    private val historyTrnsAct: HistoryTrnsAct,
    private val trnsWithDateDivsAct: TrnsWithDateDivsAct
) : FPAction<HistoryWithDateDivsAct.Input, ImmutableList<TransactionHistoryItem>>() {

    override suspend fun Input.compose(): suspend () -> ImmutableList<TransactionHistoryItem> =
        suspend {
            range
        } then historyTrnsAct then { trns ->
            TrnsWithDateDivsAct.Input(
                baseCurrency = baseCurrency,
                transactions = trns
            )
        } then trnsWithDateDivsAct then { it.toImmutableList() }

    data class Input(
        val range: ClosedTimeRange,
        val baseCurrency: String
    )
}
