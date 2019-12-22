package inc.ahmedmourad.sherlock

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import inc.ahmedmourad.sherlock.utils.childAt
import inc.ahmedmourad.sherlock.view.activity.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

import org.junit.runner.RunWith
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class HomeControllerInstrumentedTests {

    @get:Rule
    internal val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup() {
        Timber.plant(Timber.DebugTree())
        setupActivity()
    }

    private fun setupActivity() {
        activityTestRule.launchActivity(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java))
    }

    @Test
    fun homeController_hasAllCorrectElements() {

        onView(withId(R.id.home_recycler))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(hasChildCount(4)))

        onView(withId(R.id.main_toolbar))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(hasDescendant(withText(R.string.app_name))))

        mapOf(
                0 to R.string.found_a_child,
                1 to R.string.search,
                2 to R.string.coming_soon,
                3 to R.string.coming_soon
        ).forEach { (position, textRes) ->
            onView(withId(R.id.home_recycler).childAt(position))
                    .check(matches(hasDescendant(withId(R.id.section_name))))
                    .check(matches(hasDescendant(withId(R.id.section_image))))
                    .check(matches(hasDescendant(withText(textRes))))
        }
    }
}
