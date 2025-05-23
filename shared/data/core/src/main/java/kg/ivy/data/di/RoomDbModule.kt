package kg.ivy.data.di

import android.content.Context
import kg.ivy.data.db.IvyRoomDatabase
import kg.ivy.data.db.dao.read.AccountDao
import kg.ivy.data.db.dao.read.BudgetDao
import kg.ivy.data.db.dao.read.CategoryDao
import kg.ivy.data.db.dao.read.ExchangeRatesDao
import kg.ivy.data.db.dao.read.LoanDao
import kg.ivy.data.db.dao.read.LoanRecordDao
import kg.ivy.data.db.dao.read.PlannedPaymentRuleDao
import kg.ivy.data.db.dao.read.SettingsDao
import kg.ivy.data.db.dao.read.TagAssociationDao
import kg.ivy.data.db.dao.read.TagDao
import kg.ivy.data.db.dao.read.TransactionDao
import kg.ivy.data.db.dao.read.UserDao
import kg.ivy.data.db.dao.write.WriteAccountDao
import kg.ivy.data.db.dao.write.WriteBudgetDao
import kg.ivy.data.db.dao.write.WriteCategoryDao
import kg.ivy.data.db.dao.write.WriteExchangeRatesDao
import kg.ivy.data.db.dao.write.WriteLoanDao
import kg.ivy.data.db.dao.write.WriteLoanRecordDao
import kg.ivy.data.db.dao.write.WritePlannedPaymentRuleDao
import kg.ivy.data.db.dao.write.WriteSettingsDao
import kg.ivy.data.db.dao.write.WriteTagAssociationDao
import kg.ivy.data.db.dao.write.WriteTagDao
import kg.ivy.data.db.dao.write.WriteTransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomDbModule {

    @Provides
    @Singleton
    fun provideIvyRoomDatabase(
        @ApplicationContext appContext: Context,
    ): IvyRoomDatabase {
        return IvyRoomDatabase.create(
            applicationContext = appContext,
        )
    }

    @Provides
    fun provideUserDao(db: IvyRoomDatabase): UserDao {
        return db.userDao
    }

    @Provides
    fun provideAccountDao(db: IvyRoomDatabase): AccountDao {
        return db.accountDao
    }

    @Provides
    fun provideTransactionDao(db: IvyRoomDatabase): TransactionDao {
        return db.transactionDao
    }

    @Provides
    fun provideCategoryDao(db: IvyRoomDatabase): CategoryDao {
        return db.categoryDao
    }

    @Provides
    fun provideBudgetDao(db: IvyRoomDatabase): BudgetDao {
        return db.budgetDao
    }

    @Provides
    fun provideSettingsDao(db: IvyRoomDatabase): SettingsDao {
        return db.settingsDao
    }

    @Provides
    fun provideLoanDao(db: IvyRoomDatabase): LoanDao {
        return db.loanDao
    }

    @Provides
    fun provideLoanRecordDao(db: IvyRoomDatabase): LoanRecordDao {
        return db.loanRecordDao
    }

    @Provides
    fun providePlannedPaymentRuleDao(db: IvyRoomDatabase): PlannedPaymentRuleDao {
        return db.plannedPaymentRuleDao
    }

    @Provides
    fun provideTagDao(db: IvyRoomDatabase): TagDao {
        return db.tagDao
    }

    @Provides
    fun provideTagAssociationDao(db: IvyRoomDatabase): TagAssociationDao {
        return db.tagAssociationDao
    }

    @Provides
    fun provideExchangeRatesDao(
        roomDatabase: IvyRoomDatabase
    ): ExchangeRatesDao {
        return roomDatabase.exchangeRatesDao
    }

    @Provides
    fun provideWriteAccountDao(db: IvyRoomDatabase): WriteAccountDao {
        return db.writeAccountDao
    }

    @Provides
    fun provideWriteTransactionDao(db: IvyRoomDatabase): WriteTransactionDao {
        return db.writeTransactionDao
    }

    @Provides
    fun provideWriteCategoryDao(db: IvyRoomDatabase): WriteCategoryDao {
        return db.writeCategoryDao
    }

    @Provides
    fun provideWriteBudgetDao(db: IvyRoomDatabase): WriteBudgetDao {
        return db.writeBudgetDao
    }

    @Provides
    fun provideWriteSettingsDao(db: IvyRoomDatabase): WriteSettingsDao {
        return db.writeSettingsDao
    }

    @Provides
    fun provideWriteLoanDao(db: IvyRoomDatabase): WriteLoanDao {
        return db.writeLoanDao
    }

    @Provides
    fun provideWriteLoanRecordDao(db: IvyRoomDatabase): WriteLoanRecordDao {
        return db.writeLoanRecordDao
    }

    @Provides
    fun provideWritePlannedPaymentRuleDao(db: IvyRoomDatabase): WritePlannedPaymentRuleDao {
        return db.writePlannedPaymentRuleDao
    }

    @Provides
    fun provideWriteExchangeRatesDao(db: IvyRoomDatabase): WriteExchangeRatesDao {
        return db.writeExchangeRatesDao
    }

    @Provides
    fun provideWriteTagDao(db: IvyRoomDatabase): WriteTagDao {
        return db.writeTagDao
    }

    @Provides
    fun provideWriteTagAssociationDao(db: IvyRoomDatabase): WriteTagAssociationDao {
        return db.writeTagAssociationDao
    }
}
