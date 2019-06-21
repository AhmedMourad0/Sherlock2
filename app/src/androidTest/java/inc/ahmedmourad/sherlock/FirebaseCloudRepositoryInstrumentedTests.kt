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
import org.junit.After
import org.junit.Assert.*
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

        assertChildExists(child0.id, false)

        //TODO: make sure storage has the image with this link
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

        assertTrue(publishedChild0.pictureUrl.isNotBlank())

        verify(bus.state) {
            2 * { backgroundState }
        }

        verify(bus.state.backgroundState) {
            1 * { notify(ongoingState) }
            1 * { notify(successState) }
            0 * { notify(failureState) }
        }

        assertChildExists(child0.id, true)

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
    }

    @Test
    fun find_shouldReturnTheChildrenMatchingTheSpecifiedCriteria() {

        assertChildExists(child0.id, false)
        assertChildExists(child1.id, false)
        assertChildExists(child2.id, false)

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

        assertChildExists(child0.id, true)
        assertChildExists(child1.id, true)
        assertChildExists(child2.id, true)

        val domainPublishedChild0 = DataModelsMapper.toDomainUrlChild(publishedChild0)
        val domainPublishedChild1 = DataModelsMapper.toDomainUrlChild(publishedChild1)
        val domainPublishedChild2 = DataModelsMapper.toDomainUrlChild(publishedChild2)

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

//        if (true)
//            return

        //TODO: store images and make sure storage has the image with their links
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
    }

    @After
    fun clear() {
        deleteChild(child0.id)
        deleteChild(child1.id)
        deleteChild(child2.id)
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

    private fun deleteChild(id: String) {

        Completable.create {
            db.getReference(FirebaseContract.Database.PATH_CHILDREN).child(id).removeValue { error, _ ->
                if (error == null)
                    it.onComplete()
                else
                    it.onError(error.toException())
            }
        }.test().await().assertNoErrors().assertComplete()

        assertChildExists(id, false)
    }

    private fun assertChildExists(id: String, exists: Boolean) {
        Single.create<Boolean> {
            db.getReference(FirebaseContract.Database.PATH_CHILDREN)
                    .child(id)
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
}
