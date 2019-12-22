package inc.ahmedmourad.sherlock.view.controllers

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.jaygoo.widget.RangeSeekBar
import dagger.Lazy
import de.hdodenhof.circleimageview.CircleImageView
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.factories.DisplayChildControllerFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.MainActivityIntentFactory
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.AddChildViewModelQualifier
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.model.disposable
import inc.ahmedmourad.sherlock.mapper.toAppChild
import inc.ahmedmourad.sherlock.model.AppPublishedChild
import inc.ahmedmourad.sherlock.model.AppRetrievedChild
import inc.ahmedmourad.sherlock.utils.defaults.DefaultOnRangeChangedListener
import inc.ahmedmourad.sherlock.utils.defaults.DefaultTextWatcher
import inc.ahmedmourad.sherlock.utils.getImageBitmap
import inc.ahmedmourad.sherlock.utils.pickers.colors.ColorSelector
import inc.ahmedmourad.sherlock.utils.pickers.images.ImagePicker
import inc.ahmedmourad.sherlock.utils.pickers.places.PlacePicker
import inc.ahmedmourad.sherlock.utils.viewModelProvider
import inc.ahmedmourad.sherlock.view.model.ExternallyNavigableController
import inc.ahmedmourad.sherlock.view.model.TaggedController
import inc.ahmedmourad.sherlock.viewmodel.controllers.AddChildViewModel
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

//TODO: maybe never allow publishing until all publishing operations and finished?
//TODO: try moving stuff to the view model, other controllers too
internal class AddChildController(args: Bundle) : LifecycleController(args), View.OnClickListener {

//    @BindView(R.id.toolbar)
//    internal lateinit var toolbar: MaterialToolbar

    @BindView(R.id.skin_white)
    internal lateinit var skinWhiteView: View

    @BindView(R.id.skin_wheat)
    internal lateinit var skinWheatView: View

    @BindView(R.id.skin_dark)
    internal lateinit var skinDarkView: View

    @BindView(R.id.hair_blonde)
    internal lateinit var hairBlondView: View

    @BindView(R.id.hair_brown)
    internal lateinit var hairBrownView: View

    @BindView(R.id.hair_dark)
    internal lateinit var hairDarkView: View

    @BindView(R.id.first_name_edit_text)
    internal lateinit var firstNameEditText: TextInputEditText

    @BindView(R.id.last_name_edit_text)
    internal lateinit var lastNameEditText: TextInputEditText

    @BindView(R.id.gender_radio_group)
    internal lateinit var genderRadioGroup: RadioGroup

    @BindView(R.id.add_child_age_seek_bar)
    internal lateinit var ageSeekBar: RangeSeekBar

    @BindView(R.id.add_child_height_seek_bar)
    internal lateinit var heightSeekBar: RangeSeekBar

    @BindView(R.id.add_child_location_text_view)
    internal lateinit var locationTextView: MaterialTextView

    @BindView(R.id.add_child_location_image_view)
    internal lateinit var locationImageView: ImageView

    @BindView(R.id.add_child_picture_image_view)
    internal lateinit var pictureImageView: CircleImageView

    @BindView(R.id.add_child_picture_text_view)
    internal lateinit var pictureTextView: MaterialTextView

    @BindView(R.id.add_child_notes_edit_text)
    internal lateinit var notesEditText: TextInputEditText

    @BindView(R.id.add_child_publish_button)
    internal lateinit var publishButton: MaterialButton

    @Inject
    @field:AddChildViewModelQualifier
    lateinit var viewModelFactory: ViewModelProvider.NewInstanceFactory

    @Inject
    lateinit var displayChildControllerFactory: Lazy<DisplayChildControllerFactory>

    @Inject
    lateinit var placePicker: PlacePicker

    @Inject
    lateinit var imagePicker: ImagePicker

    private lateinit var skinColorSelector: ColorSelector<Skin>
    private lateinit var hairColorSelector: ColorSelector<Hair>

    private lateinit var viewModel: AddChildViewModel

    private lateinit var context: Context

    private lateinit var unbinder: Unbinder

    private var publishingDisposable by disposable()
    private var internetConnectionDisposable by disposable()
    private var internetConnectivitySingleDisposable by disposable()

