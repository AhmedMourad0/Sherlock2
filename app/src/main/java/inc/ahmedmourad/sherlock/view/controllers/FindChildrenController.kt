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
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.FindChildrenViewModelFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.SearchResultsControllerFactory
import inc.ahmedmourad.sherlock.defaults.DefaultTextWatcher
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.exceptions.ActivityNotFoundException
import inc.ahmedmourad.sherlock.model.AppCoordinates
import inc.ahmedmourad.sherlock.model.AppLocation
import inc.ahmedmourad.sherlock.utils.ColorSelector
import inc.ahmedmourad.sherlock.utils.setSupportActionBar
import inc.ahmedmourad.sherlock.utils.viewModelProvider
import inc.ahmedmourad.sherlock.viewmodel.FindChildrenViewModel
import javax.inject.Inject

class FindChildrenController : LifecycleController(), View.OnClickListener {

    @BindView(R.id.toolbar)
    internal lateinit var toolbar: Toolbar

    @BindView(R.id.found_skin_white)
    internal lateinit var skinWhiteView: View

    @BindView(R.id.found_skin_wheat)
    internal lateinit var skinWheatView: View

    @BindView(R.id.found_skin_dark)
    internal lateinit var skinDarkView: View

    @BindView(R.id.found_hair_blonde)
    internal lateinit var hairBlondView: View

    @BindView(R.id.found_hair_brown)
    internal lateinit var hairBrownView: View

    @BindView(R.id.found_hair_dark)
    internal lateinit var hairDarkView: View

    @BindView(R.id.found_first_name_edit_text)
    internal lateinit var firstNameEditText: TextInputEditText

    @BindView(R.id.found_last_name_edit_text)
    internal lateinit var lastNameEditText: TextInputEditText

    @BindView(R.id.found_gender_radio_group)
    internal lateinit var genderRadioGroup: RadioGroup

    @BindView(R.id.search_found_age_number_picker)
    internal lateinit var ageNumberPicker: NumberPicker

    @BindView(R.id.search_found_height_number_picker)
    internal lateinit var heightNumberPicker: NumberPicker

    @BindView(R.id.search_found_location_text_view)
    internal lateinit var locationTextView: TextView

    @BindView(R.id.search_found_location_image_view)
    internal lateinit var locationImageView: ImageView

    @BindView(R.id.search_found_search_button)
    internal lateinit var searchButton: Button

    @Inject
    lateinit var viewModelFactory: FindChildrenViewModelFactory

    @Inject
    lateinit var searchResultsControllerFactory: Lazy<SearchResultsControllerFactory>

    @Inject
    lateinit var bus: Lazy<Bus>

    private lateinit var skinColorSelector: ColorSelector<Skin>
    private lateinit var hairColorSelector: ColorSelector<Hair>

    private lateinit var viewModel: FindChildrenViewModel

    private lateinit var context: Context

    private lateinit var unbinder: Unbinder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        SherlockComponent.Controllers.findChildrenComponent.get().inject(this)

        val view = inflater.inflate(R.layout.controller_find_children, container, false)

        unbinder = ButterKnife.bind(this, view)

        context = view.context

        setSupportActionBar(toolbar)

        toolbar.title = context.getString(R.string.search)

        viewModel = viewModelProvider(viewModelFactory)[FindChildrenViewModel::class.java]

        skinColorSelector = createSkinColorViews()
        hairColorSelector = createHairColorViews()

        listOf(::initializeEditTexts,
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

    private fun createSkinColorViews() = ColorSelector(
            ColorSelector.newItem(Skin.WHITE, skinWhiteView, R.color.colorSkinWhite),
            ColorSelector.newItem(Skin.WHEAT, skinWheatView, R.color.colorSkinWheat),
            ColorSelector.newItem(Skin.DARK, skinDarkView, R.color.colorSkinDark),
            default = viewModel.skin.value
    ).apply {
        onSelectionChangeListeners.add { viewModel.skin.value = it }
    }

    private fun createHairColorViews() = ColorSelector(
            ColorSelector.newItem(Hair.BLONDE, hairBlondView, R.color.colorHairBlonde),
            ColorSelector.newItem(Hair.BROWN, hairBrownView, R.color.colorHairBrown),
            ColorSelector.newItem(Hair.DARK, hairDarkView, R.color.colorHairDark),
            default = viewModel.hair.value
    ).apply {
        onSelectionChangeListeners.add { viewModel.hair.value = it }
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
            R.id.found_male_radio_button
        else
            R.id.found_female_radio_button
        )

        genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.gender.value = if (checkedId == R.id.found_male_radio_button)
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
        router.pushController(RouterTransaction.with(searchResultsControllerFactory.get().create(viewModel.toAppChildCriteriaRules())))
    }

    private fun startPlacePicker() {

        if (activity == null) {
            bus.get().errors.normalErrors.notify(Bus.NormalError("", ActivityNotFoundException("Activity is null!"))) //TODO: message
            return
        }

        try {
            setLocationEnabled(false)
            startActivityForResult(PlacePicker.IntentBuilder().build(activity!!), PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            setLocationEnabled(true)
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            setLocationEnabled(true)
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        setLocationEnabled(true)

        if (resultCode != RESULT_OK)
            return

        if (data == null) {
            bus.get().errors.normalErrors.notify(Bus.NormalError("", IllegalArgumentException("Parameter data is null!"))) //TODO: message
            return
        }

        when (requestCode) {
            PLACE_PICKER_REQUEST -> {
                viewModel.location.value = PlacePicker.getPlace(context, data)?.run {
                    AppLocation(this.id,
                            this.name.toString(),
                            this.address.toString(),
                            AppCoordinates(this.latLng.latitude, this.latLng.longitude)
                    )
                } ?: AppLocation.empty()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
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

            R.id.found_skin_white -> skinColorSelector.select(Skin.WHITE)

            R.id.found_skin_wheat -> skinColorSelector.select(Skin.WHEAT)

            R.id.found_skin_dark -> skinColorSelector.select(Skin.DARK)

            R.id.found_hair_blonde -> hairColorSelector.select(Hair.BLONDE)

            R.id.found_hair_brown -> hairColorSelector.select(Hair.BROWN)

            R.id.found_hair_dark -> hairColorSelector.select(Hair.DARK)

            R.id.search_found_location_image_view, R.id.search_found_location_text_view -> startPlacePicker()

            R.id.search_found_search_button -> search()
        }
    }

    companion object {

        private const val PLACE_PICKER_REQUEST = 2723

        fun newInstance() = FindChildrenController()
    }
}
