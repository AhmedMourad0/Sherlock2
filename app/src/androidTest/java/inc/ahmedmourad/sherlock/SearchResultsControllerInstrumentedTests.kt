package inc.ahmedmourad.sherlock

import android.content.Intent
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.google.android.material.textfield.TextInputEditText
import inc.ahmedmourad.sherlock.custom.ProperNumberPicker
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.idling.toIdlingResource
import inc.ahmedmourad.sherlock.mapper.toAppChild
import inc.ahmedmourad.sherlock.model.*
import inc.ahmedmourad.sherlock.utils.childAt
import inc.ahmedmourad.sherlock.view.activity.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class SearchResultsControllerInstrumentedTests {

    @get:Rule
    internal val activityTestRule = ActivityTestRule(MainActivity::class.java)

    private val component by lazy { SherlockComponent.Test.testComponent }

    private val bus by lazy { component.get().bus() }
    private val repository by lazy { component.get().childrenRepository() }
    private val formatter by lazy { component.get().formatter() }
    private val dateManager by lazy { component.get().dateManager() }

    private val childrenFindingIdlingResource = bus.childrenFindingState.toIdlingResource("childrenFindingState")

    private val rules = AppChildCriteriaRules(
            AppName("Yasmeen", "Mourad"),
            AppLocation.empty(),
            AppExactAppearance(
                    Gender.FEMALE,
                    Skin.WHITE,
                    Hair.BLONDE,
                    20,
                    180
            )
    )

    private val publishedChild = AppPublishedChild(
            AppName("Yasmeen", "Mourad"),
            "bla bla bla",
            AppLocation.empty(),
            AppEstimatedAppearance(
                    Gender.FEMALE,
                    Skin.WHITE,
                    Hair.BLONDE,
                    AppRange(18, 22),
                    AppRange(170, 190)
            ), ""
    )

    private lateinit var retrievedChild: AppRetrievedChild

    @Before
    fun setup() {
        Timber.plant(Timber.DebugTree())
        setupActivity()
        setupRepository()
        setupEspresso()
    }

    private fun setupActivity() {
        activityTestRule.launchActivity(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java))
    }

    private fun setupRepository() {

        repository.test().clear().test().await().assertNoErrors().assertComplete()

        retrievedChild = repository.publish(publishedChild.toDomainChild())
                .test()
                .await()
                .assertNoErrors()
                .assertComplete()
                .values()[0]
                .toAppChild()
    }

    private fun setupEspresso() {
        IdlingRegistry.getInstance().register(childrenFindingIdlingResource)
    }

    @Test
    fun searchResultsController_hasAllCorrectElements() {

        onView(withId(R.id.home_recycler))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

        onView(withId(R.id.female_radio_button))
                .perform(click())

        with(activityTestRule.activity) {

            runOnUiThread {

                findViewById<TextInputEditText>(R.id.first_name_edit_text)
                        .setText(rules.name.first)

                findViewById<TextInputEditText>(R.id.last_name_edit_text)
                        .setText(rules.name.last)

                findViewById<ProperNumberPicker>(R.id.find_children_age_number_picker).value = rules.appearance.age

                findViewById<ProperNumberPicker>(R.id.find_children_height_number_picker).value = rules.appearance.height

                findViewById<Button>(R.id.find_children_search_button).performClick()
            }
        }

        onView(withId(R.id.toolbar))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(hasDescendant(withText(R.string.search_results))))

        onView(withId(R.id.search_results_recycler).childAt(0))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
//                .check(matches(hasDescendant(withText(formatter.formatName(child.name))))))
                .check(matches(hasDescendant(withText(formatter.formatLocation(retrievedChild.location)))))
                .check(matches(hasDescendant(withText(formatter.formatNotes(retrievedChild.notes)))))
                .check(matches(hasDescendant(withText(dateManager.getRelativeDateTimeString(retrievedChild.publicationDate)))))
    }

    @After
    fun clear() {
        clearEspresso()
        clearDatabase()
        component.release()
    }

    private fun clearEspresso() {
        childrenFindingIdlingResource.dispose()
        IdlingRegistry.getInstance().unregister(childrenFindingIdlingResource)
    }

    private fun clearDatabase() {
        repository.test().clear().test().await().assertNoErrors().assertComplete()
    }
}
