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
import com.jaygoo.widget.RangeSeekBar
import dagger.Lazy
import inc.ahmedmourad.sherlock.custom.ProperNumberPicker
import inc.ahmedmourad.sherlock.data.firebase.repository.FirebaseCloudRepository
import inc.ahmedmourad.sherlock.data.repositories.SherlockRepository
import inc.ahmedmourad.sherlock.data.room.database.SherlockDatabase
import inc.ahmedmourad.sherlock.data.room.repository.RoomLocalRepository
import inc.ahmedmourad.sherlock.device.AndroidTextManager
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.bus.RxBus
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.mapper.AppModelsMapper
import inc.ahmedmourad.sherlock.model.*
import inc.ahmedmourad.sherlock.utils.*
import inc.ahmedmourad.sherlock.view.activity.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.*

@RunWith(AndroidJUnit4::class)
class DisplayChildControllerInstrumentedTests {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    private val rules = AppChildCriteriaRules(
            AppName("Yasmeen", "Mourad"),
            AppLocation.empty(),
            AppAppearance(
                    Gender.FEMALE,
                    Skin.WHITE,
                    Hair.BLONDE,
                    PInt(20),
                    PInt(180)
            )
    )

    private val child = AppPictureChild(
            UUID.randomUUID().toString(),
            System.currentTimeMillis(),
            AppName("Yasmeen", "Mourad"),
            "bla bla bla",
            AppLocation.empty(),
            AppAppearance(
                    Gender.FEMALE,
                    Skin.WHITE,
                    Hair.BLONDE,
                    AppRange(18, 22),
                    AppRange(170, 190)
            ), ""
    )

    @Before
    fun setupTimber() {
        Timber.plant(Timber.DebugTree())
    }

    @Before
    fun setupActivity() {
        activityTestRule.launchActivity(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java))
    }

    @Before
    fun setupDatabase() {
        deleteChildren()
    }

    @Before
    fun setupStorage() {
        deletePictures()
    }

    @Test
    fun displayChildController_fromAddChildController_hasAllCorrectElements() {

        onView(withId(R.id.home_recycler))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        onView(withId(R.id.female_radio_button))
                .perform(click())

        with(activityTestRule.activity) {

            runOnUiThread {

                findViewById<TextInputEditText>(R.id.first_name_edit_text)
                        .setText(child.name.first)

                findViewById<TextInputEditText>(R.id.last_name_edit_text)
                        .setText(child.name.last)

                findViewById<TextInputEditText>(R.id.add_child_notes_edit_text)
                        .setText(child.notes)

                findViewById<RangeSeekBar>(R.id.add_child_age_seek_bar)
                        .setValue(child.appearance.age.from.toFloat(), child.appearance.age.to.toFloat())

                findViewById<RangeSeekBar>(R.id.add_child_height_seek_bar)
                        .setValue(child.appearance.height.from.toFloat(), child.appearance.height.to.toFloat())
            }
        }

        onView(withId(R.id.add_child_publish_button))
                .perform(nestedScrollTo())
                .perform(click())

        testDisplayChildController()
    }

    @Test
    fun displayChildController_fromFindChildrenController_hasAllCorrectElements() {

        val repository = SherlockRepository(
                Lazy { RoomLocalRepository(Lazy { SherlockDatabase.getInstance() }) },
                Lazy {
                    FirebaseCloudRepository(
                            Lazy { RxBus() },
                            Lazy { Bus.PublishingState.TextManagerProvider(Lazy { AndroidTextManager() }) }
                    )
                }
        )

        repository.publish(AppModelsMapper.toDomainPictureChild(child))
                .test()
                .await()
                .assertNoErrors()
                .assertComplete()

        val idlingResource = SearchResultsIdlingResource(repository)
        IdlingRegistry.getInstance().register(idlingResource)

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

                findViewById<ProperNumberPicker>(R.id.find_children_age_number_picker).value = rules.appearance.age.value

                findViewById<ProperNumberPicker>(R.id.find_children_height_number_picker).value = rules.appearance.height.value

                idlingResource.onStart()

                findViewById<Button>(R.id.find_children_search_button).performClick()
            }
        }

        onView(withId(R.id.search_results_recycler).childAt(0)).perform(click())

        testDisplayChildController()

        idlingResource.dispose()
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    private fun testDisplayChildController() {

        val formatter = TextFormatter()
        val name = formatter.formatName(child.name)

        onView(withId(R.id.display_child_toolbar))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(hasDescendant(withText(name))))

        onView(withId(R.id.display_child_picture))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.display_child_name))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(withText(name)))

        onView(withId(R.id.display_child_age))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(withText(formatter.formatAge(child.appearance.age))))

        onView(withId(R.id.display_child_gender))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(withText(formatter.formatGender(child.appearance.gender))))

        onView(withId(R.id.display_child_height))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(withText(formatter.formatHeight(child.appearance.height))))

        onView(withId(R.id.display_child_skin))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(withText(formatter.formatSkin(child.appearance.skin))))

        onView(withId(R.id.display_child_hair))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(withText(formatter.formatHair(child.appearance.hair))))

        onView(withId(R.id.display_child_location))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(withText(formatter.formatLocation(child.location))))

        onView(withId(R.id.display_child_notes))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(withText(formatter.formatNotes(child.notes))))

        onView(withText(R.string.description))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withText(R.string.details))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @After
    fun clearDatabase() {
        deleteChildren()
        deletePictures()
    }
}
