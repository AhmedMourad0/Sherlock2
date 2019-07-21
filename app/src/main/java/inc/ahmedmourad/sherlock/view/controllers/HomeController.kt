package inc.ahmedmourad.sherlock.view.controllers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import dagger.Lazy
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.AddChildControllerAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.SectionsRecyclerAdapterAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.qualifiers.FindChildrenControllerQualifier
import inc.ahmedmourad.sherlock.model.AppSection
import inc.ahmedmourad.sherlock.utils.setSupportActionBar
import inc.ahmedmourad.sherlock.view.activity.MainActivity
import inc.ahmedmourad.sherlock.view.model.TaggedController
import java.util.*
import javax.inject.Inject

class HomeController : Controller() {

    @BindView(R.id.toolbar)
    internal lateinit var toolbar: Toolbar

    @BindView(R.id.home_recycler)
    internal lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var adapterFactory: SectionsRecyclerAdapterAbstractFactory

    @Inject
    lateinit var addChildControllerFactory: Lazy<AddChildControllerAbstractFactory>

    @Inject
    @FindChildrenControllerQualifier
    lateinit var findChildrenController: Lazy<TaggedController>

    private lateinit var context: Context
    private lateinit var unbinder: Unbinder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        SherlockComponent.Controllers.homeComponent.get().inject(this)

        val view = inflater.inflate(R.layout.controller_home, container, false)

        unbinder = ButterKnife.bind(this, view)

        context = view.context

        setSupportActionBar(toolbar)

        initializeRecyclerView()

        val activity = this.activity

        if (activity != null && activity.intent.hasExtra(MainActivity.EXTRA_DESTINATION_ID)) {

            val destination = activity.intent.getStringExtra(MainActivity.EXTRA_DESTINATION_ID)
                    ?: ""

            checkNotNull(listOf(
                    AddChildController.Companion,
                    DisplayChildController.Companion
            ).firstOrNull {
                it.isDestination(activity.intent.getIntExtra(MainActivity.EXTRA_DESTINATION_ID, MainActivity.INVALID_DESTINATION))
            }?.navigate(router, activity.intent)) {
                "Destination is not supported: $destination!"
            }
        }

        return view
    }

    private fun initializeRecyclerView() {
        recyclerView.adapter = adapterFactory.create(createSectionsList()) {
            if (it == null)
                Toast.makeText(context.applicationContext, R.string.coming_soon, Toast.LENGTH_LONG).show()
            else
                router.pushController(RouterTransaction.with(it.get().controller).tag(it.get().tag))
        }
        recyclerView.layoutManager = GridLayoutManager(context, context.resources.getInteger(R.integer.home_column_count))
        recyclerView.isVerticalScrollBarEnabled = true
    }

    private fun createSectionsList() = ArrayList<AppSection>(4).apply {
        add(AppSection(context.getString(R.string.found_a_child), R.drawable.found_a_child, addChildControllerFactory.get().create()))
        add(AppSection(context.getString(R.string.search), R.drawable.search_child, findChildrenController))
        add(AppSection(context.getString(R.string.coming_soon), R.drawable.coming_soon, null))
        add(AppSection(context.getString(R.string.coming_soon), R.drawable.coming_soon, null))
    }

    override fun onDestroy() {
        unbinder.unbind()
        SherlockComponent.Controllers.homeComponent.release()
        super.onDestroy()
    }

    companion object {

        private const val CONTROLLER_TAG = "inc.ahmedmourad.sherlock.view.controllers.tag.HomeController"

        fun newInstance() = TaggedController(HomeController(), CONTROLLER_TAG)
    }
}
