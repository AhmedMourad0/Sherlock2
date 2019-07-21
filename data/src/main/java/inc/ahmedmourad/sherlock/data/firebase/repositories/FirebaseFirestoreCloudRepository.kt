package inc.ahmedmourad.sherlock.data.firebase.repositories

import androidx.annotation.VisibleForTesting
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Lazy
import inc.ahmedmourad.sherlock.data.firebase.contract.FirebaseContract
import inc.ahmedmourad.sherlock.data.firebase.model.*
import inc.ahmedmourad.sherlock.data.mapper.DataModelsMapper
import inc.ahmedmourad.sherlock.data.repositories.CloudRepository
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.constants.findEnum
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.DomainPictureChild
import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import inc.ahmedmourad.sherlock.domain.model.Optional
import inc.ahmedmourad.sherlock.domain.model.asOptional
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import splitties.init.appCtx

class FirebaseFirestoreCloudRepository(
        private val db: Lazy<FirebaseFirestore>,
        private val storage: Lazy<FirebaseStorage>,
        private val bus: Lazy<Bus>) : CloudRepository {

    init {
        if (FirebaseApp.getApps(appCtx).isEmpty()) {
            FirebaseApp.initializeApp(appCtx)
            db.get().firestoreSettings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build()
        }
    }

    override fun publish(domainChild: DomainPictureChild): Single<DomainUrlChild> {

        val pictureChild = DataModelsMapper.toFirebasePictureChild(domainChild)

        return storeChildPicture(pictureChild)
                .flatMap(this::fetchChildPictureUrl)
                .map { FirebaseUrlChild(pictureChild, it) }
                .flatMap(this::storeChild)
                .map(DataModelsMapper::toDomainUrlChild)
                .doOnSubscribe { bus.get().publishingState.notify(Bus.PublishingState.ONGOING) }
                .doOnSuccess { bus.get().publishingState.notify(Bus.PublishingState.SUCCESS) }
                .doOnError { bus.get().publishingState.notify(Bus.PublishingState.FAILURE) }
    }

    private fun storeChildPicture(child: FirebasePictureChild): Single<StorageReference> {

        val filePath = storage.get().getReference(FirebaseContract.Storage.PATH_CHILDREN)
                .child("${child.id}.${FirebaseContract.Storage.FILE_FORMAT}")

        return Single.create { emitter ->
            filePath.putBytes(child.picture)
                    .addOnSuccessListener {
                        emitter.onSuccess(filePath)
                    }.addOnFailureListener {
                        emitter.onError(it)
                    }
        }
    }

    private fun fetchChildPictureUrl(filePath: StorageReference): Single<String> {
        return Single.create { emitter ->
            filePath.downloadUrl
                    .addOnSuccessListener {
                        emitter.onSuccess(it.toString())
                    }.addOnFailureListener {
                        emitter.onError(it)
                    }
        }
    }

    private fun storeChild(child: FirebaseUrlChild): Single<FirebaseUrlChild> {
        return Single.create { emitter ->
            db.get().collection(FirebaseContract.Database.PATH_CHILDREN)
                    .document(child.id)
                    .set(child.toMap())
                    .addOnSuccessListener {
                        emitter.onSuccess(child)
                    }.addOnFailureListener {
                        emitter.onError(it)
                    }
        }
    }

    override fun find(childId: String): Flowable<Optional<DomainUrlChild>> {
        return Flowable.create({ emitter ->
            db.get().collection(FirebaseContract.Database.PATH_CHILDREN)
                    .document(childId)
                    .addSnapshotListener { snapshot, exception ->

                        if (exception != null) {

                            emitter.onError(exception)
                            return@addSnapshotListener

                        } else if (snapshot != null) {

                            emitter.onNext((if (snapshot.exists())
                                DataModelsMapper.toDomainUrlChild(snapshot.extractFirebaseUrlChild())
                            else
                                null
                                    ).asOptional())
                        }
                    }
        }, BackpressureStrategy.BUFFER)
    }

    //TODO: this needs to be replaced with Google's BigQuery
    override fun findAll(rules: DomainChildCriteriaRules, filter: Filter<DomainUrlChild>): Flowable<List<Pair<DomainUrlChild, Int>>> {
        return Flowable.create({ emitter ->
            db.get().collection(FirebaseContract.Database.PATH_CHILDREN)
                    .whereEqualTo(FirebaseContract.Database.CHILDREN_SKIN, rules.appearance.skin.value)
                    .whereEqualTo(FirebaseContract.Database.CHILDREN_GENDER, rules.appearance.gender.value)
                    .whereEqualTo(FirebaseContract.Database.CHILDREN_HAIR, rules.appearance.hair.value)
                    .addSnapshotListener { snapshot, exception ->

                        if (exception != null) {

                            emitter.onError(exception)
                            return@addSnapshotListener

                        } else if (snapshot != null) {

                            emitter.onNext(filter.filter(
                                    snapshot.documents
                                            .filter { it.exists() }
                                            .map(DocumentSnapshot::extractFirebaseUrlChild)
                                            .map(DataModelsMapper::toDomainUrlChild)

                            ))
                        }
                    }
        }, BackpressureStrategy.BUFFER)
    }
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun DocumentSnapshot.extractFirebaseUrlChild() = FirebaseUrlChild(
        requireNotNull(this.id) {
            "id is null!"
        },
        requireNotNull(this.getLong(FirebaseContract.Database.CHILDREN_PUBLICATION_DATE)) {
            "publicationDate is null!"
        }, extractFirebaseName(this),
        requireNotNull(this.getString(FirebaseContract.Database.CHILDREN_NOTES)) {
            "notes is null!"
        },
        FirebaseLocation.parse(requireNotNull(this.getString(FirebaseContract.Database.CHILDREN_LOCATION)) {
            "location is null!"
        }), extractFirebaseAppearance(this),
        requireNotNull(this.getString(FirebaseContract.Database.CHILDREN_PICTURE_URL)) {
            "pictureUrl is null!"
        }
)

private fun extractFirebaseName(snapshot: DocumentSnapshot) = FirebaseName(
        requireNotNull(snapshot.getString(FirebaseContract.Database.CHILDREN_FIRST_NAME)) {
            "firstName is null!"
        },
        requireNotNull(snapshot.getString(FirebaseContract.Database.CHILDREN_LAST_NAME)) {
            "lastName is null!"
        }
)

private fun extractFirebaseAppearance(snapshot: DocumentSnapshot) = FirebaseAppearance(
        findEnum(requireNotNull(snapshot.getLong(FirebaseContract.Database.CHILDREN_GENDER)?.toInt()) {
            "gender is null!"
        }, Gender.values()),
        findEnum(requireNotNull(snapshot.getLong(FirebaseContract.Database.CHILDREN_SKIN)?.toInt()) {
            "skin is null!"
        }, Skin.values()),
        findEnum(requireNotNull(snapshot.getLong(FirebaseContract.Database.CHILDREN_HAIR)?.toInt()) {
            "hair is null!"
        }, Hair.values()),
        extractFirebaseAge(snapshot),
        extractFirebaseHeight(snapshot)
)

private fun extractFirebaseAge(snapshot: DocumentSnapshot) = FirebaseRange(
        requireNotNull(snapshot.getLong(FirebaseContract.Database.CHILDREN_START_AGE)?.toInt()) {
            "startAge is null!"
        },
        requireNotNull(snapshot.getLong(FirebaseContract.Database.CHILDREN_END_AGE)?.toInt()) {
            "endAge is null!"
        }
)


private fun extractFirebaseHeight(snapshot: DocumentSnapshot) = FirebaseRange(
        requireNotNull(snapshot.getLong(FirebaseContract.Database.CHILDREN_START_HEIGHT)?.toInt()) {
            "startHeight is null!"
        },
        requireNotNull(snapshot.getLong(FirebaseContract.Database.CHILDREN_END_HEIGHT)?.toInt()) {
            "endHeight is null!"
        }
)
