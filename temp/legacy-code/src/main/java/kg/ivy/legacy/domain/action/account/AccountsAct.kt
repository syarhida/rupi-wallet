package kg.ivy.wallet.domain.action.account

import kg.ivy.data.db.dao.read.AccountDao
import kg.ivy.frp.action.FPAction
import kg.ivy.legacy.datamodel.Account
import kg.ivy.legacy.datamodel.temp.toLegacyDomain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class AccountsAct @Inject constructor(
    private val accountDao: AccountDao
) : FPAction<Unit, ImmutableList<Account>>() {

    override suspend fun Unit.compose(): suspend () -> ImmutableList<Account> = suspend {
        io { accountDao.findAll().map { it.toLegacyDomain() }.toImmutableList() }
    }
}
