package inc.ahmedmourad.sherlock.widget.adapter

import android.content.Intent
import android.widget.RemoteViewsService
import dagger.Lazy
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.RemoteViewsFactoryFactory
import inc.ahmedmourad.sherlock.model.AppUrlChild
import org.parceler.Parcels
import javax.inject.Inject

class ResultsRemoteViewsService : RemoteViewsService() {

    @Inject
    lateinit var resultsRemoteViewsFactoryFactory: Lazy<RemoteViewsFactoryFactory>

    override fun onCreate() {
        super.onCreate()
        SherlockComponent.Widget.resultsRemoteViewsServiceComponent.get().inject(this)
    }

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {

        val hackBundle = intent.getBundleExtra(EXTRA_HACK_BUNDLE)
                ?: throw IllegalArgumentException("Hack Bundle cannot be null!")

        val children = Parcels.unwrap<ArrayList<AppUrlChild>>(hackBundle.getParcelable(EXTRA_CHILDREN)
                ?: throw IllegalArgumentException("Children list cannot be null!"))

        val scores = intent.getIntegerArrayListExtra(EXTRA_SCORES)
                ?: throw IllegalArgumentException("Scores list cannot be null!")

        if (children.size != scores.size)
            throw IllegalArgumentException("Children list and Scores list must be of the same size!")

        return resultsRemoteViewsFactoryFactory.get().create(applicationContext, children.zip(scores))
    }

    override fun onDestroy() {
        SherlockComponent.Widget.resultsRemoteViewsServiceComponent.release()
        super.onDestroy()
    }

    companion object {
        /** This's as ridiculous as it looks, but it's the only way that works */
        const val EXTRA_HACK_BUNDLE = "inc.ahmedmourad.sherlock.external.adapter.extra.HACK_BUNDLE"
        const val EXTRA_CHILDREN = "inc.ahmedmourad.sherlock.external.adapter.extra.CHILDREN"
        const val EXTRA_SCORES = "inc.ahmedmourad.sherlock.external.adapter.extra.SCORES"
    }
}
