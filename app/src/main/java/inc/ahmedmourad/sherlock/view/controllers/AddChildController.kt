package inc.ahmedmourad.sherlock.view.controllers

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
//TODO: Thanks, Google!
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.material.textfield.TextInputEditText
import com.jaygoo.widget.RangeSeekBar
import dagger.Lazy
import de.hdodenhof.circleimageview.CircleImageView
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.DisplayChildControllerAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.qualifiers.AddChildViewModelQualifier
import inc.ahmedmourad.sherlock.defaults.DefaultOnRangeChangedListener
import inc.ahmedmourad.sherlock.defaults.DefaultTextWatcher
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.exceptions.ActivityNotFoundException
import inc.ahmedmourad.sherlock.model.AppCoordinates
import inc.ahmedmourad.sherlock.model.AppLocation
import inc.ahmedmourad.sherlock.utils.ColorSelector
import inc.ahmedmourad.sherlock.utils.getImageBitmap
import inc.ahmedmourad.sherlock.utils.setSupportActionBar
import inc.ahmedmourad.sherlock.utils.viewModelProvider
import inc.ahmedmourad.sherlock.viewmodel.AddChildViewModel
import javax.inject.Inject

class AddChildController : LifecycleController(), View.OnClickListener {

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

    @BindView(R.id.add_child_age_seek_bar)
    internal lateinit var ageSeekBar: RangeSeekBar

    @BindView(R.id.add_child_height_seek_bar)
    internal lateinit var heightSeekBar: RangeSeekBar

    @BindView(R.id.add_child_location_text_view)
    internal lateinit var locationTextView: TextView

    @BindView(R.id.add_child_location_image_view)
    internal lateinit var locationImageView: ImageView

    @BindView(R.id.add_child_picture_image_view)
    internal lateinit var pictureImageView: CircleImageView

    @BindView(R.id.add_child_picture_text_view)
    internal lateinit var pictureTextView: TextView

    @BindView(R.id.add_child_notes_edit_text)
    internal lateinit var notesEditText: TextInputEditText

    @BindView(R.id.add_child_publish_button)
    internal lateinit var publishButton: Button

    @Inject
    @AddChildViewModelQualifier
    lateinit var viewModelFactory: ViewModelProvider.NewInstanceFactory

    @Inject
    lateinit var displayChildControllerFactory: Lazy<DisplayChildControllerAbstractFactory>

    @Inject
    lateinit var bus: Lazy<Bus>

    private lateinit var skinColorSelector: ColorSelector<Skin>
    private lateinit var hairColorSelector: ColorSelector<Hair>

    private lateinit var viewModel: AddChildViewModel

    private lateinit var context: Context

    private lateinit var unbinder: Unbinder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        SherlockComponent.Controllers.addChildComponent.get().inject(this)

        val view = inflater.inflate(R.layout.controller_add_child, container, false)

        unbinder = ButterKnife.bind(this, view)

        context = view.context

        setSupportActionBar(toolbar)

        toolbar.title = view.context.getString(R.string.found_a_child)

        viewModel = viewModelProvider(viewModelFactory)[AddChildViewModel::class.java]

        skinColorSelector = createSkinColorViews()
        hairColorSelector = createHairColorViews()

        listOf(::initializeSeekBars,
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
                pictureTextView).forEach { it.setOnClickListener(this) }

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
                viewModel.startAge.value = Math.round(min)
                viewModel.endAge.value = Math.round(max)
            }
        })

        heightSeekBar.setIndicatorTextDecimalFormat("###")
        heightSeekBar.setValue(viewModel.startHeight.value.toFloat(), viewModel.endHeight.value.toFloat())
        heightSeekBar.setOnRangeChangedListener(object : DefaultOnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar?, min: Float, max: Float, isFromUser: Boolean) {
                viewModel.startHeight.value = Math.round(min)
                viewModel.endHeight.value = Math.round(max)
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
        viewModel.location.observe(this, Observer { location ->
            with(location.name) {
                if (this.isNotBlank())
                    locationTextView.text = this
                else
                    locationTextView.setText(R.string.no_location_specified)
            }
        })
    }

    private fun publish() {
        with(viewModel.toAppPictureChild()) {

            publishButton.isEnabled = false

            viewModel.publish(this)

            router.popToRoot()
            router.pushController(RouterTransaction.with(displayChildControllerFactory.get().create(this)))
        }
    }

    private fun startImagePicker() {

        if (activity == null) {
            bus.get().errors.normalErrors.notify(Bus.NormalError("", ActivityNotFoundException("Activity is null!"))) //TODO: message
            return
        }

        setPictureEnabled(false)

        startActivityForResult(ImagePicker.create(activity)
                .returnMode(ReturnMode.ALL)
                .folderMode(true)
                .toolbarFolderTitle(context.getString(R.string.pick_a_folder))
                .toolbarImageTitle(context.getString(R.string.tap_to_select))
                .single()
                .limit(1)
                .showCamera(true)
                .imageDirectory(context.getString(R.string.camera))
                .theme(R.style.ImagePickerTheme)
                .enableLog(true)
                .getIntent(context), IMAGE_PICKER_REQUEST
        )
    }

    //TODO: should only start when connected to the internet, not the only one though
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

    private fun setPictureEnabled(enabled: Boolean) {
        pictureImageView.isEnabled = enabled
        pictureTextView.isEnabled = enabled
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        setLocationEnabled(true)
        setPictureEnabled(true)

        if (resultCode != RESULT_OK)
            return

        if (data == null) {
            bus.get().errors.normalErrors.notify(Bus.NormalError("", IllegalArgumentException("Parameter data is null!"))) //TODO: message
            return
        }

        when (requestCode) {

            IMAGE_PICKER_REQUEST -> {
                ImagePicker.getFirstImageOrNull(data)?.also {
                    viewModel.picturePath.value = it.path
                }
            }

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

    private fun setLocationEnabled(enabled: Boolean) {
        locationImageView.isEnabled = enabled
        locationTextView.isEnabled = enabled
    }

    override fun onDestroy() {
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

    companion object {

        private const val PLACE_PICKER_REQUEST = 7424
        private const val IMAGE_PICKER_REQUEST = 4287

        fun newInstance() = AddChildController()
    }
}
