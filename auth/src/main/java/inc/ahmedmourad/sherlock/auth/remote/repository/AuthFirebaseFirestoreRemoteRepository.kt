package inc.ahmedmourad.sherlock.auth.remote.repository

import androidx.annotation.VisibleForTesting
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Lazy
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthImageRepository
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthRemoteRepository
import inc.ahmedmourad.sherlock.auth.remote.contract.Contract
import inc.ahmedmourad.sherlock.auth.remote.mapper.toAuthUserData
import inc.ahmedmourad.sherlock.auth.remote.model.AuthSignedInUser
import inc.ahmedmourad.sherlock.auth.remote.model.AuthUserData
import inc.ahmedmourad.sherlock.domain.data.AuthManager
import inc.ahmedmourad.sherlock.domain.model.DomainSignedInUser
import inc.ahmedmourad.sherlock.domain.model.DomainUserData
import inc.ahmedmourad.sherlock.domain.model.Optional
import inc.ahmedmourad.sherlock.domain.model.asOptional
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import splitties.init.appCtx

internal class AuthFirebaseFirestoreRemoteRepository(
        private val db: Lazy<FirebaseFirestore>,
        private val authImageRepository: Lazy<AuthImageRepository>,
        private val connectivityEnforcer: Lazy<ConnectivityManager.ConnectivityEnforcer>,
        private val authEnforcer: Lazy<AuthManager.AuthEnforcer>) : AuthRemoteRepository {

    init {
        if (FirebaseApp.getApps(appCtx).isEmpty()) {
            FirebaseApp.initializeApp(appCtx)
            db.get().firestoreSettings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build()
        }
    }

    override fun storeUser(user: DomainUserData): Single<DomainSignedInUser> {

        val registrationDate = System.currentTimeMillis()

        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(authEnforcer.get().requireUserSignedIn())
                .andThen(authImageRepository.get().storeUserPicture(user.id, user.picture))
                .flatMap { storeUserData(registrationDate, it, user.toAuthUserData()) }
                .map(AuthSignedInUser::toDomainUser)
    }

    private fun storeUserData(registrationDate: Long, pictureUrl: String, user: AuthUserData): Single<AuthSignedInUser> {

        return Single.create<AuthSignedInUser> { emitter ->

            val successListener = { _: Void ->
                emitter.onSuccess(user.toAuthSignedInUser(registrationDate, registrationDate, pictureUrl))
            }

            val failureListener = emitter::onError

            db.get().collection(Contract.Database.Users.PATH)
                    .document(user.id)
                    .set(user.toMap(registrationDate, registrationDate, pictureUrl))
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun findUser(id: String): Single<Optional<DomainSignedInUser>> {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(authEnforcer.get().requireUserSignedIn())
                .andThen(createFindUserSingle(id))
    }

    private fun createFindUserSingle(id: String): Single<Optional<DomainSignedInUser>> {

        return Single.create<Optional<DomainSignedInUser>> { emitter ->

            val snapshotListener = { snapshot: DocumentSnapshot?, exception: FirebaseFirestoreException? ->

                if (exception != null) {

                    emitter.onError(exception)

                } else if (snapshot != null) {

                    if (snapshot.exists())
                        emitter.onSuccess(snapshot.extractAuthSignedInUser().toDomainUser().asOptional())
                    else
                        emitter.onSuccess(null.asOptional())
                }
            }

            val registration = db.get().collection(Contract.Database.Users.PATH)
                    .document(id)
                    .addSnapshotListener(snapshotListener)

            emitter.setCancellable { registration.remove() }

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun updateUserLastLoginDate(id: String): Completable {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(authEnforcer.get().requireUserSignedIn())
                .andThen(createUpdateUserLastLoginDateCompletable(id))
    }

    private fun createUpdateUserLastLoginDateCompletable(id: String): Completable {

        return Completable.create { emitter ->

            val successListener = { _: Void ->
                emitter.onComplete()
            }

            val failureListener = emitter::onError

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
