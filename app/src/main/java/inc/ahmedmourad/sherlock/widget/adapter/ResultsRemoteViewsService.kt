package inc.ahmedmourad.sherlock.widget.adapter

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RemoteViewsService
import arrow.core.Tuple2
import arrow.core.toMap
import arrow.core.toTuple2
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.factories.ResultsRemoteViewsFactoryFactory
import inc.ahmedmourad.sherlock.model.children.AppSimpleRetrievedChild
import splitties.init.appCtx
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

internal class ResultsRemoteViewsService : RemoteViewsService() {

    @Inject
    lateinit var resultsRemoteViewsFactoryFactory: ResultsRemoteViewsFactoryFactory

    override fun onCreate() {
        super.onCreate()
        SherlockComponent.Widget.resultsRemoteViewsServiceComponent.get().inject(this)
    }

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {

        val hackBundle = requireNotNull(intent.getBundleExtra(EXTRA_HACK_BUNDLE))

        val children = requireNotNull(hackBundle.getParcelableArrayList<AppSimpleRetrievedChild>(EXTRA_CHILDREN))

        val scores = requireNotNull(hackBundle.getIntegerArrayList(EXTRA_SCORES))

        require(children.size == scores.size)

        return resultsRemoteViewsFactoryFactory(
                applicationContext,
                children.zip(scores).map(Pair<AppSimpleRetrievedChild, Int>::toTuple2)
        )
    }

    override fun onDestroy() {
        SherlockComponent.Widget.resultsRemoteViewsServiceComponent.release()
        super.onDestroy()
    }

    companion object {

        /** This's as ridiculous as it looks, but it's the only way this works */
        const val EXTRA_HACK_BUNDLE = "inc.ahmedmourad.sherlock.external.adapter.extra.HACK_BUNDLE"
        const val EXTRA_CHILDREN = "inc.ahmedmourad.sherlock.external.adapter.extra.CHILDREN"
        const val EXTRA_SCORES = "inc.ahmedmourad.sherlock.external.adapter.extra.SCORES"

        fun create(appWidgetId: Int, results: List<Tuple2<AppSimpleRetrievedChild, Int>>): Intent {
            val resultsMap = results.toMap()
            return Intent(appCtx, ResultsRemoteViewsService::class.java).also { intent ->
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                intent.data = getUniqueDataUri(appWidgetId)
                intent.putExtra(EXTRA_HACK_BUNDLE, Bundle(2).apply {
                    putParcelableArrayList(EXTRA_CHILDREN, ArrayList(resultsMap.keys))
                    putIntegerArrayList(EXTRA_SCORES, ArrayList(resultsMap.values))
                })
            }
        }

        private fun getUniqueDataUri(appWidgetId: Int): Uri {
            return Uri.withAppendedPath(Uri.parse("sherlock://widget/id/"), "$appWidgetId${UUID.randomUUID()}")
        }
    }
}
