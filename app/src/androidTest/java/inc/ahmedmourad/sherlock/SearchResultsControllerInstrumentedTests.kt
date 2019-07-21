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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Lazy
import inc.ahmedmourad.sherlock.custom.ProperNumberPicker
import inc.ahmedmourad.sherlock.data.firebase.repositories.FirebaseDatabaseCloudRepository
import inc.ahmedmourad.sherlock.data.repositories.SherlockRepository
import inc.ahmedmourad.sherlock.data.room.database.SherlockDatabase
import inc.ahmedmourad.sherlock.data.room.repository.RoomLocalRepository
import inc.ahmedmourad.sherlock.domain.bus.RxBus
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.repository.Repository
import inc.ahmedmourad.sherlock.framework.AndroidDateManager
import inc.ahmedmourad.sherlock.idling.SearchResultsIdlingResource
import inc.ahmedmourad.sherlock.mapper.AppModelsMapper
import inc.ahmedmourad.sherlock.model.*
import inc.ahmedmourad.sherlock.utils.TextFormatter
import inc.ahmedmourad.sherlock.utils.childAt
import inc.ahmedmourad.sherlock.utils.deleteChildren
import inc.ahmedmourad.sherlock.utils.deletePictures
import inc.ahmedmourad.sherlock.view.activity.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.*

@RunWith(AndroidJUnit4::class)
class SearchResultsControllerInstrumentedTests {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var repository: Repository
    private lateinit var idlingResource: SearchResultsIdlingResource

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
    fun setupRepository() {

        setupDatabase()
        setupStorage()

        repository = SherlockRepository(
                Lazy { RoomLocalRepository(Lazy { SherlockDatabase.getInstance() }) },
                Lazy {
                    FirebaseDatabaseCloudRepository(
                            Lazy { FirebaseDatabase.getInstance() },
                            Lazy { FirebaseStorage.getInstance() },
                            Lazy { RxBus() }
                    )
                }
        )

        repository.publish(AppModelsMapper.toDomainPictureChild(child))
                .test()
                .await()
                .assertNoErrors()
                .assertComplete()

        setupEspresso()
    }

    private fun setupDatabase() {
        deleteChildren()
    }

    private fun setupStorage() {
        deletePictures()
    }

    private fun setupEspresso() {
        idlingResource = SearchResultsIdlingResource(repository)
        IdlingRegistry.getInstance().register(idlingResource)
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

                findViewById<ProperNumberPicker>(R.id.find_children_age_number_picker).value = rules.appearance.age.value

                findViewById<ProperNumberPicker>(R.id.find_children_height_number_picker).value = rules.appearance.height.value

                idlingResource.onStart()

                findViewById<Button>(R.id.find_children_search_button).performClick()
            }
        }

        val formatter = TextFormatter()

        onView(withId(R.id.toolbar))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                .check(matches(hasDescendant(withText(R.string.search_results))))

        onView(withId(R.id.search_results_recycler).childAt(0))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
//                .check(matches(hasDescendant(withText(formatter.formatName(child.name))))))
                .check(matches(hasDescendant(withText(formatter.formatLocation(child.location)))))
                .check(matches(hasDescendant(withText(formatter.formatNotes(child.notes)))))
                .check(matches(hasDescendant(withText(AndroidDateManager().getRelativeDateTimeString(child.publicationDate)))))
    }

    @After
    fun clearEspresso() {
        idlingResource.dispose()
        IdlingRegistry.getInstance().unregister(idlingResource)
        clearDatabase()
    }

    private fun clearDatabase() {
        deleteChildren()
        deletePictures()
    }
}
