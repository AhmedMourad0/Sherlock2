package inc.ahmedmourad.sherlock.view.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.ChangeHandlerFrameLayout
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.github.florent37.shapeofview.shapes.CutCornerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import dagger.Lazy
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.factories.AddChildControllerFactory
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.HomeControllerQualifier
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.MainActivityViewModelQualifier
import inc.ahmedmourad.sherlock.domain.model.disposable
import inc.ahmedmourad.sherlock.model.Connectivity
import inc.ahmedmourad.sherlock.utils.hideSoftKeyboard
import inc.ahmedmourad.sherlock.view.model.TaggedController
import inc.ahmedmourad.sherlock.viewmodel.activity.MainActivityViewModel
import splitties.init.appCtx
import timber.log.Timber
import javax.inject.Inject

//TODO: use Kotlin's extensions instead of ButterKnife
internal class MainActivity : AppCompatActivity() {

    @BindView(R.id.main_content_root)
    internal lateinit var contentRoot: CutCornerView

    @BindView(R.id.main_content_controllers_container)
    internal lateinit var contentControllersContainer: ChangeHandlerFrameLayout

    @BindView(R.id.main_content_overlay)
    internal lateinit var contentOverlay: View

    @BindView(R.id.main_backdrop_controllers_container)
    internal lateinit var backdropControllersContainer: ChangeHandlerFrameLayout

    @BindView(R.id.main_backdrop_scroll_view)
    internal lateinit var backdropScrollView: NestedScrollView

    @BindView(R.id.main_toolbar)
    internal lateinit var toolbar: MaterialToolbar

    @BindView(R.id.main_appbar)
    internal lateinit var appbar: AppBarLayout

    @BindView(R.id.main_dummy_view)
    internal lateinit var dummyView: View

    @Inject
    @field:MainActivityViewModelQualifier
    lateinit var viewModelFactory: ViewModelProvider.NewInstanceFactory

    @Inject
    @field:HomeControllerQualifier
    lateinit var homeController: Lazy<TaggedController>

    @Inject
    lateinit var signUpController: AddChildControllerFactory

    private var isContentShown = true

    private val foregroundAnimator by lazy(::createForegroundAnimator)

    private lateinit var viewModel: MainActivityViewModel

    private var internetConnectivityDisposable by disposable()

    private lateinit var foregroundRouter: Router

    private lateinit var backdropRouter: Router

    private lateinit var unbinder: Unbinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SherlockComponent.Activities.mainComponent.get().inject(this)

        unbinder = ButterKnife.bind(this)

        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[MainActivityViewModel::class.java]

        foregroundRouter = Conductor.attachRouter(this, contentControllersContainer, savedInstanceState)

        if (!foregroundRouter.hasRootController())
            foregroundRouter.setRoot(RouterTransaction.with(homeController.get().controller).tag(homeController.get().tag))

        backdropRouter = Conductor.attachRouter(this, backdropControllersContainer, savedInstanceState)

        val taggedController = signUpController()
        if (!backdropRouter.hasRootController())
            backdropRouter.setRoot(RouterTransaction.with(taggedController.get().controller).tag(taggedController.get().tag))

        dummyView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                v.post(this@MainActivity::hideSoftKeyboard)
        }

        dummyView.requestFocusFromTouch()

        backdropScrollView.post {
            backdropScrollView.updatePadding(bottom = contentRoot.height / 6)
        }
    }

    override fun onStart() {
        super.onStart()
        dummyView.requestFocusFromTouch()
        internetConnectivityDisposable = viewModel.internetConnectivityObserver
                .doOnSubscribe { showConnectivitySnackBar(Connectivity.CONNECTING) }
                .subscribe(this::showConnectivitySnackBar, Timber::e)
    }

    private fun showConnectivitySnackBar(connectivity: Connectivity) {

        val duration = if (connectivity.isIndefinite)
            Snackbar.LENGTH_INDEFINITE
        else
            Snackbar.LENGTH_SHORT

        Snackbar.make(contentControllersContainer, connectivity.message, duration).apply {

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

    private fun createForegroundAnimator(): ValueAnimator {

        val animation = ValueAnimator.ofFloat(
                0f,
                1f
        ).apply {
            this.duration = 700
            this.interpolator = FastOutSlowInInterpolator()
        }

        animation.addUpdateListener {

            val animatedValue = it.animatedValue as Float

            contentRoot.translationY =
                    animatedValue * (contentRoot.height - contentRoot.height / 6f)

            appbar.elevation =
                    animatedValue * resources.getDimensionPixelSize(R.dimen.defaultAppBarElevation).toFloat()

            backdropScrollView.translationY =
                    (1 - animatedValue) * resources.getDimensionPixelSize(R.dimen.backdropTranslationY).toFloat()

            contentOverlay.alpha =
                    animatedValue * 0.4f
        }

        animation.doOnEnd {
            invalidateOptionsMenu()
            contentOverlay.visibility = if (isContentShown) View.GONE else View.VISIBLE
            contentControllersContainer.isEnabled = isContentShown
        }

        return animation
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        val isSignedIn = false

        if (isContentShown) {
            if (isSignedIn) {
                menu?.findItem(R.id.main_menu_show_or_hide_backdrop)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_gender)
            } else {
                menu?.findItem(R.id.main_menu_show_or_hide_backdrop)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_location)
            }
        } else {
            menu?.findItem(R.id.main_menu_show_or_hide_backdrop)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_age)
        }

        if (!isSignedIn)
            menu?.removeItem(R.id.main_menu_sign_out)

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.main_menu_show_or_hide_backdrop -> {
                showOrHideBackdrop()
                true
            }
            R.id.main_menu_sign_out -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showOrHideBackdrop() {
        isContentShown = !isContentShown
        invalidateOptionsMenu()
        contentControllersContainer.isEnabled = false
        contentOverlay.visibility = View.VISIBLE
        dummyView.requestFocusFromTouch()
        when {
            foregroundAnimator.isStarted -> foregroundAnimator.reverse()
            isContentShown -> foregroundAnimator.reverse()
            else -> foregroundAnimator.start()
        }
    }

    private fun signOut() {
        invalidateOptionsMenu()
    }

    override fun onStop() {
        if (foregroundAnimator.isStarted)
            foregroundAnimator.end()
        internetConnectivityDisposable?.dispose()
        super.onStop()
    }

    override fun onBackPressed() {

        if (foregroundAnimator.isRunning)
            return

        if (isContentShown) {
            if (!foregroundRouter.handleBack())
                super.onBackPressed()

        } else {
            if (!backdropRouter.handleBack())
                showOrHideBackdrop()
        }
    }

    override fun onDestroy() {
        SherlockComponent.Activities.mainComponent.release()
        foregroundAnimator.cancel()
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
