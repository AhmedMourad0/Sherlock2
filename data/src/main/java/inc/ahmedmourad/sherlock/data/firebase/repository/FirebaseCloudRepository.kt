package inc.ahmedmourad.sherlock.data.firebase.repository

import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import splitties.init.appCtx

//TODO: maybe inject db and storage too
class FirebaseCloudRepository(private val bus: Lazy<Bus>, private val provider: Lazy<Bus.PublishingState.Provider>) : CloudRepository {

    private val db = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance()

    init {
        if (FirebaseApp.getApps(appCtx).isEmpty()) {
            FirebaseApp.initializeApp(appCtx)
            db.setPersistenceEnabled(false)
        }
    }

    override fun publish(domainChild: DomainPictureChild): Single<DomainUrlChild> {

        val pictureChild = DataModelsMapper.toFirebasePictureChild(domainChild)

        return storeChildPicture(pictureChild)
                .flatMap(this::fetchChildPictureUrl)
                .map { FirebaseUrlChild(pictureChild, it) }
                .flatMap(this::storeChild)
                .map(DataModelsMapper::toDomainUrlChild)
                .doOnSubscribe { bus.get().state.backgroundState.notify(provider.get().ongoing()) }
                .doOnSuccess { bus.get().state.backgroundState.notify(provider.get().success()) }
                .doOnError { bus.get().state.backgroundState.notify(provider.get().failure()) }
    }

    private fun storeChildPicture(child: FirebasePictureChild): Single<StorageReference> {

        val filePath = storage.getReference(FirebaseContract.Storage.PATH_CHILDREN)
                .child("${child.id}.${FirebaseContract.Storage.FILE_FORMAT}")

        return Single.create {
            filePath.putBytes(child.picture)
                    .addOnCompleteListener { task ->
                        //TODO: this must be retriable
                        if (task.isSuccessful)
                            it.onSuccess(filePath)
                        else
                            it.onError(task.exception ?: RuntimeException())
                    }
        }
    }

    private fun fetchChildPictureUrl(filePath: StorageReference): Single<String> {
        return Single.create {
            filePath.downloadUrl.addOnCompleteListener { task ->
                //TODO: this must be retriable
                if (task.isSuccessful)
                    it.onSuccess(task.result.toString())
                else
                    it.onError(task.exception ?: RuntimeException())
            }
        }
    }

    private fun storeChild(child: FirebaseUrlChild): Single<FirebaseUrlChild> {
        return Single.create {
            db.getReference(FirebaseContract.Database.PATH_CHILDREN)
                    .child(child.id)
                    .updateChildren(child.toMap())
                    .addOnCompleteListener { task ->
                        //TODO: this must be retriable
                        if (task.isSuccessful)
                            it.onSuccess(child)
                        else
                            it.onError(task.exception ?: RuntimeException())
                    }
        }
    }

    override fun find(rules: DomainChildCriteriaRules, filter: Filter<DomainUrlChild>): Flowable<List<Pair<DomainUrlChild, Int>>> {
        return Flowable.create({
            db.getReference(FirebaseContract.Database.PATH_CHILDREN)
                    .orderByChild(FirebaseContract.Database.CHILDREN_SKIN)
                    .equalTo(rules.appearance.skin.value.toDouble())
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            it.onNext(if (dataSnapshot.exists())
                                filter.filter(dataSnapshot.children
                                        .map(DataSnapshot::extractFirebaseUrlChild)
                                        .map(DataModelsMapper::toDomainUrlChild))
                            else
                                emptyList()
                            )
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            it.onError(databaseError.toException())
                        }
                    })
        }, BackpressureStrategy.BUFFER)
    }
}

fun DataSnapshot.extractFirebaseUrlChild() = FirebaseUrlChild(
        requireNotNull(this.key) {
            "id is null!"
        },
        requireNotNull(this.child(FirebaseContract.Database.CHILDREN_PUBLICATION_DATE).value?.toString()?.toLong()) {
            "publicationDate is null!"
        }, extractFirebaseName(this),
        requireNotNull(this.child(FirebaseContract.Database.CHILDREN_NOTES).value?.toString()) {
            "notes is null!"
        },
        FirebaseLocation.parse(requireNotNull(this.child(FirebaseContract.Database.CHILDREN_LOCATION).value?.toString()) {
            "location is null!"
        }), extractFirebaseAppearance(this),
        requireNotNull(this.child(FirebaseContract.Database.CHILDREN_PICTURE_URL).value?.toString()) {
            "pictureUrl is null!"
        }
)

private fun extractFirebaseName(snapshot: DataSnapshot) = FirebaseName(
        requireNotNull(snapshot.child(FirebaseContract.Database.CHILDREN_FIRST_NAME).value?.toString()) {
            "firstName is null!"
        },
        requireNotNull(snapshot.child(FirebaseContract.Database.CHILDREN_LAST_NAME).value?.toString()) {
            "lastName is null!"
        }
)

private fun extractFirebaseAppearance(snapshot: DataSnapshot) = FirebaseAppearance(
        findEnum(requireNotNull(snapshot.child(FirebaseContract.Database.CHILDREN_GENDER).value?.toString()?.toInt()) {
            "gender is null!"
        }, Gender.values()),
        findEnum(requireNotNull(snapshot.child(FirebaseContract.Database.CHILDREN_SKIN).value?.toString()?.toInt()) {
            "skin is null!"
        }, Skin.values()),
        findEnum(requireNotNull(snapshot.child(FirebaseContract.Database.CHILDREN_HAIR).value?.toString()?.toInt()) {
            "hair is null!"
        }, Hair.values()),
        extractFirebaseAge(snapshot),
        extractFirebaseHeight(snapshot)
)

private fun extractFirebaseAge(snapshot: DataSnapshot) = FirebaseRange(
        requireNotNull(snapshot.child(FirebaseContract.Database.CHILDREN_START_AGE).value?.toString()?.toInt()) {
            "startAge is null!"
        },
        requireNotNull(snapshot.child(FirebaseContract.Database.CHILDREN_END_AGE).value?.toString()?.toInt()) {
            "endAge is null!"
        }
)


private fun extractFirebaseHeight(snapshot: DataSnapshot) = FirebaseRange(
        requireNotNull(snapshot.child(FirebaseContract.Database.CHILDREN_START_HEIGHT).value?.toString()?.toInt()) {
            "startHeight is null!"
        },
        requireNotNull(snapshot.child(FirebaseContract.Database.CHILDREN_END_HEIGHT).value?.toString()?.toInt()) {
            "endHeight is null!"
        }
)
