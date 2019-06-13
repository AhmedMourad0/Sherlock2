package inc.ahmedmourad.sherlock.widget.adapter

import android.content.Intent
import android.os.Parcelable
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
        val parcelable = intent.getBundleExtra(EXTRA_HACK_BUNDLE)?.getParcelable<Parcelable>(EXTRA_RESULTS)
        return resultsRemoteViewsFactoryFactory.get().create(applicationContext, if (parcelable != null)
            Parcels.unwrap<ArrayList<AppUrlChild>>(parcelable)
        else
            ArrayList()
        )
    }

    override fun onDestroy() {
        SherlockComponent.Widget.resultsRemoteViewsServiceComponent.release()
        super.onDestroy()
    }

    companion object {
        /** This's as ridiculous as it looks, but it's the only way that works */
        const val EXTRA_HACK_BUNDLE = "inc.ahmedmourad.sherlock.external.adapter.extra.HACK_BUNDLE"
        const val EXTRA_RESULTS = "inc.ahmedmourad.sherlock.external.adapter.extra.RESULTS"
    }
}
