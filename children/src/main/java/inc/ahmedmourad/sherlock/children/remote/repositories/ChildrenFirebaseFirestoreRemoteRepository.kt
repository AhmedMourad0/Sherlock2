package inc.ahmedmourad.sherlock.children.remote.repositories

import androidx.annotation.VisibleForTesting
import arrow.core.*
import com.google.android.gms.tasks.Tasks
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
import inc.ahmedmourad.sherlock.domain.exceptions.NoInternetConnectionException
import inc.ahmedmourad.sherlock.domain.exceptions.NoSignedInUserException
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.DomainPublishedChild
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import splitties.init.appCtx
import java.util.*

internal class ChildrenFirebaseFirestoreRemoteRepository(
        private val db: Lazy<FirebaseFirestore>,
        private val childrenImageRepository: Lazy<ChildrenImageRepository>,
        private val authManager: Lazy<AuthManager>,
        private val connectivityManager: Lazy<ConnectivityManager>
) : ChildrenRemoteRepository {

    init {
        if (FirebaseApp.getApps(appCtx).isEmpty()) {
            FirebaseApp.initializeApp(appCtx)
            db.get().firestoreSettings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build()
        }
    }

    override fun publish(domainChild: DomainPublishedChild): Single<Either<Throwable, DomainRetrievedChild>> {

        val childId = UUID.randomUUID().toString()
        val publicationDate = System.currentTimeMillis()
        val publishedChild = domainChild.toFirebasePublishedChild()

        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        authManager.get().isUserSignedIn().map(Boolean::right)
                    else
                        Single.just(NoInternetConnectionException().left())
                }.flatMap { isUserSignedInEither ->
                    isUserSignedInEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = {
                        if (it)
                            childrenImageRepository.get().storeChildPicture(childId, publishedChild.picture)
                        else
                            Single.just(NoSignedInUserException().left())
                    })
                }.flatMap { filePathEither ->
                    filePathEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = {
                        publishChildData(childId, publicationDate, it, publishedChild)
                    })
                }.map { childEither ->
                    childEither.map(FirebaseRetrievedChild::toDomainChild)
                }
    }

    private fun publishChildData(
            childId: String,
            publicationDate: Long,
            pictureUrl: String,
            child: FirebasePublishedChild
    ): Single<Either<Throwable, FirebaseRetrievedChild>> {

        return Single.create<Either<Throwable, FirebaseRetrievedChild>> { emitter ->

            val successListener = { _: Void ->
                emitter.onSuccess(child.toFirebaseRetrievedChild(childId, publicationDate, pictureUrl).right())
            }

            val failureListener = { throwable: Throwable ->
                emitter.onSuccess(throwable.left())
            }

            db.get().collection(Contract.Database.Children.PATH)
                    .document(childId)
                    .set(child.toMap(publicationDate, pictureUrl))
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun find(
            child: DomainSimpleRetrievedChild
    ): Flowable<Either<Throwable, Option<DomainRetrievedChild>>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        authManager.get().isUserSignedIn().map(Boolean::right)
                    else
                        Single.just(NoInternetConnectionException().left())
                }.toFlowable()
                .flatMap { isUserSignedInEither ->
                    isUserSignedInEither.fold(ifLeft = {
                        Flowable.just(it.left())
                    }, ifRight = {
                        if (it)
                            createFindFlowable(child)
                        else
                            Flowable.just(NoSignedInUserException().left())
                    })
                }
    }

    private fun createFindFlowable(
            child: DomainSimpleRetrievedChild
    ): Flowable<Either<Throwable, Option<DomainRetrievedChild>>> {

        return Flowable.create<Either<Throwable, Option<DomainRetrievedChild>>>({ emitter ->

            val snapshotListener = { snapshot: DocumentSnapshot?, exception: FirebaseFirestoreException? ->

                if (exception != null) {

                    emitter.onNext(exception.left())

                } else if (snapshot != null) {

                    if (snapshot.exists())
                        emitter.onNext(snapshot.extractFirebaseRetrievedChild().toDomainChild().some().right())
                    else
                        emitter.onNext(none<DomainRetrievedChild>().right())
                }
            }

            val registration = db.get().collection(Contract.Database.Children.PATH)
                    .document(child.toFirebaseSimpleChild().id)
                    .addSnapshotListener(snapshotListener)

            emitter.setCancellable { registration.remove() }

        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    //TODO: this needs to be replaced with Google's BigQuery
    override fun findAll(
            rules: DomainChildCriteriaRules,
            filter: Filter<DomainRetrievedChild>
    ): Flowable<Either<Throwable, List<Tuple2<DomainRetrievedChild, Int>>>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        authManager.get().isUserSignedIn().map(Boolean::right)
                    else
                        Single.just(NoInternetConnectionException().left())
                }.toFlowable()
                .flatMap { isUserSignedInEither ->
                    isUserSignedInEither.fold(ifLeft = {
                        Flowable.just(it.left())
                    }, ifRight = {
                        if (it)
                            createFindAllFlowable(rules, filter)
                        else
                            Flowable.just(NoSignedInUserException().left())
                    })
                }
    }

    private fun createFindAllFlowable(
            rules: DomainChildCriteriaRules,
            filter: Filter<DomainRetrievedChild>
    ): Flowable<Either<Throwable, List<Tuple2<DomainRetrievedChild, Int>>>> {

        val firebaseRules = rules.toFirebaseChildCriteriaRules()

        return Flowable.create<Either<Throwable, List<Tuple2<DomainRetrievedChild, Int>>>>({ emitter ->

            val snapshotListener = { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->

                if (exception != null) {

                    emitter.onNext(exception.left())

                } else if (snapshot != null) {

                    emitter.onNext(filter.filter(snapshot.documents
                            .filter(DocumentSnapshot::exists)
                            .map(DocumentSnapshot::extractFirebaseRetrievedChild)
                            .map(FirebaseRetrievedChild::toDomainChild)
                    ).right())
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

    override fun clear(): Single<Either<Throwable, Unit>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        authManager.get().isUserSignedIn().map(Boolean::right)
                    else
                        Single.just(NoInternetConnectionException().left())
                }.flatMap { isUserSignedInEither ->
                    isUserSignedInEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = {
                        if (it)
                            deleteChildren()
                        else
                            Single.just(NoSignedInUserException().left())
                    })
                }
    }

    private fun deleteChildren(): Single<Either<Throwable, Unit>> {

        return Single.create<Either<Throwable, Unit>> { emitter ->

            val successListener = { _: Void ->
                emitter.onSuccess(Unit.right())
            }

            val failureListener = { throwable: Throwable ->
                emitter.onSuccess(throwable.left())
            }

            val querySuccessListener: (QuerySnapshot) -> Unit = { snapshot: QuerySnapshot ->
                Tasks.whenAll(snapshot.documents.map { it.reference.delete() })
                        .addOnSuccessListener(successListener)
                        .addOnFailureListener(failureListener)
            }

            db.get().collection(Contract.Database.Children.PATH)
                    .get()
                    .addOnSuccessListener(querySuccessListener)
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
