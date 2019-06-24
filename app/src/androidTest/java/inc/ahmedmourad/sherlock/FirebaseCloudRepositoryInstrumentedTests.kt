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
    fun setup() {

        Timber.plant(Timber.DebugTree())

        val event = mock<Bus.Event<Bus.BackgroundState>>()
        val state = mock<Bus.State> { on { backgroundState } doReturn event }

        provider = mock {
            on { ongoing() } doReturn ongoingState
            on { success() } doReturn successState
            on { failure() } doReturn failureState
        }
        bus = mock { on { this.state } doReturn state }
        repository = FirebaseCloudRepository(Lazy { bus }, Lazy { provider })

        db = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
    }

    @Test
    fun init_shouldInitializeTheFirebaseApp() {
        assertFalse(FirebaseApp.getApps(appCtx).isEmpty())
    }

    @Test
    fun publish_shouldStoreChildPictureInStorage_shouldStoreChildInDatabase() {

        assertChildExists(child0, false)
        assertPictureExists(child0, false)

        val publishedChild0 = repository.publish(child0)
                .test()
                .await()
                .assertComplete()
                .assertNoErrors()
                .values()[0]

        assertEquals(publishedChild0, DomainUrlChild(
                child0.id,
                child0.publicationDate,
                child0.name,
                child0.notes,
                child0.location,
                child0.appearance,
                publishedChild0.pictureUrl
        ))

        assertPictureUrlCorrect(publishedChild0, publishedChild0.pictureUrl)

        verify(bus.state) {
            2 * { backgroundState }
        }

        verify(bus.state.backgroundState) {
            1 * { notify(ongoingState) }
            1 * { notify(successState) }
            0 * { notify(failureState) }
        }

        assertChildExists(child0, true)
        assertPictureExists(child0, true)

        Single.create<DomainUrlChild> {
            db.getReference(FirebaseContract.Database.PATH_CHILDREN)
                    .child(child0.id)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            it.onSuccess(DataModelsMapper.toDomainUrlChild(dataSnapshot.extractFirebaseUrlChild()))
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            it.onError(databaseError.toException())
                        }
                    })
        }.test().await().assertComplete().assertNoErrors().assertValue(publishedChild0)

        deleteChild(publishedChild0)
        deletePicture(publishedChild0)
    }

    @Test
    fun find_shouldReturnTheChildrenMatchingTheSpecifiedCriteria() {

        assertChildExists(child0, false)
        assertChildExists(child1, false)
        assertChildExists(child2, false)

        val child0 = child0.copy(appearance = child0.appearance.copy(skin = Skin.WHITE))
        val child1 = child1.copy(appearance = child1.appearance.copy(skin = Skin.WHITE))
        val child2 = child2.copy(appearance = child2.appearance.copy(skin = Skin.WHITE))

        val publishedChild0 = publishChild(FirebaseUrlChild(DataModelsMapper.toFirebasePictureChild(child0), "url - 0"))
                .test()
                .await()
                .assertComplete()
                .assertNoErrors()
                .values()[0]

        val publishedChild1 = publishChild(FirebaseUrlChild(DataModelsMapper.toFirebasePictureChild(child1), "url - 1"))
                .test()
                .await()
                .assertComplete()
                .assertNoErrors()
                .values()[0]

        val publishedChild2 = publishChild(FirebaseUrlChild(DataModelsMapper.toFirebasePictureChild(child2), "url - 2"))
                .test()
                .await()
                .assertComplete()
                .assertNoErrors()
                .values()[0]

        val domainPublishedChild0 = DataModelsMapper.toDomainUrlChild(publishedChild0)
        val domainPublishedChild1 = DataModelsMapper.toDomainUrlChild(publishedChild1)
        val domainPublishedChild2 = DataModelsMapper.toDomainUrlChild(publishedChild2)

        assertChildExists(domainPublishedChild0, true)
        assertChildExists(domainPublishedChild1, true)
        assertChildExists(domainPublishedChild2, true)

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
                .assertValue(listOf(
                        domainPublishedChild0,
                        domainPublishedChild1,
                        domainPublishedChild2
                ).sortedBy { it.id }.mapIndexed { i, child -> child to i * 100 })

        listOf(domainPublishedChild0,
                domainPublishedChild1,
                domainPublishedChild2).forEach(this::deleteChild)
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
