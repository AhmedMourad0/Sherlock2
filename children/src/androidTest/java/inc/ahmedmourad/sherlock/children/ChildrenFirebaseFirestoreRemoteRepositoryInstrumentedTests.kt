package inc.ahmedmourad.sherlock.children

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import dagger.Lazy
import inc.ahmedmourad.sherlock.children.images.repository.ChildrenFirebaseStorageImageRepository
import inc.ahmedmourad.sherlock.children.remote.contract.Contract
import inc.ahmedmourad.sherlock.children.remote.repositories.ChildrenFirebaseFirestoreRemoteRepository
import inc.ahmedmourad.sherlock.children.remote.repositories.extractFirebaseRetrievedChild
import inc.ahmedmourad.sherlock.children.repository.dependencies.ChildrenRemoteRepository
import inc.ahmedmourad.sherlock.children.utils.getImageBytes
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.model.children.*
import inc.ahmedmourad.sherlock.domain.model.children.Range
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
class ChildrenFirebaseFirestoreRemoteRepositoryInstrumentedTests {

    private lateinit var repository: ChildrenRemoteRepository

    private val child0 = PublishedChild(
            FullName("Ahmed", "Mourad"),
            "",
            Location("1", "a", "a", Coordinates(90.0, 140.0)),
            ApproximateAppearance(
                    Gender.MALE,
                    Skin.WHEAT,
                    Hair.DARK,
                    Range(18, 22),
                    Range(170, 190)
            ), getImageBytes(R.drawable.test))

    private val child1 = PublishedChild(
            FullName("Yasmeen", "Mourad"),
            "",
            Location("11", "sh", "hs", Coordinates(70.0, 120.0)),
            ApproximateAppearance(
                    Gender.FEMALE,
                    Skin.WHITE,
                    Hair.DARK,
                    Range(16, 21),
                    Range(160, 180)
            ), getImageBytes(R.drawable.test))

    private val child2 = PublishedChild(
            FullName("Ahmed", "Shamakh"),
            "",
            Location("111", "b", "bb", Coordinates(55.0, 99.0)),
            ApproximateAppearance(
                    Gender.MALE,
                    Skin.DARK,
                    Hair.BROWN,
                    Range(11, 23),
                    Range(150, 180)
            ), getImageBytes(R.drawable.test))

    @Before
    fun setup() {
        Timber.plant(Timber.DebugTree())
        setupRepository()
    }

