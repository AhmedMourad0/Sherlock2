package inc.ahmedmourad.sherlock.view.controllers

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.material.textfield.TextInputEditText
import dagger.Lazy
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.factories.SearchResultsControllerAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.FindChildrenViewModelQualifier
import inc.ahmedmourad.sherlock.defaults.DefaultTextWatcher
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.model.disposable
import inc.ahmedmourad.sherlock.model.AppCoordinates
import inc.ahmedmourad.sherlock.model.AppLocation
import inc.ahmedmourad.sherlock.utils.ColorSelector
import inc.ahmedmourad.sherlock.utils.setSupportActionBar
import inc.ahmedmourad.sherlock.utils.viewModelProvider
import inc.ahmedmourad.sherlock.view.model.TaggedController
import inc.ahmedmourad.sherlock.viewmodel.controllers.FindChildrenViewModel
import timber.log.Timber
import javax.inject.Inject

internal class FindChildrenController : LifecycleController(), View.OnClickListener {

    @BindView(R.id.toolbar)
    internal lateinit var toolbar: Toolbar

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

    @BindView(R.id.find_children_age_number_picker)
    internal lateinit var ageNumberPicker: NumberPicker

    @BindView(R.id.find_children_height_number_picker)
    internal lateinit var heightNumberPicker: NumberPicker

    @BindView(R.id.find_children_location_text_view)
    internal lateinit var locationTextView: TextView

    @BindView(R.id.find_children_location_image_view)
    internal lateinit var locationImageView: ImageView

    @BindView(R.id.find_children_search_button)
    internal lateinit var searchButton: Button

    @Inject
    @field:FindChildrenViewModelQualifier
    lateinit var viewModelFactory: ViewModelProvider.NewInstanceFactory

    @Inject
    lateinit var searchResultsControllerFactory: Lazy<SearchResultsControllerAbstractFactory>

    private lateinit var skinColorSelector: ColorSelector<Skin>
    private lateinit var hairColorSelector: ColorSelector<Hair>

    private lateinit var viewModel: FindChildrenViewModel

    private var internetConnectionDisposable by disposable()

    private lateinit var context: Context

    private lateinit var unbinder: Unbinder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        var time = System.currentTimeMillis()
        SherlockComponent.Controllers.findChildrenComponent.get().inject(this)
        val injection = System.currentTimeMillis() - time

        time = System.currentTimeMillis()
        val view = inflater.inflate(R.layout.controller_find_children, container, false)
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

        setSupportActionBar(toolbar)

        toolbar.title = context.getString(R.string.search)

        viewModel = viewModelProvider(viewModelFactory)[FindChildrenViewModel::class.java]

        arrayOf(::createSkinColorViews,
                ::createHairColorViews,
                ::initializeEditTexts,
                ::initializeGenderRadioGroup,
                ::initializeLocationTextView,
                ::initializeNumberPickers).forEach { it() }

        arrayOf(locationImageView,
                locationTextView,
                skinWhiteView,
                skinWheatView,
                skinDarkView,
                hairBlondView,
                hairBrownView,
                hairDarkView,
                searchButton).forEach { it.setOnClickListener(this) }

        return view
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        internetConnectionDisposable = viewModel.internetConnectivityObserver
                .subscribe(this::handleConnectionStatusChange, Timber::e)
    }

    private fun handleConnectionStatusChange(connected: Boolean) {
        setLocationEnabled(connected)
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

    private fun initializeEditTexts() {

        firstNameEditText.setText(viewModel.firstName.value)
        lastNameEditText.setText(viewModel.lastName.value)

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

    private fun initializeNumberPickers() {
        ageNumberPicker.value = viewModel.age.value
        heightNumberPicker.value = viewModel.height.value
        ageNumberPicker.setOnValueChangedListener { _, _, newVal -> viewModel.age.value = newVal }
        heightNumberPicker.setOnValueChangedListener { _, _, newVal -> viewModel.height.value = newVal }
    }

    private fun initializeLocationTextView() {
        viewModel.location.observe(this, Observer { location ->
            with(location.name) {
                if (this.isNotBlank())
                    locationTextView.text = this
                else
                    locationTextView.setText(R.string.no_location_specified)
            }
        })
    }

    private fun search() {
        val taggedController = searchResultsControllerFactory.get().create(viewModel.toAppChildCriteriaRules())
        router.pushController(RouterTransaction.with(taggedController.controller).tag(taggedController.tag))
    }

    private fun startPlacePicker() {

        checkNotNull(activity)

        try {
            setLocationEnabled(false)
            startActivityForResult(PlacePicker.IntentBuilder().build(activity), PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            setLocationEnabled(true)
            Timber.e(e)
        } catch (e: GooglePlayServicesNotAvailableException) {
            setLocationEnabled(true)
            Timber.e(e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        setLocationEnabled(true)

        if (resultCode != RESULT_OK)
            return

        checkNotNull(data) {
            Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
            "Parameter data is null!"
        }

        when (requestCode) {
            PLACE_PICKER_REQUEST -> handlePlacePickerResult(data)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handlePlacePickerResult(data: Intent) {
        viewModel.location.value = PlacePicker.getPlace(context, data)?.run {
            AppLocation(this.id,
                    this.name.toString(),
                    this.address.toString(),
                    AppCoordinates(this.latLng.latitude, this.latLng.longitude)
            )
        } ?: AppLocation.empty()
    }

    override fun onDetach(view: View) {
        internetConnectionDisposable?.dispose()
        super.onDetach(view)
    }

    override fun onDestroy() {
        internetConnectionDisposable?.dispose()
        SherlockComponent.Controllers.findChildrenComponent.release()
        unbinder.unbind()
        super.onDestroy()
    }

    private fun setLocationEnabled(enabled: Boolean) {
        locationImageView.isEnabled = enabled
        locationTextView.isEnabled = enabled
    }

    override fun onClick(v: View) {

        when (v.id) {

            R.id.skin_white -> skinColorSelector.select(Skin.WHITE)

            R.id.skin_wheat -> skinColorSelector.select(Skin.WHEAT)

            R.id.skin_dark -> skinColorSelector.select(Skin.DARK)

            R.id.hair_blonde -> hairColorSelector.select(Hair.BLONDE)

            R.id.hair_brown -> hairColorSelector.select(Hair.BROWN)

            R.id.hair_dark -> hairColorSelector.select(Hair.DARK)

            R.id.find_children_location_image_view, R.id.find_children_location_text_view -> startPlacePicker()

            R.id.find_children_search_button -> search()
        }
    }

    companion object {

        private const val CONTROLLER_TAG = "inc.ahmedmourad.sherlock.view.controllers.tag.FindChildrenController"

        private const val PLACE_PICKER_REQUEST = 2723

        fun newInstance() = TaggedController(FindChildrenController(), CONTROLLER_TAG)
    }
}
