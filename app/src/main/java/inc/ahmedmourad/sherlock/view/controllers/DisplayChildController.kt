package inc.ahmedmourad.sherlock.view.controllers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.DisplayChildViewModelFactoryAbstractFactory
import inc.ahmedmourad.sherlock.model.AppChild
import inc.ahmedmourad.sherlock.utils.Formatter
import inc.ahmedmourad.sherlock.utils.setSupportActionBar
import inc.ahmedmourad.sherlock.utils.viewModelProvider
import inc.ahmedmourad.sherlock.view.activity.MainActivity
import inc.ahmedmourad.sherlock.view.model.ExternallyNavigableController
import inc.ahmedmourad.sherlock.view.model.TaggedController
import inc.ahmedmourad.sherlock.viewmodel.model.DisplayChildViewModel
import javax.inject.Inject

//TODO: rename to ChildDetailsController maybe?
class DisplayChildController(args: Bundle) : LifecycleController(args) {

    @BindView(R.id.display_child_toolbar)
    internal lateinit var toolbar: Toolbar

    @BindView(R.id.display_child_picture)
    internal lateinit var pictureImageView: ImageView

    @BindView(R.id.display_child_name)
    internal lateinit var nameTextView: TextView

    @BindView(R.id.display_child_age)
    internal lateinit var ageTextView: TextView

    @BindView(R.id.display_child_gender)
    internal lateinit var genderTextView: TextView

    @BindView(R.id.display_child_height)
    internal lateinit var heightTextView: TextView

    @BindView(R.id.display_child_skin)
    internal lateinit var skinTextView: TextView

    @BindView(R.id.display_child_hair)
    internal lateinit var hairTextView: TextView

    @BindView(R.id.display_child_location)
    internal lateinit var locationTextView: TextView

    @BindView(R.id.display_child_notes)
    internal lateinit var notesTextView: TextView

    @Inject
    lateinit var interactor: Formatter<String>

    @Inject
    lateinit var formatter: Formatter<String>

    @Inject
    lateinit var viewModelFactoryFactory: DisplayChildViewModelFactoryAbstractFactory

    private var viewModel: DisplayChildViewModel? = null

    private lateinit var childId: String

    private lateinit var context: Context

    private lateinit var unbinder: Unbinder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        SherlockComponent.Controllers.displayChildComponent.get().inject(this)

        val view = inflater.inflate(R.layout.controller_display_child, container, false)

        unbinder = ButterKnife.bind(this, view)

        context = view.context

        setSupportActionBar(toolbar)

        childId = requireNotNull(args.getString(ARG_CHILD_ID)) {
            "childId cannot be null!"
        }

        viewModel = viewModelProvider(viewModelFactoryFactory.create(childId))[DisplayChildViewModel::class.java]

        viewModel?.result?.observe(this, Observer {

            val (value) = it

            if (value != null)
                populateUi(value)
            else
                Toast.makeText(context, context.getString(R.string.child_data_deleted), Toast.LENGTH_LONG).show()
        })

        return view
    }

    private fun populateUi(result: Pair<AppChild, Int>) {

        //TODO: should we inject picasso?
        result.first.loadImage(pictureImageView)

        val name = formatter.formatName(result.first.name)
        toolbar.title = name
        nameTextView.text = name

        ageTextView.text = formatter.formatAge(result.first.appearance.age)

        genderTextView.text = formatter.formatGender(result.first.appearance.gender)

        heightTextView.text = formatter.formatHeight(result.first.appearance.height)

        skinTextView.text = formatter.formatSkin(result.first.appearance.skin)

        hairTextView.text = formatter.formatHair(result.first.appearance.hair)

        locationTextView.text = formatter.formatLocation(result.first.location)

        notesTextView.text = formatter.formatNotes(result.first.notes)
    }

    override fun onDestroy() {
        SherlockComponent.Controllers.displayChildComponent.release()
        unbinder.unbind()
        super.onDestroy()
    }

    companion object : ExternallyNavigableController {

        private const val CONTROLLER_TAG = "inc.ahmedmourad.sherlock.view.controllers.tag.DisplayChildController"

        private const val ARG_CHILD_ID = "inc.ahmedmourad.sherlock.view.controllers.arg.CHILD_ID"

        private const val DESTINATION_ID = 3763

        private const val EXTRA_CHILD_ID = "inc.ahmedmourad.sherlock.view.controllers.extra.CHILD_ID"

        fun newInstance(childId: String) = TaggedController(DisplayChildController(Bundle(1).apply {
            putString(ARG_CHILD_ID, childId)
        }), CONTROLLER_TAG)

        fun createIntent(childId: String): Intent {
            return MainActivity.createIntent(DESTINATION_ID).apply {
                putExtra(EXTRA_CHILD_ID, childId)
            }
        }

        override fun isDestination(destinationId: Int): Boolean {
            return destinationId == DESTINATION_ID
        }

        override fun navigate(router: Router, intent: Intent) {

            val taggedController = newInstance(requireNotNull(intent.getStringExtra(EXTRA_CHILD_ID)) {
                "childId is null!"
            })

            router.pushController(RouterTransaction.with(taggedController.controller).tag(taggedController.tag))
        }
    }
}
