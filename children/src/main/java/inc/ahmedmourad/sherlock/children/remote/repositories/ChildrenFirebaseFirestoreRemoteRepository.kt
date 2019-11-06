package inc.ahmedmourad.sherlock.children.remote.repositories

import androidx.annotation.VisibleForTesting
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import dagger.Lazy
import inc.ahmedmourad.sherlock.children.remote.contract.Contract
import inc.ahmedmourad.sherlock.children.remote.mapper.toFirebaseChildCriteriaRules
import inc.ahmedmourad.sherlock.children.remote.mapper.toFirebasePublishedChild
import inc.ahmedmourad.sherlock.children.remote.mapper.toFirebaseSimpleChild
import inc.ahmedmourad.sherlock.children.remote.model.*
import inc.ahmedmourad.sherlock.children.repository.dependencies.ChildrenImageRepository
import inc.ahmedmourad.sherlock.children.repository.dependencies.ChildrenRemoteRepository
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.constants.findEnum
import inc.ahmedmourad.sherlock.domain.data.AuthManager
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.*
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import splitties.init.appCtx
import java.util.*

internal class ChildrenFirebaseFirestoreRemoteRepository(
        private val db: Lazy<FirebaseFirestore>,
        private val childrenImageRepository: Lazy<ChildrenImageRepository>,
        private val authEnforcer: Lazy<AuthManager.AuthEnforcer>,
        private val connectivityEnforcer: Lazy<ConnectivityManager.ConnectivityEnforcer>) : ChildrenRemoteRepository {

    init {
        if (FirebaseApp.getApps(appCtx).isEmpty()) {
            FirebaseApp.initializeApp(appCtx)
            db.get().firestoreSettings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build()
        }
    }

    override fun publish(domainChild: DomainPublishedChild): Single<DomainRetrievedChild> {

        val childId = UUID.randomUUID().toString()
        val publicationDate = System.currentTimeMillis()
        val publishedChild = domainChild.toFirebasePublishedChild()

        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(authEnforcer.get().requireUserSignedIn())
                .andThen(childrenImageRepository.get().storeChildPicture(childId, publishedChild.picture))
                .flatMap { publishChildData(childId, publicationDate, it, publishedChild) }
                .map(FirebaseRetrievedChild::toDomainChild)
    }

    private fun publishChildData(childId: String, publicationDate: Long, pictureUrl: String, child: FirebasePublishedChild): Single<FirebaseRetrievedChild> {

        return Single.create<FirebaseRetrievedChild> { emitter ->

            val successListener = { _: Void ->
                emitter.onSuccess(child.toFirebaseRetrievedChild(childId, publicationDate, pictureUrl))
            }

            val failureListener = emitter::onError

            db.get().collection(Contract.Database.Children.PATH)
                    .document(childId)
                    .set(child.toMap(publicationDate, pictureUrl))
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun find(child: DomainSimpleRetrievedChild): Flowable<Either<DomainRetrievedChild?, Throwable>> {
        return connectivityEnforcer.get()
                .requireInternetConnectedEither()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapEither { authEnforcer.get().requireUserSignedInEither() }
                .toFlowable()
                .flatMapEither { createFindFlowable(child) }
    }

    private fun createFindFlowable(child: DomainSimpleRetrievedChild): Flowable<Either<DomainRetrievedChild?, Throwable>> {

        return Flowable.create<Either<DomainRetrievedChild?, Throwable>>({ emitter ->

            val snapshotListener = { snapshot: DocumentSnapshot?, exception: FirebaseFirestoreException? ->

                if (exception != null) {

                    emitter.onNext(exception.asEither())

                } else if (snapshot != null) {

                    if (snapshot.exists())
                        emitter.onNext(Either.Value(snapshot.extractFirebaseRetrievedChild().toDomainChild()))
                    else
                        emitter.onNext(Either.NULL)
                }
            }

            val registration = db.get().collection(Contract.Database.Children.PATH)
                    .document(child.toFirebaseSimpleChild().id)
                    .addSnapshotListener(snapshotListener)

            emitter.setCancellable { registration.remove() }

        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    //TODO: this needs to be replaced with Google's BigQuery
    override fun findAll(rules: DomainChildCriteriaRules, filter: Filter<DomainRetrievedChild>): Flowable<Either<List<Pair<DomainRetrievedChild, Int>>, Throwable>> {
        return connectivityEnforcer.get()
                .requireInternetConnectedEither()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapEither { authEnforcer.get().requireUserSignedInEither() }
                .toFlowable()
                .flatMapEither { createFindAllFlowable(rules, filter) }
    }

    private fun createFindAllFlowable(rules: DomainChildCriteriaRules, filter: Filter<DomainRetrievedChild>): Flowable<Either<List<Pair<DomainRetrievedChild, Int>>, Throwable>> {

        val firebaseRules = rules.toFirebaseChildCriteriaRules()

        return Flowable.create<Either<List<Pair<DomainRetrievedChild, Int>>, Throwable>>({ emitter ->

            val snapshotListener = { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->

                if (exception != null) {

                    emitter.onNext(exception.asEither())

                } else if (snapshot != null) {

                    emitter.onNext(Either.Value(
                            filter.filter(snapshot.documents
                                    .filter(DocumentSnapshot::exists)
                                    .map(DocumentSnapshot::extractFirebaseRetrievedChild)
                                    .map(FirebaseRetrievedChild::toDomainChild)

                            )
                    ))
                }
            }

            val registration = db.get().collection(Contract.Database.Children.PATH)
                    .whereEqualTo(Contract.Database.Children.SKIN, firebaseRules.appearance.skin.value)
                    .whereEqualTo(Contract.Database.Children.GENDER, firebaseRules.appearance.gender.value)
                    .whereEqualTo(Contract.Database.Children.HAIR, firebaseRules.appearance.hair.value)
                    .addSnapshotListener(snapshotListener)

            emitter.setCancellable { registration.remove() }

        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun clear(): Completable {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(authEnforcer.get().requireUserSignedIn())
                .andThen(deleteChildren())
    }

    private fun deleteChildren(): Completable {

        return Completable.create { emitter ->

            val successListener = { snapshot: QuerySnapshot ->
                snapshot.documents.forEach { it.reference.delete() }
            }

            val failureListener = emitter::onError

            db.get()
                    .collection(Contract.Database.Children.PATH)
                    .get()
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)
        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun DocumentSnapshot.extractFirebaseRetrievedChild() = FirebaseRetrievedChild(
        requireNotNull(this.id),
        requireNotNull(this.getLong(Contract.Database.Children.PUBLICATION_DATE)),
        extractFirebaseName(this),
        requireNotNull(this.getString(Contract.Database.Children.NOTES)),
        FirebaseLocation.parse(requireNotNull(this.getString(Contract.Database.Children.LOCATION))),
        extractFirebaseAppearance(this),
        requireNotNull(this.getString(Contract.Database.Children.PICTURE_URL))
)

private fun extractFirebaseName(snapshot: DocumentSnapshot) = FirebaseName(
        requireNotNull(snapshot.getString(Contract.Database.Children.FIRST_NAME)),
        requireNotNull(snapshot.getString(Contract.Database.Children.LAST_NAME))
)

private fun extractFirebaseAppearance(snapshot: DocumentSnapshot) = FirebaseEstimatedAppearance(
        findEnum(requireNotNull(snapshot.getLong(Contract.Database.Children.GENDER)?.toInt()), Gender.values()),
        findEnum(requireNotNull(snapshot.getLong(Contract.Database.Children.SKIN)?.toInt()), Skin.values()),
        findEnum(requireNotNull(snapshot.getLong(Contract.Database.Children.HAIR)?.toInt()), Hair.values()),
        extractFirebaseAge(snapshot),
        extractFirebaseHeight(snapshot)
)

private fun extractFirebaseAge(snapshot: DocumentSnapshot) = FirebaseRange(
        requireNotNull(snapshot.getLong(Contract.Database.Children.START_AGE)?.toInt()),
        requireNotNull(snapshot.getLong(Contract.Database.Children.END_AGE)?.toInt())
)

private fun extractFirebaseHeight(snapshot: DocumentSnapshot) = FirebaseRange(
        requireNotNull(snapshot.getLong(Contract.Database.Children.START_HEIGHT)?.toInt()),
        requireNotNull(snapshot.getLong(Contract.Database.Children.END_HEIGHT)?.toInt())
)
