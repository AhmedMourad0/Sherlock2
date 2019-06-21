package inc.ahmedmourad.sherlock.services

import android.app.IntentService
import android.content.Intent
import dagger.Lazy
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.AddChildInteractorAbstractFactory
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.mapper.AppModelsMapper
import inc.ahmedmourad.sherlock.model.AppPictureChild
import io.reactivex.disposables.Disposable
import splitties.init.appCtx
import javax.inject.Inject

class SherlockIntentService : IntentService("SherlockIntentService") {

    @Inject
    lateinit var addChildInteractor: AddChildInteractorAbstractFactory

    @Inject
    lateinit var bus: Lazy<Bus>

    @Inject
    lateinit var provider: Lazy<Bus.PublishingState.Provider>

    private var disposable: Disposable? = null

    override fun onCreate() {
        super.onCreate()
        SherlockComponent.Services.sherlockServiceComponent.get().inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {

        if (intent == null || intent.action == null) {
            bus.get().state.backgroundState.notify(provider.get().failure())
            return
        }

        when (intent.action) {
            ACTION_PUBLISH_FOUND -> handleActionPublishFound(intent.getParcelableExtra(EXTRA_FOUND))
        }
    }

    private fun handleActionPublishFound(child: AppPictureChild) {
        disposable = addChildInteractor.create(AppModelsMapper.toDomainPictureChild(child))
                .execute()
                .doOnSuccess { disposable?.dispose() }
                .doOnError { disposable?.dispose() }
                .subscribe({ }, { it.printStackTrace() })
    }

    override fun onDestroy() {
        SherlockComponent.Services.sherlockServiceComponent.release()
        super.onDestroy()
    }

    companion object {

        const val ACTION_PUBLISH_FOUND = "inc.ahmedmourad.sherlock.services.action.PUBLISH_FOUND"
        const val EXTRA_FOUND = "inc.ahmedmourad.sherlock.services.extra.FOUND"

        fun create(child: AppPictureChild) = Intent(appCtx, SherlockIntentService::class.java).apply {
            action = ACTION_PUBLISH_FOUND
            putExtra(EXTRA_FOUND, child)
        }
    }
}
