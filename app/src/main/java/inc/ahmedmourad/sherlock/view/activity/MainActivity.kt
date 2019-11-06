package inc.ahmedmourad.sherlock.view.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.*
import dagger.Lazy
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.HomeControllerQualifier
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.view.model.TaggedController
import splitties.init.appCtx
import javax.inject.Inject
import inc.ahmedmourad.sherlock.model.Connectivity
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.MainActivityViewModelQualifier
import inc.ahmedmourad.sherlock.domain.model.disposable
import inc.ahmedmourad.sherlock.view.controllers.AddChildController
import inc.ahmedmourad.sherlock.view.dialog.ConductorBottomSheetDialog
import inc.ahmedmourad.sherlock.viewmodel.activity.MainActivityViewModel
import timber.log.Timber

//TODO: use Anko's Dsl for all of our layouts
//TODO: use Kotlin's extensions instead of ButterKnife
internal class MainActivity : AppCompatActivity() {

    @BindView(R.id.main_controllers_container)
    internal lateinit var container: ChangeHandlerFrameLayout

    @Inject
    @field:MainActivityViewModelQualifier
    lateinit var viewModelFactory: ViewModelProvider.NewInstanceFactory

    @Inject
    @field:HomeControllerQualifier
    lateinit var homeController: Lazy<TaggedController<Controller>>

    @Inject
    lateinit var bus: Bus

    private val bottomSheet by lazy { ConductorBottomSheetDialog(this) }

    private lateinit var viewModel: MainActivityViewModel

    private var internetConnectivityDisposable by disposable()

    private lateinit var router: Router

    private lateinit var unbinder: Unbinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SherlockComponent.Activities.mainComponent.get().inject(this)

        unbinder = ButterKnife.bind(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[MainActivityViewModel::class.java]

        router = Conductor.attachRouter(this, container, savedInstanceState)

        if (!router.hasRootController())
            router.setRoot(RouterTransaction.with(homeController.get().controller).tag(homeController.get().tag))
    }

    override fun onStart() {
        super.onStart()
        internetConnectivityDisposable = viewModel.internetConnectivityObserver
                .doOnSubscribe { showConnectivitySnackBar(Connectivity.CONNECTING) }
                .subscribe(this::showConnectivitySnackBar, Timber::e)

//        showBottomSheet(AddChildController.newInstance().controller)
    }

    fun showBottomSheet(controller: Controller) {
        bottomSheet.setController(controller)
        bottomSheet.show()
    }

    fun hideBottomSheet() {
        bottomSheet.hide()
    }

    fun dismissBottomSheet() {
        bottomSheet.hide()
    }

    private fun showConnectivitySnackBar(connectivity: Connectivity) {

        val duration = if (connectivity.isIndefinite) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_SHORT

        Snackbar.make(container, connectivity.message, duration).apply {

            val snackBarView = view.apply {
                setBackgroundColor(ContextCompat.getColor(this@MainActivity, connectivity.color))
            }

            (snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                else
                    gravity = Gravity.CENTER
            }

        }.show()
    }

    override fun onStop() {
        internetConnectivityDisposable?.dispose()
        super.onStop()
    }

    override fun onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed()
    }

    override fun onDestroy() {
        SherlockComponent.Activities.mainComponent.release()
        internetConnectivityDisposable?.dispose()
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