    private fun setupRepository() {

        repository = ChildrenFirebaseFirestoreRemoteRepository(
                Lazy { FirebaseFirestore.getInstance() },
                Lazy { ChildrenFirebaseStorageImageRepository(Lazy { FirebaseStorage.getInstance() }) }
        )
        repository.clear().test().await().assertNoErrors().assertComplete()
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
        ).map {
            it to repository.publish(it)
                    .test()
                    .await()
                    .assertComplete()
                    .assertNoErrors()
                    .values()[0]
        }.onEach { (publishedChild, retrievedChild) ->
            assertEquals(retrievedChild, RetrievedChild(
                    retrievedChild.id,
                    retrievedChild.publicationDate,
                    publishedChild.name,
                    publishedChild.notes,
                    publishedChild.location,
                    publishedChild.appearance,
                    retrievedChild.pictureUrl
            ))
        }.map { (_, publishedChild) ->
            publishedChild
        }.onEach {
            assertPictureUrlCorrect(it, it.pictureUrl)
        }.forEach {
            assertChildCorrect(it)
            assertPictureExists(it, true)
        }
    }

    @Test
    fun find_shouldReturnTheChildWithTheSpecifiedId() {

        sequenceOf(
                child0,
                child1,
                child2
        ).map {
            repository.publish(it)
                    .test()
                    .await()
                    .assertComplete()
                    .assertNoErrors()
                    .values()[0]
        }.onEach {
            assertChildCorrect(it)
        }.forEach {
            repository.find(it.simplify())
                    .test()
                    .awaitCount(1)
                    .assertNotComplete()
                    .assertNoErrors()
                    .assertValue(Either.Value(it))
        }

        repository.find(SimpleRetrievedChild(
                UUID.randomUUID().toString(),
                0L,
                FullName("", ""),
                "",
                "",
                "",
                ""
        )).test()
                .awaitCount(1)
                .assertNotComplete()
                .assertNoErrors()
                .assertValue(Either.NULL)
    }

    @Test
    fun findAll_shouldReturnTheChildrenMatchingTheSpecifiedCriteria() {

        val domainPublishedChildren = sequenceOf(
                child0,
                child1,
                child2
        ).map {
            it.copy(appearance = it.appearance.copy(skin = Skin.WHITE))
        }.map {
            repository.publish(it)
                    .test()
                    .await()
                    .assertComplete()
                    .assertNoErrors()
                    .values()[0]
        }.onEach {
            assertChildCorrect(it)
        }.toList()

        val filter = mock<Filter<RetrievedChild>> {
            on {
                filter(any())
            } doAnswer { param ->
                param.getArgument<List<RetrievedChild>>(0).sortedBy { it.id }.mapIndexed { i, child -> child to i * 100 }
            }
        }

        val rules = ChildQuery(
                FullName("", ""),
                Location("", "", "", Coordinates(0.0, 0.0)),
                ExactAppearance(
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
                .assertValue(Either.Value(domainPublishedChildren.sortedBy { it.id }.mapIndexed { i, child -> child to i * 100 }))
    }

    @Test
    fun clear_shouldDeleteAllTheRecordsInTheDatabase() {

        val publishedChildren = sequenceOf(
                child0,
                child1,
                child2
        ).map {
            repository.publish(it)
                    .test()
                    .await()
                    .assertComplete()
                    .assertNoErrors()
                    .values()[0]
        }.onEach {
            assertChildCorrect(it)
        }.toList()

        repository.clear()
                .test()
                .await()
                .assertComplete()
                .assertNoErrors()

        publishedChildren.forEach { assertChildExists(it, false) }
    }

    @After
    fun clear() {
        repository.clear().test().await().assertNoErrors().assertComplete()
    }

    private fun assertChildExists(child: RetrievedChild, exists: Boolean) {
        Single.create<Boolean> {
            FirebaseFirestore.getInstance()
                    .collection(Contract.Database.Children.PATH)
                    .document(child.id)
                    .addSnapshotListener { snapshot, exception ->

                        if (snapshot != null)
                            it.onSuccess(snapshot.exists())

                        if (exception != null)
                            it.onError(exception)
                    }
        }.test().await().assertComplete().assertNoErrors().assertValue(exists)
    }

    private fun assertPictureExists(child: RetrievedChild, exists: Boolean) {
        Single.create<Boolean> {
            FirebaseStorage.getInstance()
                    .getReference(Contract.Database.Children.PATH)
                    .child("${child.id}.${inc.ahmedmourad.sherlock.children.images.contract.Contract.FILE_FORMAT}")
                    .downloadUrl
                    .addOnCompleteListener { task ->
                        it.onSuccess(task.isSuccessful)
                    }
        }.test().await().assertComplete().assertNoErrors().assertValue(exists)
    }

    private fun assertChildCorrect(child: RetrievedChild) {
        Single.create<RetrievedChild> {
            FirebaseFirestore.getInstance()
                    .collection(Contract.Database.Children.PATH)
                    .document(child.id)
                    .get()
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            val result = task.result?.extractFirebaseRetrievedChild()

                            if (result == null) {
                                it.onError(IllegalArgumentException("DocumentSnapshot is null!"))
                                return@addOnCompleteListener
                            }

                            it.onSuccess(result.toDomainChild())
                        } else {
                            it.onError(task.exception
                                    ?: IllegalArgumentException("Exception is null!"))
                        }
                    }
        }.test().await().assertComplete().assertNoErrors().assertValue(child)
    }

    private fun assertPictureUrlCorrect(child: RetrievedChild, url: String) {
        Single.create<Boolean> {
            FirebaseStorage.getInstance()
                    .getReference(Contract.Database.Children.PATH)
                    .child("${child.id}.${inc.ahmedmourad.sherlock.children.images.contract.Contract.FILE_FORMAT}")
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
