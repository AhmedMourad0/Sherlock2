package inc.ahmedmourad.sherlock.dagger.modules.app.factories

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RemoteViewsService
import inc.ahmedmourad.sherlock.model.AppUrlChild
import inc.ahmedmourad.sherlock.widget.adapter.ResultsRemoteViewsFactory
import inc.ahmedmourad.sherlock.widget.adapter.ResultsRemoteViewsService
import org.parceler.Parcels
import java.util.*
import kotlin.collections.ArrayList

interface RemoteViewsServiceFactory {
    fun create(context: Context, appWidgetId: Int, results: List<Pair<AppUrlChild, Int>>): Intent
}

class ResultsRemoteViewsServiceFactory : RemoteViewsServiceFactory {

    override fun create(context: Context, appWidgetId: Int, results: List<Pair<AppUrlChild, Int>>): Intent {
        return Intent(context, ResultsRemoteViewsService::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            data = getUniqueDataUri(appWidgetId)
            putExtra(ResultsRemoteViewsService.EXTRA_HACK_BUNDLE, Bundle(2).apply {
                this.putParcelable(ResultsRemoteViewsService.EXTRA_CHILDREN, Parcels.wrap(ArrayList(results.map { it.first })))
                this.putIntegerArrayList(ResultsRemoteViewsService.EXTRA_SCORES, ArrayList(results.map { it.second }))
            })
        }
    }

    private fun getUniqueDataUri(appWidgetId: Int): Uri {
        return Uri.withAppendedPath(Uri.parse("sherlock://widget/id/"), "$appWidgetId${UUID.randomUUID()}")
    }
}

interface RemoteViewsFactoryFactory {
    fun create(context: Context, results: List<Pair<AppUrlChild, Int>>): RemoteViewsService.RemoteViewsFactory
}

class ResultsRemoteViewsFactoryFactory : RemoteViewsFactoryFactory {
    override fun create(context: Context, results: List<Pair<AppUrlChild, Int>>): RemoteViewsService.RemoteViewsFactory {
        return ResultsRemoteViewsFactory(context, results)
    }
}
