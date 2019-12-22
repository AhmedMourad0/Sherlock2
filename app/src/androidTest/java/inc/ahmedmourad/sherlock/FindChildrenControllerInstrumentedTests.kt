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
import inc.ahmedmourad.sherlock.view.activity.MainActivity
import inc.ahmedmourad.sherlock.view.views.ProperNumberPicker
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class FindChildrenControllerInstrumentedTests {

    @get:Rule
    internal val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup() {
        Timber.plant(Timber.DebugTree())
        setupActivity()
    }

    private fun setupActivity() {
        activityTestRule.launchActivity(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java))
        onView(withId(R.id.home_recycler))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))
    }

    @Test
    fun findChildrenController_hasAllCorrectElements() {

        onView(withId(R.id.main_toolbar))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(hasDescendant(withText(R.string.search))))

        onView(withId(R.id.first_name_edit_text))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(isEnabled()))
                .check(matches(withHint(R.string.first_name)))

        onView(withId(R.id.last_name_edit_text))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(isEnabled()))
                .check(matches(withHint(R.string.last_name)))

        onView(withId(R.id.find_children_location_text_view))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(withText(R.string.no_location_specified)))

        onView(withId(R.id.find_children_location_image_view))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withText(R.string.age))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.find_children_age_number_picker))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        with(activityTestRule.activity.findViewById<ProperNumberPicker>(R.id.find_children_age_number_picker)) {
            assertEquals(value, resources.getInteger(R.integer.find_children_age_number_picker_default_value))
        }

        onView(withText(R.string.height))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.find_children_height_number_picker))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        with(activityTestRule.activity.findViewById<ProperNumberPicker>(R.id.find_children_height_number_picker)) {
            assertEquals(value, resources.getInteger(R.integer.find_children_height_number_picker_default_value))
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

        onView(withId(R.id.find_children_search_button))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(isEnabled()))
                .check(matches(withText(R.string.search)))
    }
}
