package inc.ahmedmourad.sherlock.auth.remote.repository

import androidx.annotation.VisibleForTesting
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Lazy
import inc.ahmedmourad.sherlock.auth.manager.ObserveUserAuthState
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthRemoteRepository
import inc.ahmedmourad.sherlock.auth.model.AuthRetrievedUserDetails
import inc.ahmedmourad.sherlock.auth.model.AuthStoredUserDetails
import inc.ahmedmourad.sherlock.auth.remote.contract.RemoteContract
import inc.ahmedmourad.sherlock.auth.remote.mapper.toRemoteUserDetails
import inc.ahmedmourad.sherlock.auth.remote.model.RemoteRetrievedUserDetails
import inc.ahmedmourad.sherlock.auth.remote.model.RemoteStoredUserDetails
import inc.ahmedmourad.sherlock.domain.exceptions.NoInternetConnectionException
import inc.ahmedmourad.sherlock.domain.exceptions.NoSignedInUserException
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import splitties.init.appCtx

internal class AuthFirebaseFirestoreRemoteRepository(
        private val db: Lazy<FirebaseFirestore>,
        private val connectivityManager: Lazy<ConnectivityManager>,
        private val observeUserAuthState: ObserveUserAuthState
) : AuthRemoteRepository {

    init {
        if (FirebaseApp.getApps(appCtx).isEmpty()) {
            FirebaseApp.initializeApp(appCtx)
            db.get().firestoreSettings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build()
        }
    }

    override fun storeUserDetails(details: AuthStoredUserDetails): Single<Either<Throwable, AuthRetrievedUserDetails>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected) {
                        observeUserAuthState().map(Boolean::right).singleOrError()
                    } else {
                        Single.just(NoInternetConnectionException().left())
                    }
                }.flatMap { isUserSignedInEither ->
                    isUserSignedInEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = { isUserSignedIn ->
                        if (isUserSignedIn) {
                            store(details.toRemoteUserDetails())
                        } else {
                            Single.just(NoSignedInUserException().left())
                        }
                    })
                }
    }

    private fun store(details: RemoteStoredUserDetails): Single<Either<Throwable, AuthRetrievedUserDetails>> {

        return Single.create<Either<Throwable, AuthRetrievedUserDetails>> { emitter ->

            val registrationDate = System.currentTimeMillis()
            val successListener = { _: Void ->
                emitter.onSuccess(details.toRemoteRetrievedUserDetails(registrationDate).toAuthUserDetails().right())
            }

            val failureListener = { throwable: Throwable ->
                emitter.onSuccess(throwable.left())
            }

            db.get().collection(RemoteContract.Database.Users.PATH)
                    .document(details.id)
                    .set(details.toMap())
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun findUser(id: String): Flowable<Either<Throwable, AuthRetrievedUserDetails?>> {
        return connectivityManager.get()
                .observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected) {
                        observeUserAuthState().map(Boolean::right)
                    } else {
                        Flowable.just(NoInternetConnectionException().left())
                    }
                }.flatMap { isUserSignedInEither ->
                    isUserSignedInEither.fold(ifLeft = {
                        Flowable.just(it.left())
                    }, ifRight = { isUserSignedIn ->
                        if (isUserSignedIn) {
                            createFindUserFlowable(id)
                        } else {
                            Flowable.just(NoSignedInUserException().left())
                        }
                    })
                }
    }

    private fun createFindUserFlowable(id: String): Flowable<Either<Throwable, AuthRetrievedUserDetails?>> {

        return Flowable.create<Either<Throwable, AuthRetrievedUserDetails?>>({ emitter ->

            val snapshotListener = { snapshot: DocumentSnapshot?, exception: FirebaseFirestoreException? ->

                if (exception != null) {

                    emitter.onNext(exception.left())

                } else if (snapshot != null) {

                    if (snapshot.exists()) {
                        emitter.onNext(snapshot.extractRemoteRetrievedUserDetails().toAuthUserDetails().right())
                    } else {
                        emitter.onNext(null.right())
                    }
                }
            }

            val registration = db.get().collection(RemoteContract.Database.Users.PATH)
                    .document(id)
                    .addSnapshotListener(snapshotListener)

            emitter.setCancellable { registration.remove() }

        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun updateUserLastLoginDate(id: String): Single<Either<Throwable, Unit>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected) {
                        observeUserAuthState().map(Boolean::right).singleOrError()
                    } else {
                        Single.just(NoInternetConnectionException().left())
                    }
                }.flatMap { isUserSignedInEither ->
                    isUserSignedInEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = { isUserSignedIn ->
                        if (isUserSignedIn) {
                            createUpdateUserLastLoginDateSingle(id, db)
                        } else {
                            Single.just(NoSignedInUserException().left())
                        }
                    })
                }
    }

    private fun createUpdateUserLastLoginDateSingle(
            id: String,
            db: Lazy<FirebaseFirestore>
    ): Single<Either<Throwable, Unit>> {

        return Single.create<Either<Throwable, Unit>> { emitter ->

            val successListener = { _: Void ->
                emitter.onSuccess(Unit.right())
            }

            val failureListener = { throwable: Throwable ->
                emitter.onSuccess(throwable.left())
            }

            db.get().collection(RemoteContract.Database.Users.PATH)
                    .document(id)
                    .update(RemoteContract.Database.Users.LAST_LOGIN_DATE, System.currentTimeMillis())
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun DocumentSnapshot.extractRemoteRetrievedUserDetails() = RemoteRetrievedUserDetails(
        requireNotNull(this.id),
        requireNotNull(this.getTimestamp(RemoteContract.Database.Users.REGISTRATION_DATE)?.seconds) * 1000L,
        requireNotNull(this.getString(RemoteContract.Database.Users.PHONE_NUMBER))
)
