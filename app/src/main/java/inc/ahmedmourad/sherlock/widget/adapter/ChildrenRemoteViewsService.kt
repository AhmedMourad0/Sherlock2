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
import inc.ahmedmourad.sherlock.dagger.modules.factories.ChildrenRemoteViewsFactoryFactory
import inc.ahmedmourad.sherlock.domain.model.children.SimpleRetrievedChild
import inc.ahmedmourad.sherlock.model.core.ParcelableWrapper
import inc.ahmedmourad.sherlock.model.core.parcelize
import splitties.init.appCtx
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

internal class ChildrenRemoteViewsService : RemoteViewsService() {

    @Inject
    lateinit var childrenRemoteViewsFactoryFactory: ChildrenRemoteViewsFactoryFactory

    override fun onCreate() {
        super.onCreate()
        SherlockComponent.Widget.childrenRemoteViewsServiceComponent.get().inject(this)
    }

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {

        val hackBundle = requireNotNull(intent.getBundleExtra(EXTRA_HACK_BUNDLE))

        val children = requireNotNull(
                hackBundle.getParcelableArrayList<ParcelableWrapper<SimpleRetrievedChild>>(EXTRA_CHILDREN)
        ).map(ParcelableWrapper<SimpleRetrievedChild>::value)

        val scores = requireNotNull(hackBundle.getIntegerArrayList(EXTRA_SCORES))

        require(children.size == scores.size)

        return childrenRemoteViewsFactoryFactory(
                applicationContext,
                children.zip(scores).map(Pair<SimpleRetrievedChild, Int>::toTuple2)
        )
    }

    override fun onDestroy() {
        SherlockComponent.Widget.childrenRemoteViewsServiceComponent.release()
        super.onDestroy()
    }

    companion object {

        /** This's as ridiculous as it looks, but it's the only way this works */
        const val EXTRA_HACK_BUNDLE = "inc.ahmedmourad.sherlock.external.adapter.extra.HACK_BUNDLE"
        const val EXTRA_CHILDREN = "inc.ahmedmourad.sherlock.external.adapter.extra.CHILDREN"
        const val EXTRA_SCORES = "inc.ahmedmourad.sherlock.external.adapter.extra.SCORES"

        fun create(appWidgetId: Int, results: List<Tuple2<SimpleRetrievedChild, Int>>): Intent {

            val resultsMap = results.toMap()
            val hackBundle = Bundle(2).apply {
                putParcelableArrayList(EXTRA_CHILDREN, ArrayList(resultsMap.keys.map(SimpleRetrievedChild::parcelize)))
                putIntegerArrayList(EXTRA_SCORES, ArrayList(resultsMap.values))
            }

            return Intent(appCtx, ChildrenRemoteViewsService::class.java).also { intent ->
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                intent.data = getUniqueDataUri(appWidgetId)
                intent.putExtra(EXTRA_HACK_BUNDLE, hackBundle)
            }
        }

        private fun getUniqueDataUri(appWidgetId: Int): Uri {
            return Uri.withAppendedPath(Uri.parse("sherlock://widget/id/"), "$appWidgetId${UUID.randomUUID()}")
        }
    }
}
