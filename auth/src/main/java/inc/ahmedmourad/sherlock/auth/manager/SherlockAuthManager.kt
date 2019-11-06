package inc.ahmedmourad.sherlock.auth.manager

import dagger.Lazy
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthAuthenticator
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthRemoteRepository
import inc.ahmedmourad.sherlock.domain.data.AuthManager
import inc.ahmedmourad.sherlock.domain.model.*
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

internal class SherlockAuthManager(
        private val authenticator: Lazy<AuthAuthenticator>,
        private val remoteRepository: Lazy<AuthRemoteRepository>,
        private val connectivityEnforcer: Lazy<ConnectivityManager.ConnectivityEnforcer>
) : AuthManager {

    override fun isUserSignedIn(): Single<Boolean> {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(authenticator.get().isUserSignedIn())
                .flatMap {
                    if (it)
                        authenticator.get().getCurrentUserId()
                    else
                        Single.just(null.asOptional())
                }.flatMap { (id) ->
                    if (id != null)
                        remoteRepository.get()
                                .updateUserLastLoginDate(id)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .andThen(Single.just(true))
                    else
                        Single.just(false)
                }
    }

    override fun getCurrentUser(): Single<Optional<DomainSignedInUser>> {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(authenticator.get().getCurrentUserId())
                .flatMap { (id) ->
                    if (id != null)
                        remoteRepository.get().findUser(id)
                    else
                        Single.just(null.asOptional())
                }
    }

    override fun signIn(email: String, password: String): Single<Optional<DomainSignedInUser>> {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(authenticator.get().signIn(email, password))
                .flatMap { remoteRepository.get().findUser(it) }
    }

    override fun signUp(user: DomainSignUpUser): Single<DomainSignedInUser> {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(authenticator.get().signUp(user))
                .flatMap { remoteRepository.get().storeUser(it) }
    }

    override fun signInWithGoogle(): Single<Optional<DomainSignedInUser>> {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(authenticator.get().signInWithGoogle())
                .flatMap { remoteRepository.get().findUser(it) }
    }

    override fun signInWithFacebook(): Single<Optional<DomainSignedInUser>> {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(authenticator.get().signInWithFacebook())
                .flatMap { remoteRepository.get().findUser(it) }
    }

    override fun signInWithTwitter(): Single<Optional<DomainSignedInUser>> {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(authenticator.get().signInWithTwitter())
                .flatMap { remoteRepository.get().findUser(it) }
    }

    override fun sendPasswordResetEmail(email: String): Completable {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(authenticator.get().sendPasswordResetEmail(email))
    }

    override fun signOut(): Completable {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(authenticator.get().signOut())
                .flatMapCompletable { (id) ->
                    if (id != null)
                        remoteRepository.get().updateUserLastLoginDate(id)
                    else
                        Completable.complete()
                }
    }

    class SherlockAuthEnforcer(
            private val connectivityEnforcer: Lazy<ConnectivityManager.ConnectivityEnforcer>,
            private val authenticator: Lazy<AuthAuthenticator>
    ) : AuthManager.AuthEnforcer {

        override fun requireUserSignedIn(): Completable {
            return connectivityEnforcer.get()
                    .requireInternetConnected()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .andThen(authenticator.get().requireUserSignedIn())
        }

        override fun requireUserSignedInEither(): Single<Either<Nothing?, Throwable>> {
            return connectivityEnforcer.get()
                    .requireInternetConnectedEither()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMapEither { authenticator.get().requireUserSignedInEither() }
        }
    }
}