    constructor() : this(Bundle(0))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        var time = System.currentTimeMillis()
        SherlockComponent.Controllers.addChildComponent.get().inject(this)
        val injection = System.currentTimeMillis() - time

        time = System.currentTimeMillis()
        val view = inflater.inflate(R.layout.controller_add_child, container, false)
        val inflation = System.currentTimeMillis() - time

        time = System.currentTimeMillis()
        unbinder = ButterKnife.bind(this, view)
        val binding = System.currentTimeMillis() - time

        val result = "Injection: $injection\niInflation: $inflation\nBinding: $binding"

        context = view.context

        Timber.e(result)
        Toast.makeText(
                context.applicationContext,
                result,
                Toast.LENGTH_LONG
        ).show()

//        setSupportActionBar(toolbar)
//
//        toolbar.title = view.context.getString(R.string.found_a_child)

        viewModel = viewModelProvider(viewModelFactory)[AddChildViewModel::class.java]

        val navigationChild = args.getParcelable<AppPublishedChild>(ARG_CHILD)

        if (navigationChild != null)
            handleExternalNavigation(navigationChild)
        else
            setEnabledAndIdle(true)

        arrayOf(::createSkinColorViews,
                ::createHairColorViews,
                ::initializeSeekBars,
                ::initializeEditTexts,
                ::initializeGenderRadioGroup,
                ::initializePictureImageView,
                ::initializeLocationTextView).forEach { it() }

        arrayOf(locationImageView,
                locationTextView,
                pictureImageView,
                publishButton,
                skinWhiteView,
                skinWheatView,
                skinDarkView,
                hairBlondView,
                hairBrownView,
                hairDarkView,
                pictureTextView,
                pictureTextView
        ).forEach { it.setOnClickListener(this) }

