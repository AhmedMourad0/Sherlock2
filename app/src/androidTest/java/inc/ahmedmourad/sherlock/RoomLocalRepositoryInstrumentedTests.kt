package inc.ahmedmourad.sherlock

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.Lazy
import inc.ahmedmourad.sherlock.data.mapper.DataModelsMapper
import inc.ahmedmourad.sherlock.data.repositories.LocalRepository
import inc.ahmedmourad.sherlock.data.room.database.SherlockDatabase
import inc.ahmedmourad.sherlock.data.room.repository.RoomLocalRepository
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.model.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import splitties.init.appCtx
import timber.log.Timber
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomLocalRepositoryInstrumentedTests {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: SherlockDatabase
    private lateinit var repository: LocalRepository

    private val child0 = DomainUrlChild(
            "0",
            System.currentTimeMillis(),
            DomainName("Ahmed", "Mourad"),
            "",
            DomainLocation("1", "a", "a", DomainCoordinates(90.0, 140.0)),
            DomainAppearance(
                    Gender.MALE,
                    Skin.WHEAT,
                    Hair.DARK,
                    DomainRange(18, 22),
                    DomainRange(170, 190)
            ), ""
    ) to 100

    private val child1 = DomainUrlChild(
            "1",
            System.currentTimeMillis(),
            DomainName("Yasmeen", "Mourad"),
            "",
            DomainLocation("11", "sh", "hs", DomainCoordinates(70.0, 120.0)),
            DomainAppearance(
                    Gender.FEMALE,
                    Skin.WHITE,
                    Hair.DARK,
                    DomainRange(16, 21),
                    DomainRange(160, 180)
            ), ""
    ) to 200

    private val child2 = DomainUrlChild(
            "2",
            System.currentTimeMillis(),
            DomainName("Ahmed", "Shamakh"),
            "",
            DomainLocation("111", "b", "bb", DomainCoordinates(55.0, 99.0)),
            DomainAppearance(
                    Gender.MALE,
                    Skin.DARK,
                    Hair.BROWN,
                    DomainRange(11, 23),
                    DomainRange(150, 180)
            ), ""
    ) to 300

    @Before
    fun setupTimber() {
        Timber.plant(Timber.DebugTree())
    }

    @Before
    fun setupRepositoryAndDatabase() {
        db = Room.inMemoryDatabaseBuilder(appCtx, SherlockDatabase::class.java).allowMainThreadQueries().build()
        repository = RoomLocalRepository(Lazy { db })
    }

    @Test
    fun replaceResults_shouldReplaceTheSearchResultsInTheDatabaseWithTheOnesProvided() {

        repository.replaceAll(listOf(child1, child2)).test().await()

        val resultsTestObserver = db.resultsDao()
                .findAll()
                .distinctUntilChanged()
                .map { it.map(DataModelsMapper::toDomainUrlChild) }
                .test()

        resultsTestObserver.awaitCount(1).assertValuesOnly(listOf(child1, child2))

        repository.apply {
            replaceAll(listOf(child0, child1, child2))
                    .andThen(replaceAll(listOf(child0)))
                    .andThen(replaceAll(listOf(child1, child2)))
                    .andThen(replaceAll(listOf(child0, child1, child2)))
                    .subscribe()
        }

        resultsTestObserver.awaitCount(5).assertValuesOnly(
                listOf(child1, child2),
                listOf(child0, child1, child2),
                listOf(child0),
                listOf(child1, child2),
                listOf(child0, child1, child2)
        )
    }

    @Test
    fun getResults_shouldBeNotifiedWithExistingAndChangingSearchResults() {

        db.resultsDao().replaceAll(listOf(child1, child2).map(DataModelsMapper::toRoomChildEntity)).test().await()

        val resultsTestObserver = repository.findAll().test()

        resultsTestObserver.awaitCount(1).assertValuesOnly(listOf(child1, child2))

        db.resultsDao().apply {
            replaceAll(listOf(child0, child1, child2).map(DataModelsMapper::toRoomChildEntity))
                    .andThen(replaceAll(listOf(child0).map(DataModelsMapper::toRoomChildEntity)))
                    .andThen(replaceAll(listOf(child1, child2).map(DataModelsMapper::toRoomChildEntity)))
                    .andThen(replaceAll(listOf(child0, child1, child2).map(DataModelsMapper::toRoomChildEntity)))
                    .subscribe()
        }

        resultsTestObserver.awaitCount(5).assertValuesOnly(
                listOf(child1, child2),
                listOf(child0, child1, child2),
                listOf(child0),
                listOf(child1, child2),
                listOf(child0, child1, child2)
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}
