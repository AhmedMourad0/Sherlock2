package inc.ahmedmourad.sherlock.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.ChangeHandlerFrameLayout
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import dagger.Lazy
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.app.qualifiers.HomeControllerQualifier
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.view.model.TaggedController
import io.reactivex.disposables.Disposable
import splitties.init.appCtx
import javax.inject.Inject

//TODO: use Anko's Dsl for all of our layouts
//TODO: use Kotlin's extensions instead of ButterKnife
class MainActivity : AppCompatActivity() {

    @BindView(R.id.main_controllers_container)
    internal lateinit var container: ChangeHandlerFrameLayout

    @Inject
    @HomeControllerQualifier
    lateinit var homeController: Lazy<TaggedController>

    @Inject
    lateinit var bus: Bus

    private lateinit var disposable: Disposable

    private lateinit var router: Router

    private lateinit var unbinder: Unbinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SherlockComponent.Activities.mainComponent.get().inject(this)

        unbinder = ButterKnife.bind(this)

        router = Conductor.attachRouter(this, container, savedInstanceState)

        if (!router.hasRootController())
            router.setRoot(RouterTransaction.with(homeController.get().controller).tag(homeController.get().tag))
    }

    override fun onStop() {
        disposable.dispose()
        super.onStop()
    }

    override fun onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed()
    }

    override fun onDestroy() {
        SherlockComponent.Activities.mainComponent.release()
        unbinder.unbind()
        super.onDestroy()
    }

    companion object {

        const val EXTRA_DESTINATION_ID = "inc.ahmedmourad.sherlock.view.activities.extra.DESTINATION_ID"
        const val INVALID_DESTINATION = -1

        fun createIntent(destinationId: Int): Intent {
            return Intent(appCtx, MainActivity::class.java).apply {
                putExtra(EXTRA_DESTINATION_ID, destinationId)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
        }
    }
}
