package inc.ahmedmourad.sherlock

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.nhaarman.mockitokotlin2.*
import dagger.Lazy
import inc.ahmedmourad.sherlock.data.firebase.contract.FirebaseContract
import inc.ahmedmourad.sherlock.data.firebase.model.FirebaseUrlChild
import inc.ahmedmourad.sherlock.data.firebase.repository.FirebaseCloudRepository
import inc.ahmedmourad.sherlock.data.firebase.repository.extractFirebaseUrlChild
import inc.ahmedmourad.sherlock.data.mapper.DataModelsMapper
import inc.ahmedmourad.sherlock.data.repositories.CloudRepository
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.*
import inc.ahmedmourad.sherlock.utils.getImageBytes
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import splitties.init.appCtx
import timber.log.Timber
import java.util.*

@RunWith(AndroidJUnit4::class)
class FirebaseCloudRepositoryInstrumentedTests {

    private lateinit var bus: Bus
    private lateinit var provider: Bus.PublishingState.Provider
    private lateinit var db: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var repository: CloudRepository

    private val ongoingState = Bus.PublishingState("ongoing", true)
    private val successState = Bus.PublishingState("ongoing", false)
    private val failureState = Bus.PublishingState("ongoing", false)

    private val child0 = DomainPictureChild(
            UUID.randomUUID().toString(),
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
            ), getImageBytes(R.drawable.found_child))

    private val child1 = DomainPictureChild(
            UUID.randomUUID().toString(),
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
            ), getImageBytes(R.drawable.search_child))

    private val child2 = DomainPictureChild(
            UUID.randomUUID().toString(),
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
            ), getImageBytes(R.drawable.coming_soon))

    @Before
    fun setupTimber() {
        Timber.plant(Timber.DebugTree())
    }

    @Before
    fun setupRepository() {

        val event = mock<Bus.Event<Bus.BackgroundState>>()
        val state = mock<Bus.State> { on { backgroundState } doReturn event }

        bus = mock { on { this.state } doReturn state }

        provider = mock {
            on { ongoing() } doReturn ongoingState
            on { success() } doReturn successState
            on { failure() } doReturn failureState
        }

        repository = FirebaseCloudRepository(Lazy { bus }, Lazy { provider })
    }

    @Before
    fun setupDatabase() {
        db = FirebaseDatabase.getInstance()
    }

    @Before
    fun setupStorage() {
        storage = FirebaseStorage.getInstance()
    }

    @Test
    fun init_shouldInitializeTheFirebaseApp() {
        assertFalse(FirebaseApp.getApps(appCtx).isEmpty())
    }

    @Test
    fun publish_shouldStoreChildPictureInStorage_shouldStoreChildInDatabase() {

        sequenceOf(
                child0,
                child1,
                child2
        ).onEach {
            assertChildExists(it, false)
            assertPictureExists(it, false)
        }.map {
            it to repository.publish(it)
                    .test()
                    .await()
                    .assertComplete()
                    .assertNoErrors()
                    .values()[0]
        }.onEach { (child, publishedChild) ->
            assertEquals(publishedChild, DomainUrlChild(
                    child.id,
                    child.publicationDate,
                    child.name,
                    child.notes,
                    child.location,
                    child.appearance,
                    publishedChild.pictureUrl
            ))
        }.onEach { (_, publishedChild) ->
            assertPictureUrlCorrect(publishedChild, publishedChild.pictureUrl)
        }.mapIndexed { index, (_, publishedChild) ->
            (index + 1) to publishedChild
        }.onEach { (index, _) ->

            verify(bus.state) {
                ((index * 2) + (index - 1)) * { backgroundState }
            }

            verify(bus.state.backgroundState) {
                (index * 1) * { notify(ongoingState) }
                (index * 1) * { notify(successState) }
                (index * 0) * { notify(failureState) }
            }

        }.map { (_, publishedChild) ->
            publishedChild
        }.onEach {
            assertChildExists(it, true)
            assertPictureExists(it, true)
        }.onEach { publishedChild ->

            Single.create<DomainUrlChild> {
                db.getReference(FirebaseContract.Database.PATH_CHILDREN)
                        .child(publishedChild.id)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                it.onSuccess(DataModelsMapper.toDomainUrlChild(dataSnapshot.extractFirebaseUrlChild()))
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                it.onError(databaseError.toException())
                            }
                        })
            }.test().await().assertComplete().assertNoErrors().assertValue(publishedChild)

        }.forEach {
            deleteChild(it)
            deletePicture(it)
        }
    }

    @Test
    fun find_shouldReturnTheChildrenMatchingTheSpecifiedCriteria() {

        val domainPublishedChildren = sequenceOf(
                child0,
                child1,
                child2
        ).onEach {
            assertChildExists(it, false)
        }.map {
            it.copy(appearance = it.appearance.copy(skin = Skin.WHITE))
        }.map {
            publishChild(FirebaseUrlChild(DataModelsMapper.toFirebasePictureChild(it), UUID.randomUUID().toString()))
                    .test()
                    .await()
                    .assertComplete()
                    .assertNoErrors()
                    .values()[0]
        }.map {
            DataModelsMapper.toDomainUrlChild(it)
        }.onEach {
            assertChildExists(it, true)
        }.toList()

        val filter = mock<Filter<DomainUrlChild>> {
            on {
                filter(any())
            } doAnswer { param ->
                param.getArgument<List<DomainUrlChild>>(0).sortedBy { it.id }.mapIndexed { i, child -> child to i * 100 }
            }
        }

        val rules = DomainChildCriteriaRules(
                DomainName("", ""),
                DomainLocation("", "", "", DomainCoordinates(0.0, 0.0)),
                DomainAppearance(
                        Gender.MALE,
                        Skin.WHITE,
                        Hair.BROWN,
                        0,
                        0
                )
        )

        repository.find(rules, filter)
                .test()
                .awaitCount(1)
                .assertNotComplete()
                .assertNoErrors()
                .assertValue(domainPublishedChildren.sortedBy { it.id }.mapIndexed { i, child -> child to i * 100 })

        domainPublishedChildren.forEach(this::deleteChild)
    }

    private fun publishChild(child: FirebaseUrlChild): Single<FirebaseUrlChild> {
        return Single.create {
            db.getReference(FirebaseContract.Database.PATH_CHILDREN)
                    .child(child.id)
                    .updateChildren(child.toMap())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful)
                            it.onSuccess(child)
                        else
                            it.onError(task.exception ?: RuntimeException())
                    }
        }
    }

    private fun deleteChild(child: DomainChild) {

        Completable.create {
            db.getReference(FirebaseContract.Database.PATH_CHILDREN)
                    .child(child.id)
                    .removeValue { error, _ ->
                        if (error == null)
                            it.onComplete()
                        else
                            it.onError(error.toException())
                    }
        }.test().await().assertNoErrors().assertComplete()

        assertChildExists(child, false)
    }

    private fun deletePicture(child: DomainChild) {

        Completable.create {
            storage.getReference(FirebaseContract.Storage.PATH_CHILDREN)
                    .child("${child.id}.${FirebaseContract.Storage.FILE_FORMAT}")
                    .delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful)
                            it.onComplete()
                        else
                            it.onError(task.exception ?: RuntimeException())
                    }
        }.test().await().assertNoErrors().assertComplete()

        assertPictureExists(child, false)
    }

    private fun assertChildExists(child: DomainChild, exists: Boolean) {
        Single.create<Boolean> {
            db.getReference(FirebaseContract.Database.PATH_CHILDREN)
                    .child(child.id)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            it.onSuccess(dataSnapshot.exists())
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            it.onError(databaseError.toException())
                        }
                    })
        }.test().await().assertComplete().assertNoErrors().assertValue(exists)
    }

    private fun assertPictureExists(child: DomainChild, exists: Boolean) {
        Single.create<Boolean> {
            storage.getReference(FirebaseContract.Storage.PATH_CHILDREN)
                    .child("${child.id}.${FirebaseContract.Storage.FILE_FORMAT}")
                    .downloadUrl
                    .addOnCompleteListener { task ->
                        it.onSuccess(task.isSuccessful)
                    }
        }.test().await().assertComplete().assertNoErrors().assertValue(exists)
    }

    private fun assertPictureUrlCorrect(child: DomainChild, url: String) {
        Single.create<Boolean> {
            storage.getReference(FirebaseContract.Storage.PATH_CHILDREN)
                    .child("${child.id}.${FirebaseContract.Storage.FILE_FORMAT}")
                    .downloadUrl
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful)
                            it.onSuccess(task.result.toString() == url)
                        else
                            it.onError(task.exception ?: RuntimeException())
                    }
        }.test().await().assertComplete().assertNoErrors().assertValue(true)
    }
}
