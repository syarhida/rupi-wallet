package kg.ivy.widget.transaction

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.annotation.IdRes
import kg.ivy.base.model.TransactionType
import kg.ivy.domain.AppStarter
import javax.inject.Inject

class AddTransactionWidgetClick @Inject constructor(
    private val appStarter: AppStarter
) {
    companion object {
        const val ACTION_ADD_INCOME = "kg.ivy.wallet.ACTION_ADD_INCOME"
        const val ACTION_ADD_EXPENSE = "kg.ivy.wallet.ACTION_ADD_EXPENSE"
        const val ACTION_ADD_TRANSFER = "kg.ivy.wallet.ACTION_ADD_TRANSFER"
    }

    // ============================= <HANDLE> =======================================================
    fun handleClick(intent: Intent) {
        when (intent.action) {
            ACTION_ADD_INCOME -> {
                appStarter.addTransactionStart(TransactionType.INCOME)
            }

            ACTION_ADD_EXPENSE -> {
                appStarter.addTransactionStart(TransactionType.EXPENSE)
            }

            ACTION_ADD_TRANSFER -> {
                appStarter.addTransactionStart(TransactionType.TRANSFER)
            }

            else -> return
        }
    }

    // ============================= <HANDLE> =======================================================
    // ------------------------------ <SETUP> -------------------------------------------------------
    class Setup(
        private val context: Context,
        private val rv: RemoteViews,
        private val appWidgetId: Int
    ) {
        fun clickListener(@IdRes viewId: Int, action: String) {
            val actionIntent = newActionIntent(context, appWidgetId, action)
            rv.setOnClickPendingIntent(viewId, actionIntent)
        }

        private fun newActionIntent(
            context: Context,
            appWidgetId: Int,
            action: String
        ): PendingIntent {
            val intent = Intent(context, AddTransactionWidget::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.action = action
            return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
    } // ----------------------------- </SETUP> -------------------------------------------------------
}
