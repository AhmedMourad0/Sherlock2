package inc.ahmedmourad.sherlock.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.*
import com.google.android.material.snackbar.Snackbar
import dagger.Lazy
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.app.qualifiers.HomeControllerQualifier
import inc.ahmedmourad.sherlock.domain.bus.Bus
import io.reactivex.disposables.Disposable
import javax.inject.Inject

//TODO: use Anko's Dsl for all of our layouts
class MainActivity : AppCompatActivity() {

    @BindView(R.id.controller_container)
    internal lateinit var container: ChangeHandlerFrameLayout

    @Inject
    @HomeControllerQualifier
    lateinit var homeController: Lazy<Controller>

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
            router.setRoot(RouterTransaction.with(homeController.get()))
    }

    override fun onStart() {
        super.onStart()
        disposable = bus.state.backgroundState.get().subscribe({

            Snackbar.make(container,
                    it.message,
                    if (it.isIndefinite) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_LONG
            ).show()

        }, { bus.errors.normalErrors.notify(Bus.NormalError("", it)) }) //TODO: message
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
}
