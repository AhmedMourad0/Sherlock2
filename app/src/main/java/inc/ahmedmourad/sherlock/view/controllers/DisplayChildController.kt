package inc.ahmedmourad.sherlock.view.controllers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import arrow.core.Tuple2
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.factories.DisplayChildViewModelFactoryFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.MainActivityIntentFactory
import inc.ahmedmourad.sherlock.domain.model.disposable
import inc.ahmedmourad.sherlock.model.AppRetrievedChild
import inc.ahmedmourad.sherlock.model.AppSimpleRetrievedChild
import inc.ahmedmourad.sherlock.utils.formatter.Formatter
import inc.ahmedmourad.sherlock.utils.setSupportActionBar
import inc.ahmedmourad.sherlock.utils.viewModelProvider
import inc.ahmedmourad.sherlock.view.model.ExternallyNavigableController
import inc.ahmedmourad.sherlock.view.model.TaggedController
import inc.ahmedmourad.sherlock.viewmodel.controllers.DisplayChildViewModel
import timber.log.Timber
import javax.inject.Inject

//TODO: rename to ChildDetailsController maybe?
internal class DisplayChildController(args: Bundle) : LifecycleController(args) {

    @BindView(R.id.display_child_toolbar)
    internal lateinit var toolbar: MaterialToolbar

    @BindView(R.id.display_child_picture)
    internal lateinit var pictureImageView: ImageView

    @BindView(R.id.display_child_name)
    internal lateinit var nameTextView: MaterialTextView

    @BindView(R.id.display_child_age)
    internal lateinit var ageTextView: MaterialTextView

    @BindView(R.id.display_child_gender)
    internal lateinit var genderTextView: MaterialTextView

    @BindView(R.id.display_child_height)
    internal lateinit var heightTextView: MaterialTextView

    @BindView(R.id.display_child_skin)
    internal lateinit var skinTextView: MaterialTextView

    @BindView(R.id.display_child_hair)
    internal lateinit var hairTextView: MaterialTextView

    @BindView(R.id.display_child_location)
    internal lateinit var locationTextView: MaterialTextView

    @BindView(R.id.display_child_notes)
    internal lateinit var notesTextView: MaterialTextView

    @Inject
    lateinit var formatter: Formatter

    @Inject
    lateinit var viewModelFactoryFactory: DisplayChildViewModelFactoryFactory

    private lateinit var viewModel: DisplayChildViewModel

    private lateinit var context: Context

    private var findChildDisposable by disposable()

    private lateinit var unbinder: Unbinder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        SherlockComponent.Controllers.displayChildComponent.get().inject(this)

        val view = inflater.inflate(R.layout.controller_display_child, container, false)

        unbinder = ButterKnife.bind(this, view)

        context = view.context

        setSupportActionBar(toolbar)

        val child = requireNotNull(args.getParcelable<AppSimpleRetrievedChild>(ARG_CHILD))

        viewModel = viewModelProvider(viewModelFactoryFactory(child))[DisplayChildViewModel::class.java]

        return view
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        //TODO: notify the user when the data is updated
        findChildDisposable = viewModel.result.subscribe({ resultEither ->
            resultEither.fold(ifLeft = {
                Timber.e(it)
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                router.popCurrentController()
            }, ifRight = { resultOption ->
                resultOption.fold(ifEmpty = {
                    Toast.makeText(context, context.getString(R.string.child_data_deleted), Toast.LENGTH_LONG).show()
                    router.popCurrentController()
                }, ifSome = this@DisplayChildController::populateUi)
            })
        }, {
            Timber.e(it)
            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
        })
    }

    override fun onDetach(view: View) {
        findChildDisposable?.dispose()
        super.onDetach(view)
    }

    private fun populateUi(result: Tuple2<AppRetrievedChild, Int>) {

        //TODO: should we inject picasso/glide?
        result.a.loadImage(pictureImageView)

        val name = formatter.formatName(result.a.name)
        toolbar.title = name
        nameTextView.text = name

        ageTextView.text = formatter.formatAge(result.a.appearance.age)

        genderTextView.text = formatter.formatGender(result.a.appearance.gender)

        heightTextView.text = formatter.formatHeight(result.a.appearance.height)

        skinTextView.text = formatter.formatSkin(result.a.appearance.skin)

        hairTextView.text = formatter.formatHair(result.a.appearance.hair)

        locationTextView.text = formatter.formatLocation(result.a.location)

        notesTextView.text = formatter.formatNotes(result.a.notes)
    }

    override fun onDestroy() {
        SherlockComponent.Controllers.displayChildComponent.release()
        findChildDisposable?.dispose()
        unbinder.unbind()
        super.onDestroy()
    }

    companion object : ExternallyNavigableController {

        private const val CONTROLLER_TAG = "inc.ahmedmourad.sherlock.view.controllers.tag.DisplayChildController"

        private const val ARG_CHILD = "inc.ahmedmourad.sherlock.view.controllers.arg.CHILD"

        private const val DESTINATION_ID = 3763

        private const val EXTRA_CHILD = "inc.ahmedmourad.sherlock.view.controllers.extra.CHILD"

        fun newInstance(child: AppSimpleRetrievedChild) = TaggedController(DisplayChildController(Bundle(1).apply {
            putParcelable(ARG_CHILD, child)
        }), CONTROLLER_TAG)

        fun createIntent(activityFactory: MainActivityIntentFactory, child: AppSimpleRetrievedChild): Intent {
            return activityFactory(DESTINATION_ID).apply {
                putExtra(EXTRA_CHILD, child)
            }
        }

        override fun isDestination(destinationId: Int): Boolean {
            return destinationId == DESTINATION_ID
        }

        override fun navigate(router: Router, intent: Intent) {

            val taggedController = newInstance(requireNotNull(intent.getParcelableExtra(EXTRA_CHILD)))

            router.pushController(RouterTransaction.with(taggedController.controller).tag(taggedController.tag))
        }
    }
}
