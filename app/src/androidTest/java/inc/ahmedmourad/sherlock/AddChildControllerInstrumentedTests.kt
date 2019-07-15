package inc.ahmedmourad.sherlock

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.jaygoo.widget.RangeSeekBar
import inc.ahmedmourad.sherlock.view.activity.MainActivity
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class AddChildControllerInstrumentedTests {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setupTimber() {
        Timber.plant(Timber.DebugTree())
    }

    @Before
    fun setupActivity() {
        activityTestRule.launchActivity(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java))
        onView(withId(R.id.home_recycler))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
    }

    @Test
    fun addChildController_hasAllCorrectElements() {

        onView(withId(R.id.toolbar))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(hasDescendant(withText(R.string.found_a_child))))

        onView(withId(R.id.first_name_edit_text))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(isEnabled()))
                .check(matches(withHint(R.string.first_name)))

        onView(withId(R.id.last_name_edit_text))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(isEnabled()))
                .check(matches(withHint(R.string.last_name)))

        onView(withId(R.id.add_child_location_text_view))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(withText(R.string.no_location_specified)))

        onView(withId(R.id.add_child_location_image_view))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withText(R.string.age))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.add_child_age_seek_bar))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        with(activityTestRule.activity.findViewById<RangeSeekBar>(R.id.add_child_age_seek_bar)) {
            assertEquals(minProgress.toInt(), resources.getInteger(R.integer.add_child_age_seek_bar_min))
            assertEquals(maxProgress.toInt(), resources.getInteger(R.integer.add_child_age_seek_bar_max))
        }

        onView(withText(R.string.height))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.add_child_height_seek_bar))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        with(activityTestRule.activity.findViewById<RangeSeekBar>(R.id.add_child_height_seek_bar)) {
            assertEquals(minProgress.toInt(), resources.getInteger(R.integer.add_child_height_seek_bar_min))
            assertEquals(maxProgress.toInt(), resources.getInteger(R.integer.add_child_height_seek_bar_max))
        }

        onView(withText(R.string.gender))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.male_radio_button))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(isChecked()))

        onView(withId(R.id.female_radio_button))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(not(isChecked())))

        onView(withText(R.string.skin_color))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.skin_white))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.skin_wheat))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.skin_dark))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withText(R.string.hair_color))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.hair_blonde))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.hair_brown))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.hair_dark))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.add_child_picture_image_view))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.add_child_picture_text_view))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(withText(R.string.choose_a_picture)))

        onView(withId(R.id.add_child_notes_edit_text))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(isEnabled()))
                .check(matches(withHint(R.string.notes)))

        onView(withId(R.id.add_child_publish_button))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(isEnabled()))
                .check(matches(withText(R.string.publish)))
    }
}
