package inc.ahmedmourad.sherlock.view.dialog

import android.app.Activity
import android.os.Bundle
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import inc.ahmedmourad.sherlock.R

internal class ConductorBottomSheetDialog(activity: Activity) : BottomSheetDialog(activity) {

    @BindView(R.id.conductor_bottom_sheet_controllers_container)
    internal lateinit var container: ChangeHandlerFrameLayout

    private lateinit var router: Router

    private lateinit var unbinder: Unbinder

    init {
        setOwnerActivity(activity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = View.inflate(context, R.layout.dialog_conductor_bottom_sheet, null)

        unbinder = ButterKnife.bind(this, view)

        router = Conductor.attachRouter(checkNotNull(ownerActivity), container, savedInstanceState)

        setContentView(view)
    }

    override fun onStart() {
        super.onStart()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun setController(controller: Controller) {
        router.setRoot(RouterTransaction.with(controller))
    }

    override fun onStop() {
        router.popToRoot()
        super.onStop()
    }

    override fun onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed()
    }
}
