package kg.ivy.wallet.domain.pure.transaction

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import kg.ivy.data.model.Transaction
import kg.ivy.frp.Pure
import kg.ivy.wallet.domain.pure.util.mapIndexedNel
import kg.ivy.wallet.domain.pure.util.mapIndexedNelSuspend
import kg.ivy.wallet.domain.pure.util.nonEmptyListOfZeros
import java.math.BigDecimal

typealias ValueFunction<A> = (Transaction, A) -> BigDecimal
typealias SuspendValueFunction<A> = suspend (Transaction, A) -> BigDecimal

@Pure
fun <Arg> foldTransactions(
    transactions: List<Transaction>,
    valueFunctions: NonEmptyList<ValueFunction<Arg>>,
    arg: Arg
): NonEmptyList<BigDecimal> = sumTransactionsInternal(
    valueFunctionArgument = arg,
    transactions = transactions,
    valueFunctions = valueFunctions
)

@Pure
internal tailrec fun <A> sumTransactionsInternal(
    transactions: List<Transaction>,
    valueFunctionArgument: A,
    valueFunctions: NonEmptyList<ValueFunction<A>>,
    sum: NonEmptyList<BigDecimal> = nonEmptyListOfZeros(n = valueFunctions.size)
): NonEmptyList<BigDecimal> {
    return if (transactions.isEmpty()) {
        sum
    } else {
        sumTransactionsInternal(
            valueFunctionArgument = valueFunctionArgument,
            transactions = transactions.drop(1),
            valueFunctions = valueFunctions,
            sum = sum.mapIndexedNel { index, sumValue ->
                val valueFunction = valueFunctions[index]
                sumValue + valueFunction(transactions.first(), valueFunctionArgument)
            }
        )
    }
}

@Pure
suspend fun <Arg> foldTransactionsSuspend(
    transactions: List<Transaction>,
    valueFunctions: NonEmptyList<SuspendValueFunction<Arg>>,
    arg: Arg
): NonEmptyList<BigDecimal> = sumTransactionsSuspendInternal(
    transactions = transactions,
    valueFunctions = valueFunctions,
    valueFunctionArgument = arg
)

@Pure
internal tailrec suspend fun <A> sumTransactionsSuspendInternal(
    transactions: List<Transaction>,
    valueFunctionArgument: A,
    valueFunctions: NonEmptyList<SuspendValueFunction<A>>,
    sum: NonEmptyList<BigDecimal> = nonEmptyListOfZeros(n = valueFunctions.size)
): NonEmptyList<BigDecimal> {
    return if (transactions.isEmpty()) {
        sum
    } else {
        sumTransactionsSuspendInternal(
            valueFunctionArgument = valueFunctionArgument,
            transactions = transactions.drop(1),
            valueFunctions = valueFunctions,
            sum = sum.mapIndexedNelSuspend { index, sumValue ->
                val valueFunction = valueFunctions[index]
                sumValue + valueFunction(transactions.first(), valueFunctionArgument)
            }
        )
    }
}

suspend fun <A> sumTrns(
    transactions: List<Transaction>,
    valueFunction: SuspendValueFunction<A>,
    argument: A
): BigDecimal {
    return sumTransactionsSuspendInternal(
        transactions = transactions,
        valueFunctionArgument = argument,
        valueFunctions = nonEmptyListOf(valueFunction)
    ).head
}

@Deprecated("Uses legacy Transaction")
object LegacyFoldTransactions {

    @Pure
    suspend fun <Arg> foldTransactionsSuspend(
        transactions: List<kg.ivy.base.legacy.Transaction>,
        valueFunctions: NonEmptyList<suspend (kg.ivy.base.legacy.Transaction, Arg) -> BigDecimal>,
        arg: Arg
    ): NonEmptyList<BigDecimal> = sumTransactionsSuspendInternal(
        transactions = transactions,
        valueFunctions = valueFunctions,
        valueFunctionArgument = arg
    )

    @Deprecated("Kept only to stay compatible with legacy.Transaction")
    @Pure
    internal tailrec suspend fun <A> sumTransactionsSuspendInternal(
        transactions: List<kg.ivy.base.legacy.Transaction>,
        valueFunctionArgument: A,
        valueFunctions: NonEmptyList<suspend (kg.ivy.base.legacy.Transaction, A) -> BigDecimal>,
        sum: NonEmptyList<BigDecimal> = nonEmptyListOfZeros(n = valueFunctions.size)
    ): NonEmptyList<BigDecimal> {
        return if (transactions.isEmpty()) {
            sum
        } else {
            sumTransactionsSuspendInternal(
                valueFunctionArgument = valueFunctionArgument,
                transactions = transactions.drop(1),
                valueFunctions = valueFunctions,
                sum = sum.mapIndexedNelSuspend { index, sumValue ->
                    val valueFunction = valueFunctions[index]
                    sumValue + valueFunction(transactions.first(), valueFunctionArgument)
                }
            )
        }
    }

    @Deprecated("Kept only to stay compatible with legacy.Transaction")
    suspend fun <A> sumTrns(
        transactions: List<kg.ivy.base.legacy.Transaction>,
        valueFunction: suspend (kg.ivy.base.legacy.Transaction, A) -> BigDecimal,
        argument: A
    ): BigDecimal {
        return sumTransactionsSuspendInternal(
            transactions = transactions,
            valueFunctionArgument = argument,
            valueFunctions = nonEmptyListOf(valueFunction)
        ).head
    }
}