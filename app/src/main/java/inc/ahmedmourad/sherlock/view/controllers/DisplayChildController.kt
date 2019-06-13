package inc.ahmedmourad.sherlock.view.controllers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.Controller
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.model.AppChild
import inc.ahmedmourad.sherlock.utils.Formatter
import inc.ahmedmourad.sherlock.utils.setSupportActionBar
import org.parceler.Parcels
import javax.inject.Inject

class DisplayChildController(args: Bundle?) : Controller(args) {

    @BindView(R.id.display_found_toolbar)
    internal lateinit var toolbar: Toolbar

    @BindView(R.id.display_found_picture)
    internal lateinit var pictureImageView: ImageView

    @BindView(R.id.display_name)
    internal lateinit var nameTextView: TextView

    @BindView(R.id.display_age)
    internal lateinit var ageTextView: TextView

    @BindView(R.id.display_gender)
    internal lateinit var genderTextView: TextView

    @BindView(R.id.display_height)
    internal lateinit var heightTextView: TextView

    @BindView(R.id.display_skin)
    internal lateinit var skinTextView: TextView

    @BindView(R.id.display_hair)
    internal lateinit var hairTextView: TextView

    @BindView(R.id.display_location)
    internal lateinit var locationTextView: TextView

    @BindView(R.id.display_notes)
    internal lateinit var notesTextView: TextView

    @Inject
    lateinit var formatter: Formatter

    private lateinit var context: Context

    private lateinit var child: AppChild

    private lateinit var unbinder: Unbinder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        SherlockComponent.Controllers.displayChildComponent.get().inject(this)

        val view = inflater.inflate(R.layout.controller_display_child, container, false)

        unbinder = ButterKnife.bind(this, view)

        context = view.context

        setSupportActionBar(toolbar)

        child = Parcels.unwrap(args.getParcelable(ARG_CHILD))

        populateUi()

        return view
    }

    private fun populateUi() {

        //TODO: maybe always fetch the latest picture (it doesn't actually change, but who knows)
        //TODO: should we inject picasso?
        child.loadImage(pictureImageView)

        val name = formatter.formatName(child.name)
        toolbar.title = name
        nameTextView.text = name

        ageTextView.text = formatter.formatAge(child.appearance.age)

        genderTextView.text = formatter.formatGender(child.appearance.gender)

        heightTextView.text = formatter.formatHeight(child.appearance.height)

        skinTextView.text = formatter.formatSkin(child.appearance.skin)

        hairTextView.text = formatter.formatHair(child.appearance.hair)

        if (child.location.isValid())
            locationTextView.text = formatter.formatLocation(child.location)

        if (child.notes.isNotBlank())
            notesTextView.text = child.notes
    }

    override fun onDestroy() {
        SherlockComponent.Controllers.displayChildComponent.release()
        unbinder.unbind()
        super.onDestroy()
    }

    companion object {

        private const val ARG_CHILD = "dc_c"

        fun newInstance(child: AppChild) = DisplayChildController(Bundle(1).apply {
            putParcelable(ARG_CHILD, Parcels.wrap(child))
        })
    }
}
