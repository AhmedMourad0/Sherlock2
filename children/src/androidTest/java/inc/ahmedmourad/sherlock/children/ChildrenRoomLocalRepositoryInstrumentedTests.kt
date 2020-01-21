package inc.ahmedmourad.sherlock.children

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.Lazy
import inc.ahmedmourad.sherlock.children.local.database.SherlockDatabase
import inc.ahmedmourad.sherlock.children.local.model.entities.RoomChildEntity
import inc.ahmedmourad.sherlock.children.local.repository.ChildrenRoomLocalRepository
import inc.ahmedmourad.sherlock.children.repository.dependencies.ChildrenLocalRepository
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.model.children.*
import io.reactivex.Flowable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import splitties.init.appCtx
import timber.log.Timber
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class ChildrenRoomLocalRepositoryInstrumentedTests {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: SherlockDatabase
    private lateinit var repository: ChildrenLocalRepository

    private val child0 = DomainRetrievedChild(
            UUID.randomUUID().toString(),
            System.currentTimeMillis(),
            DomainName("Ahmed", "Mourad"),
            "",
            DomainLocation("1", "a", "a", DomainCoordinates(90.0, 140.0)),
            DomainEstimatedAppearance(
                    Gender.MALE,
                    Skin.WHEAT,
                    Hair.DARK,
                    DomainRange(18, 22),
                    DomainRange(170, 190)
            ), ""
    ) to 100

    private val child1 = DomainRetrievedChild(
            UUID.randomUUID().toString(),
            System.currentTimeMillis(),
            DomainName("Yasmeen", "Mourad"),
            "",
            DomainLocation("11", "sh", "hs", DomainCoordinates(70.0, 120.0)),
            DomainEstimatedAppearance(
                    Gender.FEMALE,
                    Skin.WHITE,
                    Hair.DARK,
                    DomainRange(16, 21),
                    DomainRange(160, 180)
            ), ""
    ) to 200

    private val child2 = DomainRetrievedChild(
            UUID.randomUUID().toString(),
            System.currentTimeMillis(),
            DomainName("Ahmed", "Shamakh"),
            "",
            DomainLocation("111", "b", "bb", DomainCoordinates(55.0, 99.0)),
            DomainEstimatedAppearance(
                    Gender.MALE,
                    Skin.DARK,
                    Hair.BROWN,
                    DomainRange(11, 23),
                    DomainRange(150, 180)
            ), ""
    ) to 300

    @Before
    fun setup() {
        Timber.plant(Timber.DebugTree())
        setupRepositoryAndDatabase()
    }

    private fun setupRepositoryAndDatabase() {
        db = Room.inMemoryDatabaseBuilder(appCtx, SherlockDatabase::class.java).allowMainThreadQueries().build()
        repository = ChildrenRoomLocalRepository(Lazy { db })
    }

    @Test
    fun updateIfExists_shouldReturnTheScoreOfTheGivenChild() {

        repository.updateIfExists(child0.first)
                .test()
                .await()
                .assertNoErrors()
                .assertNoValues()
                .assertComplete()

        val resultsTestObserver = db.resultsDao()
                .findAll()
                .distinctUntilChanged()
                .flatMap {
                    Flowable.fromIterable(it)
                            .map(RoomChildEntity::toDomainRetrievedChild)
                            .toList()
                            .toFlowable()
                }.test()

        resultsTestObserver.awaitCount(1).assertValuesOnly(listOf())

        repository.replaceAll(listOf(child0))
                .test()
                .assertNoErrors()
                .assertComplete()

        resultsTestObserver.awaitCount(2).assertValuesOnly(
                listOf(),
                listOf(child0)
        )

        val newChild0 = child0.first.copy(name = DomainName("Yasmeen", "Shamakh")) to child0.second

        repository.updateIfExists(newChild0.first)
                .test()
                .await()
                .assertNoErrors()
                .assertValue(newChild0)
                .assertComplete()

        resultsTestObserver.awaitCount(3).assertValuesOnly(
                listOf(),
                listOf(child0),
                listOf(newChild0)
        )
    }

    @Test
    fun replaceAll_shouldReplaceTheResultsInTheDatabaseWithTheOnesProvided() {

        repository.replaceAll(listOf(child1, child2)).test().await()

        val resultsTestObserver = db.resultsDao()
                .findAll()
                .distinctUntilChanged()
                .flatMap {
                    Flowable.fromIterable(it)
                            .map(RoomChildEntity::toDomainRetrievedChild)
                            .toList()
                            .toFlowable()
                }.test()

        resultsTestObserver.awaitCount(1).assertValuesOnly(listOf(child1, child2))

        repository.apply {
            replaceAll(listOf(child0, child1, child2))
                    .flatMap { replaceAll(listOf(child0)) }
                    .flatMap { replaceAll(listOf(child1, child2)) }
                    .flatMap { replaceAll(listOf(child0, child1, child2)) }
                    .test()
                    .assertNoErrors()
                    .assertComplete()
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
    fun findAll_shouldRetrieveAndObserveChangesToTheResultsInTheDatabase() {

        db.resultsDao().replaceAll(listOf(child1, child2).map(Pair<DomainRetrievedChild, Int>::toRoomChildEntity)).test().await()

        val resultsTestObserver = repository.findAll().test()

        resultsTestObserver.awaitCount(1).assertValuesOnly(
                listOf(child1, child2).map { (child, score) ->
                    child.simplify() to score
                }
        )

        db.resultsDao().apply {
            replaceAll(listOf(child0, child1, child2).map(Pair<DomainRetrievedChild, Int>::toRoomChildEntity))
                    .andThen(replaceAll(listOf(child0).map(Pair<DomainRetrievedChild, Int>::toRoomChildEntity)))
                    .andThen(replaceAll(listOf(child1, child2).map(Pair<DomainRetrievedChild, Int>::toRoomChildEntity)))
                    .andThen(replaceAll(listOf(child0, child1, child2).map(Pair<DomainRetrievedChild, Int>::toRoomChildEntity)))
                    .test()
                    .assertNoErrors()
                    .assertComplete()
        }

        resultsTestObserver.awaitCount(5).assertValuesOnly(
                *listOf(
                        listOf(child1, child2),
                        listOf(child0, child1, child2),
                        listOf(child0),
                        listOf(child1, child2),
                        listOf(child0, child1, child2)
                ).map {
                    it.map { (child, score) ->
                        child.simplify() to score
                    }
                }.toTypedArray()
        )
    }

    @Test
    fun clear_shouldDeleteAllTheResultsInTheDatabase() {

        repository.replaceAll(listOf(child1, child2)).test().await()

        val resultsTestObserver = db.resultsDao()
                .findAll()
                .distinctUntilChanged()
                .flatMap {
                    Flowable.fromIterable(it)
                            .map(RoomChildEntity::toDomainRetrievedChild)
                            .toList()
                            .toFlowable()
                }.test()

        resultsTestObserver.awaitCount(1).assertValuesOnly(listOf(child1, child2))

        repository.clear().test().await().assertNoErrors().assertComplete()

        resultsTestObserver.awaitCount(2).assertValuesOnly(
                listOf(child1, child2),
                listOf()
        )
    }


    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}
