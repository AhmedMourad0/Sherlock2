package inc.ahmedmourad.sherlock.auth.remote.repository

import androidx.annotation.VisibleForTesting
import arrow.core.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Lazy
import inc.ahmedmourad.sherlock.auth.manager.IsUserSignedIn
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthImageRepository
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthRemoteRepository
import inc.ahmedmourad.sherlock.auth.remote.contract.Contract
import inc.ahmedmourad.sherlock.auth.remote.mapper.toAuthUserData
import inc.ahmedmourad.sherlock.auth.remote.model.AuthSignedInUser
import inc.ahmedmourad.sherlock.auth.remote.model.AuthUserData
import inc.ahmedmourad.sherlock.domain.exceptions.NoInternetConnectionException
import inc.ahmedmourad.sherlock.domain.exceptions.NoSignedInUserException
import inc.ahmedmourad.sherlock.domain.model.DomainSignedInUser
import inc.ahmedmourad.sherlock.domain.model.DomainUserData
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import splitties.init.appCtx

internal class AuthFirebaseFirestoreRemoteRepository(
        private val db: Lazy<FirebaseFirestore>,
        private val authImageRepository: Lazy<AuthImageRepository>,
        private val connectivityManager: Lazy<ConnectivityManager>,
        private val isUserSignedIn: IsUserSignedIn
) : AuthRemoteRepository {

    init {
        if (FirebaseApp.getApps(appCtx).isEmpty()) {
            FirebaseApp.initializeApp(appCtx)
            db.get().firestoreSettings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build()
        }
    }

    override fun storeUser(user: DomainUserData): Single<Either<Throwable, DomainSignedInUser>> {

        val registrationDate = System.currentTimeMillis()

        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        isUserSignedIn().map(Boolean::right)
                    else
                        Single.just(NoInternetConnectionException().left())
                }.flatMap { isUserSignedInEither ->
                    isUserSignedInEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = {
                        if (it)
                            authImageRepository.get().storeUserPicture(user.id, user.picture)
                        else
                            Single.just(NoSignedInUserException().left())
                    })
                }.flatMap { pictureUrlEither ->
                    pictureUrlEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = {
                        storeUserData(registrationDate, it, user.toAuthUserData())
                    })
                }.map { authSignedInUserEither ->
                    authSignedInUserEither.map { it.toDomainUser() }
                }
    }

    private fun storeUserData(registrationDate: Long, pictureUrl: String, user: AuthUserData): Single<Either<Throwable, AuthSignedInUser>> {

        return Single.create<Either<Throwable, AuthSignedInUser>> { emitter ->

            val successListener = { _: Void ->
                emitter.onSuccess(user.toAuthSignedInUser(registrationDate, registrationDate, pictureUrl).right())
            }

            val failureListener = { throwable: Throwable ->
                emitter.onSuccess(throwable.left())
            }

            db.get().collection(Contract.Database.Users.PATH)
                    .document(user.id)
                    .set(user.toMap(registrationDate, registrationDate, pictureUrl))
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun findUser(id: String): Single<Either<Throwable, Option<DomainSignedInUser>>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        isUserSignedIn().map(Boolean::right)
                    else
                        Single.just(NoInternetConnectionException().left())
                }.flatMap { isUserSignedInEither ->
                    isUserSignedInEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = {
                        if (it)
                            createFindUserSingle(id)
                        else
                            Single.just(NoSignedInUserException().left())
                    })
                }
    }

    private fun createFindUserSingle(id: String): Single<Either<Throwable, Option<DomainSignedInUser>>> {

        return Single.create<Either<Throwable, Option<DomainSignedInUser>>> { emitter ->

            val snapshotListener = { snapshot: DocumentSnapshot?, exception: FirebaseFirestoreException? ->

                if (exception != null) {

                    emitter.onSuccess(exception.left())

                } else if (snapshot != null) {

                    if (snapshot.exists())
                        emitter.onSuccess(snapshot.extractAuthSignedInUser().toDomainUser().some().right())
                    else
                        emitter.onSuccess(none<DomainSignedInUser>().right())
                }
            }

            val registration = db.get().collection(Contract.Database.Users.PATH)
                    .document(id)
                    .addSnapshotListener(snapshotListener)

            emitter.setCancellable { registration.remove() }

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun updateUserLastLoginDate(id: String): Single<Either<Throwable, Unit>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        isUserSignedIn().map(Boolean::right)
                    else
                        Single.just(NoInternetConnectionException().left())
                }.flatMap { isUserSignedInEither ->
                    isUserSignedInEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = {
                        if (it)
                            createUpdateUserLastLoginDateSingle(id, db)
                        else
                            Single.just(NoSignedInUserException().left())
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

            db.get().collection(Contract.Database.Users.PATH)
                    .document(id)
                    .update(Contract.Database.Users.LAST_LOGIN_DATE, System.currentTimeMillis())
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }
}


@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun DocumentSnapshot.extractAuthSignedInUser() = AuthSignedInUser(
        requireNotNull(this.id),
        requireNotNull(this.getLong(Contract.Database.Users.REGISTRATION_DATE)),
        requireNotNull(this.getLong(Contract.Database.Users.LAST_LOGIN_DATE)),
        requireNotNull(this.getString(Contract.Database.Users.EMAIL)),
        requireNotNull(this.getString(Contract.Database.Users.NAME)),
        requireNotNull(this.getString(Contract.Database.Users.PHONE_NUMBER)),
        requireNotNull(this.getString(Contract.Database.Users.PICTURE_URL))
)