        return view
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        // we only handle connection (enabling and disabling internet-dependant
        // views) if publishing isn't underway
        internetConnectionDisposable = viewModel.internetConnectivityObserver
                .subscribe({ (isConnected, publishingStateOption) ->

                    publishingStateOption.fold(ifEmpty = {
                        setEnabledAndIdle(true)
                        handleConnectionStateChange(isConnected)
                    }, ifSome = this@AddChildController::handlePublishingStateValue)

                }, Timber::e)
    }

    private fun publish() {

        publishingDisposable = viewModel.publishingStateObserver
                .subscribe(this::handlePublishingStateValue, Timber::e)

        setEnabledAndIdle(false)
        viewModel.onPublish()
    }

    private fun handleExternalNavigation(navigationChild: AppPublishedChild) {

        viewModel.take(navigationChild)

        //TODO: move all similar to this to view model
        publishingDisposable = viewModel.publishingStateObserver
                .subscribe(this::handlePublishingStateValue) {
                    Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
                    Timber.e(it)
                }
    }

    private fun handlePublishingStateValue(value: Bus.PublishingState<*>) {
        when (value) {
            is Bus.PublishingState.Success -> onPublishedSuccessfully(value.child.toAppChild())
            is Bus.PublishingState.Failure -> onPublishingFailed()
            is Bus.PublishingState.Ongoing -> onPublishingOngoing()
        }
    }

    private fun onPublishedSuccessfully(child: AppRetrievedChild) {
        publishingDisposable?.dispose()
        val taggedController = displayChildControllerFactory.get()(child.simplify())
        router.popCurrentController()
        router.pushController(RouterTransaction.with(taggedController.controller).tag(taggedController.tag))
    }

    private fun onPublishingFailed() {
        publishingDisposable?.dispose()
        setEnabledAndIdle(true)
    }

    private fun onPublishingOngoing() {
        setEnabledAndIdle(false)
    }

    private fun setEnabledAndIdle(enabled: Boolean) {

        //TODO: start loading when false and stop when true
        arrayOf(skinWhiteView,
                skinWheatView,
                skinDarkView,
                hairBlondView,
                hairBrownView,
                hairDarkView,
                firstNameEditText,
                lastNameEditText,
                genderRadioGroup,
                ageSeekBar,
                heightSeekBar,
                locationTextView,
                locationImageView,
                pictureImageView,
                pictureTextView,
                notesEditText,
                publishButton
        ).forEach { it.isEnabled = enabled }

        // we disable internet-dependant views if no longer publishing and there's no internet connection
        internetConnectivitySingleDisposable = viewModel.internetConnectivitySingle
                .map { enabled && !it }
                .filter { it }
                .subscribe({ handleConnectionStateChange(false) }, Timber::e)
    }

    private fun handleConnectionStateChange(connected: Boolean) {
        setLocationEnabled(connected)
        publishButton.isEnabled = connected
    }

    private fun createSkinColorViews() {
        skinColorSelector = ColorSelector(
                ColorSelector.newItem(Skin.WHITE, skinWhiteView, R.color.colorSkinWhite),
                ColorSelector.newItem(Skin.WHEAT, skinWheatView, R.color.colorSkinWheat),
                ColorSelector.newItem(Skin.DARK, skinDarkView, R.color.colorSkinDark),
                default = viewModel.skin.value
        ).apply {
            onSelectionChangeListeners.add { viewModel.skin.value = it }
        }
    }

    private fun createHairColorViews() {
        hairColorSelector = ColorSelector(
                ColorSelector.newItem(Hair.BLONDE, hairBlondView, R.color.colorHairBlonde),
                ColorSelector.newItem(Hair.BROWN, hairBrownView, R.color.colorHairBrown),
                ColorSelector.newItem(Hair.DARK, hairDarkView, R.color.colorHairDark),
                default = viewModel.hair.value
        ).apply {
            onSelectionChangeListeners.add { viewModel.hair.value = it }
        }
    }

    //TODO: consider removing all listeners and using observe and onSaveInstanceState instead
    private fun initializeEditTexts() {

        firstNameEditText.setText(viewModel.firstName.value)
        lastNameEditText.setText(viewModel.lastName.value)
        notesEditText.setText(viewModel.notes.value)

        firstNameEditText.addTextChangedListener(object : DefaultTextWatcher {
            override fun afterTextChanged(s: Editable) {
                viewModel.firstName.value = s.toString()
            }
        })

        lastNameEditText.addTextChangedListener(object : DefaultTextWatcher {
            override fun afterTextChanged(s: Editable) {
                viewModel.lastName.value = s.toString()
            }
        })

        notesEditText.addTextChangedListener(object : DefaultTextWatcher {
            override fun afterTextChanged(s: Editable) {
                viewModel.notes.value = s.toString()
            }
        })
    }

    private fun initializeGenderRadioGroup() {

        genderRadioGroup.check(if (viewModel.gender.value == Gender.MALE)
            R.id.male_radio_button
        else
            R.id.female_radio_button
        )

        genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.gender.value = if (checkedId == R.id.male_radio_button)
                Gender.MALE
            else
                Gender.FEMALE
        }
    }

    private fun initializeSeekBars() {

        ageSeekBar.setIndicatorTextDecimalFormat("##")
        ageSeekBar.setValue(viewModel.startAge.value.toFloat(), viewModel.endAge.value.toFloat())
        ageSeekBar.setOnRangeChangedListener(object : DefaultOnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar, min: Float, max: Float, isFromUser: Boolean) {
                viewModel.startAge.value = min.roundToInt()
                viewModel.endAge.value = max.roundToInt()
            }
        })

        heightSeekBar.setIndicatorTextDecimalFormat("###")
        heightSeekBar.setValue(viewModel.startHeight.value.toFloat(), viewModel.endHeight.value.toFloat())
        heightSeekBar.setOnRangeChangedListener(object : DefaultOnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar?, min: Float, max: Float, isFromUser: Boolean) {
                viewModel.startHeight.value = min.roundToInt()
                viewModel.endHeight.value = max.roundToInt()
            }
        })
    }

    private fun initializePictureImageView() {
        viewModel.picturePath.observe(this, Observer {
            if (it.isNotBlank())
                pictureImageView.setImageBitmap(getImageBitmap(it))
            else
                pictureImageView.setImageResource(R.drawable.placeholder)
        })
    }

    private fun initializeLocationTextView() {
        viewModel.location.observe(this, Observer { locationOption ->
            locationOption.filter {
                it.name.isNotBlank()
            }.fold(ifEmpty = {
                locationTextView.setText(R.string.no_location_specified)
            }, ifSome = {
                locationTextView.text = it.name
            })
        })
    }

    private fun startImagePicker() {
        setPictureEnabled(false)
        imagePicker.start(checkNotNull(activity)) {
            setPictureEnabled(true)
            Timber.e(it)
        }
    }

    //TODO: should only start when connected to the internet, not the only one though
    private fun startPlacePicker() {
        setLocationEnabled(false)
        placePicker.start(checkNotNull(activity)) {
            setLocationEnabled(true)
            Timber.e(it)
        }
    }

    private fun setPictureEnabled(enabled: Boolean) {
        pictureImageView.isEnabled = enabled
        pictureTextView.isEnabled = enabled
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        setLocationEnabled(true)
        setPictureEnabled(true)

        if (resultCode != RESULT_OK)
            return

        checkNotNull(data) {
            Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
            "Parameter data is null!"
        }

        placePicker.handleActivityResult(requestCode, data, viewModel.location::setValue)
        imagePicker.handleActivityResult(requestCode, data, viewModel.picturePath::setValue) {
            Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
            Timber.e(it)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setLocationEnabled(enabled: Boolean) {
        locationImageView.isEnabled = enabled
        locationTextView.isEnabled = enabled
    }

    override fun onDetach(view: View) {
        publishingDisposable?.dispose()
        internetConnectionDisposable?.dispose()
        internetConnectivitySingleDisposable?.dispose()
        super.onDetach(view)
    }

    override fun onDestroy() {
        publishingDisposable?.dispose()
        internetConnectionDisposable?.dispose()
        internetConnectivitySingleDisposable?.dispose()
        unbinder.unbind()
        SherlockComponent.Controllers.addChildComponent.release()
        super.onDestroy()
    }

    override fun onClick(v: View) {

        when (v.id) {

            R.id.skin_white -> skinColorSelector.select(Skin.WHITE)

            R.id.skin_wheat -> skinColorSelector.select(Skin.WHEAT)

            R.id.skin_dark -> skinColorSelector.select(Skin.DARK)

            R.id.hair_blonde -> hairColorSelector.select(Hair.BLONDE)

            R.id.hair_brown -> hairColorSelector.select(Hair.BROWN)

            R.id.hair_dark -> hairColorSelector.select(Hair.DARK)

            R.id.add_child_location_image_view, R.id.add_child_location_text_view -> startPlacePicker()

            R.id.add_child_picture_image_view, R.id.add_child_picture_text_view -> startImagePicker()

            R.id.add_child_publish_button -> publish()
        }
    }

    companion object : ExternallyNavigableController {

        private const val CONTROLLER_TAG = "inc.ahmedmourad.sherlock.view.controllers.tag.AddChildController"

        private const val ARG_CHILD = "inc.ahmedmourad.sherlock.view.controllers.arg.CHILD"

        private const val DESTINATION_ID = 4727

        private const val EXTRA_CHILD = "inc.ahmedmourad.sherlock.view.controllers.extra.CHILD"

        fun newInstance() = TaggedController(AddChildController(), CONTROLLER_TAG)

        private fun newInstance(child: AppPublishedChild) = TaggedController(AddChildController(Bundle(1).apply {
            putParcelable(ARG_CHILD, child)
        }), CONTROLLER_TAG)

        fun createIntent(activityFactory: MainActivityIntentFactory, child: AppPublishedChild): Intent {
            return activityFactory(DESTINATION_ID).apply {
                putExtra(EXTRA_CHILD, child)
            }
        }

        override fun isDestination(destinationId: Int): Boolean {
            return destinationId == DESTINATION_ID
        }

        override fun navigate(router: Router, intent: Intent) {

            val addChildControllerBackstackInstance = router.getControllerWithTag(CONTROLLER_TAG)

            if (addChildControllerBackstackInstance != null)
                router.popController(addChildControllerBackstackInstance)

            val taggedController = newInstance(requireNotNull(intent.getParcelableExtra(EXTRA_CHILD)))

            router.pushController(RouterTransaction.with(taggedController.controller).tag(taggedController.tag))
        }
    }
}
