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
import javax.inject.Inject

class DisplayChildController(args: Bundle) : Controller(args) {

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
    lateinit var formatter: Formatter<String>

    private lateinit var context: Context

    private lateinit var result: Pair<AppChild, Int>

    private lateinit var unbinder: Unbinder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        SherlockComponent.Controllers.displayChildComponent.get().inject(this)

        val view = inflater.inflate(R.layout.controller_display_child, container, false)

        unbinder = ButterKnife.bind(this, view)

        context = view.context

        setSupportActionBar(toolbar)

        val child = requireNotNull(args.getParcelable<AppChild>(ARG_CHILD)) {
            "Child cannot be null!"
        }

        val score = requireNotNull(args.getInt(ARG_SCORE, INVALID_SCORE).takeUnless { it == INVALID_SCORE }) {
            "Score cannot be null!"
        }

        result = child to score

        populateUi()

        return view
    }

    //TODO: should always fetch the latest child data
    private fun populateUi() {

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

    companion object {

        private const val ARG_CHILD = "inc.ahmedmourad.sherlock.view.controllers.arg.CHILD"
        private const val ARG_SCORE = "inc.ahmedmourad.sherlock.view.controllers.arg.SCORE"

        private const val NO_SCORE = -1
        private const val INVALID_SCORE = -2

        fun newInstance(result: Pair<AppChild, Int>) = DisplayChildController(Bundle(2).apply {
            putParcelable(ARG_CHILD, result.first)
            putInt(ARG_SCORE, result.second)
        })

        fun newInstance(child: AppChild) = newInstance(child to NO_SCORE)
    }
}
