package kg.ivy.planned.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kg.ivy.data.model.Category
import kg.ivy.design.l0_system.UI
import kg.ivy.design.l0_system.style
import kg.ivy.legacy.datamodel.Account
import kg.ivy.legacy.datamodel.PlannedPaymentRule
import kg.ivy.legacy.ui.component.transaction.SectionDivider
import kg.ivy.navigation.EditPlannedScreen
import kg.ivy.navigation.Navigation
import kg.ivy.navigation.navigation
import kg.ivy.ui.R
import kg.ivy.wallet.ui.theme.Gray
import kg.ivy.wallet.ui.theme.components.IvyIcon
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.absoluteValue

@Suppress("LongParameterList")
@Composable
fun PlannedPaymentsLazyColumn(
    Header: @Composable () -> Unit,
    currency: String,
    categories: ImmutableList<Category>,
    accounts: ImmutableList<Account>,
    oneTime: ImmutableList<PlannedPaymentRule>,
    oneTimeIncome: Double,
    oneTimeExpenses: Double,
    recurring: ImmutableList<PlannedPaymentRule>,
    recurringIncome: Double,
    recurringExpenses: Double,
    oneTimeExpanded: Boolean,
    recurringExpanded: Boolean,
    setOneTimeExpanded: (Boolean) -> Unit,
    setRecurringExpanded: (Boolean) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val nav = navigation()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        state = listState
    ) {
        item {
            Header()
        }

        plannedPaymentItems(
            nav = nav,
            currency = currency,
            categories = categories,
            accounts = accounts,
            listState = listState,

            oneTime = oneTime,
            oneTimeIncome = oneTimeIncome,
            oneTimeExpenses = oneTimeExpenses,
            oneTimeExpanded = oneTimeExpanded,
            setOneTimeExpanded = setOneTimeExpanded,

            recurring = recurring,
            recurringIncome = recurringIncome,
            recurringExpenses = recurringExpenses,
            recurringExpanded = recurringExpanded,
            setRecurringExpanded = setRecurringExpanded
        )
    }
}

@Suppress("LongParameterList")
private fun LazyListScope.plannedPaymentItems(
    nav: Navigation,
    currency: String,
    categories: ImmutableList<Category>,
    accounts: ImmutableList<Account>,
    listState: LazyListState,

    oneTime: ImmutableList<PlannedPaymentRule>,
    oneTimeIncome: Double,
    oneTimeExpenses: Double,
    oneTimeExpanded: Boolean,
    setOneTimeExpanded: (Boolean) -> Unit,

    recurring: ImmutableList<PlannedPaymentRule>,
    recurringIncome: Double,
    recurringExpenses: Double,
    recurringExpanded: Boolean,
    setRecurringExpanded: (Boolean) -> Unit,
) {
    if (oneTime.isNotEmpty()) {
        item {
            SectionDivider(
                expanded = oneTimeExpanded,
                setExpanded = setOneTimeExpanded,
                title = stringResource(R.string.one_time_payments),
                titleColor = UI.colors.pureInverse,
                baseCurrency = currency,
                income = oneTimeIncome,
                expenses = oneTimeExpenses.absoluteValue
            )
        }

        if (oneTimeExpanded) {
            itemsIndexed(oneTime) { _, item ->
                PlannedPaymentCard(
                    baseCurrency = currency,
                    categories = categories,
                    accounts = accounts,
                    plannedPayment = item,
                ) { plannedPaymentRule ->
                    onPlannedPaymentClick(
                        nav = nav,
                        listState = listState,
                        rule = plannedPaymentRule
                    )
                }
            }
        }
    }

    if (recurring.isNotEmpty()) {
        item {
            SectionDivider(
                expanded = recurringExpanded,
                setExpanded = setRecurringExpanded,
                title = stringResource(R.string.recurring_payments),
                titleColor = UI.colors.pureInverse,
                baseCurrency = currency,
                income = recurringIncome,
                expenses = recurringExpenses.absoluteValue
            )
        }

        if (recurringExpanded) {
            itemsIndexed(recurring) { _, item ->
                PlannedPaymentCard(
                    baseCurrency = currency,
                    categories = categories,
                    accounts = accounts,
                    plannedPayment = item,
                ) { plannedPaymentRule ->
                    onPlannedPaymentClick(
                        nav = nav,
                        listState = listState,
                        rule = plannedPaymentRule
                    )
                }
            }
        }
    }

    if (oneTime.isEmpty() && recurring.isEmpty()) {
        item {
            NoPlannedPaymentsEmptyState()
        }
    }

    item {
        // last spacer - scroll hack
        Spacer(Modifier.height(150.dp))
    }
}

private fun onPlannedPaymentClick(
    nav: Navigation,
    listState: LazyListState,
    rule: PlannedPaymentRule,
) {
    nav.navigateTo(
        EditPlannedScreen(
            plannedPaymentRuleId = rule.id,
            type = rule.type
        )
    )
}

@Composable
private fun LazyItemScope.NoPlannedPaymentsEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(64.dp))

        IvyIcon(
            icon = R.drawable.ic_planned_payments,
            tint = Gray
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.no_planned_payments),
            style = UI.typo.b1.style(
                color = Gray,
                fontWeight = FontWeight.ExtraBold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.no_planned_payments_description),
            style = UI.typo.b2.style(
                color = Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )
    }
}
