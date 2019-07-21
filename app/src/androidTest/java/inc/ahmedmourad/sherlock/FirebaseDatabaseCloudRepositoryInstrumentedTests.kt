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
import inc.ahmedmourad.sherlock.data.firebase.repositories.FirebaseDatabaseCloudRepository
import inc.ahmedmourad.sherlock.data.firebase.repositories.extractFirebaseUrlChild
import inc.ahmedmourad.sherlock.data.mapper.DataModelsMapper
import inc.ahmedmourad.sherlock.data.repositories.CloudRepository
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.*
import inc.ahmedmourad.sherlock.utils.deleteChildren
import inc.ahmedmourad.sherlock.utils.deletePictures
import inc.ahmedmourad.sherlock.utils.getImageBytes
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import splitties.init.appCtx
import timber.log.Timber
import java.util.*

@RunWith(AndroidJUnit4::class)
class FirebaseDatabaseCloudRepositoryInstrumentedTests {

    private lateinit var bus: Bus
    private lateinit var db: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var repository: CloudRepository

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
            ), getImageBytes(R.drawable.found_a_child))

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

        val event = mock<Bus.Event<Bus.PublishingState>>()

        bus = mock { on { publishingState } doReturn event }

        repository = FirebaseDatabaseCloudRepository(
                Lazy { FirebaseDatabase.getInstance() },
                Lazy { FirebaseStorage.getInstance() },
                Lazy { bus }
        )
    }

    @Before
    fun setupDatabase() {
        db = FirebaseDatabase.getInstance()
        deleteChildren()
    }

    @Before
    fun setupStorage() {
        storage = FirebaseStorage.getInstance()
        deletePictures()
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

            verify(bus.publishingState) {
                (index * 1) * { notify(Bus.PublishingState.ONGOING) }
                (index * 1) * { notify(Bus.PublishingState.SUCCESS) }
                (index * 0) * { notify(Bus.PublishingState.FAILURE) }
            }

        }.map { (_, publishedChild) ->
            publishedChild
        }.forEach {
            assertChildCorrect(it)
            assertPictureExists(it, true)
        }

        deleteChildren()
        deletePictures()
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
            assertChildCorrect(it)
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

        repository.findAll(rules, filter)
                .test()
                .awaitCount(1)
                .assertNotComplete()
                .assertNoErrors()
                .assertValue(domainPublishedChildren.sortedBy { it.id }.mapIndexed { i, child -> child to i * 100 })

        deleteChildren()
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
                            it.onError(task.exception
                                    ?: IllegalArgumentException("Exception is null!"))
                    }
        }
    }

    @After
    fun clearDatabase() {
        deleteChildren()
        deletePictures()
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

    private fun assertChildCorrect(child: DomainUrlChild) {
        Single.create<DomainUrlChild> {
            db.getReference(FirebaseContract.Database.PATH_CHILDREN)
                    .child(child.id)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            it.onSuccess(DataModelsMapper.toDomainUrlChild(dataSnapshot.extractFirebaseUrlChild()))
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            it.onError(databaseError.toException())
                        }
                    })
        }.test().await().assertComplete().assertNoErrors().assertValue(child)
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
                            it.onError(task.exception
                                    ?: IllegalArgumentException("Exception is null!"))
                    }
        }.test().await().assertComplete().assertNoErrors().assertValue(true)
    }
}
