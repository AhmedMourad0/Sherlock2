package inc.ahmedmourad.sherlock.auth.manager

import arrow.core.*
import dagger.Lazy
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthAuthenticator
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthRemoteRepository
import inc.ahmedmourad.sherlock.domain.data.AuthManager
import inc.ahmedmourad.sherlock.domain.exceptions.NoInternetConnectionException
import inc.ahmedmourad.sherlock.domain.model.DomainSignUpUser
import inc.ahmedmourad.sherlock.domain.model.DomainSignedInUser
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

internal typealias IsUserSignedIn = () -> @JvmSuppressWildcards Single<Boolean>

internal class SherlockAuthManager(
        private val authenticator: Lazy<AuthAuthenticator>,
        private val remoteRepository: Lazy<AuthRemoteRepository>,
        private val connectivityManager: Lazy<ConnectivityManager>,
        private val isUserSignedIn: IsUserSignedIn
) : AuthManager {

    override fun isUserSignedIn(): Single<Boolean> {
        return isUserSignedIn.invoke()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }

    override fun getCurrentUser(): Single<Either<Throwable, Option<DomainSignedInUser>>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected) {
                        authenticator.get()
                                .getCurrentUserId()
                                .flatMap { userIdEither ->
                                    userIdEither.fold(ifLeft = {
                                        Single.just(it.left())
                                    }, ifRight = { userIdOption ->
                                        userIdOption.fold(
                                                ifEmpty = { Single.just(none<DomainSignedInUser>().right()) },
                                                ifSome = remoteRepository.get()::findUser
                                        )
                                    })
                                }
                    } else {
                        Single.just(NoInternetConnectionException().left())
                    }
                }
    }

    override fun signIn(email: String, password: String): Single<Either<Throwable, Option<DomainSignedInUser>>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        authenticator.get().signIn(email, password)
                    else
                        Single.just(NoInternetConnectionException().left())
                }.flatMap { domainUserDataEither ->
                    domainUserDataEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = {
                        remoteRepository.get().findUser(it)
                    })
                }
    }

    override fun signUp(user: DomainSignUpUser): Single<Either<Throwable, DomainSignedInUser>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        authenticator.get().signUp(user)
                    else
                        Single.just(NoInternetConnectionException().left())
                }.flatMap { domainUserDataEither ->
                    domainUserDataEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = {
                        remoteRepository.get().storeUser(it)
                    })
                }
    }

    override fun signInWithGoogle(): Single<Either<Throwable, Option<DomainSignedInUser>>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        authenticator.get().signInWithGoogle()
                    else
                        Single.just(NoInternetConnectionException().left())
                }.flatMap { userUid ->
                    userUid.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = {
                        remoteRepository.get().findUser(it)
                    })
                }
    }

    override fun signInWithFacebook(): Single<Either<Throwable, Option<DomainSignedInUser>>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        authenticator.get().signInWithFacebook()
                    else
                        Single.just(NoInternetConnectionException().left())
                }.flatMap { userUid ->
                    userUid.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = {
                        remoteRepository.get().findUser(it)
                    })
                }
    }

    override fun signInWithTwitter(): Single<Either<Throwable, Option<DomainSignedInUser>>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        authenticator.get().signInWithTwitter()
                    else
                        Single.just(NoInternetConnectionException().left())
                }.flatMap { userUid ->
                    userUid.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = {
                        remoteRepository.get().findUser(it)
                    })
                }
    }

    override fun sendPasswordResetEmail(email: String): Single<Either<Throwable, Unit>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        authenticator.get().sendPasswordResetEmail(email)
                    else
                        Single.just(NoInternetConnectionException().left())
                }
    }

    override fun signOut(): Single<Either<Throwable, Unit>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        authenticator.get().signOut()
                    else
                        Single.just(NoInternetConnectionException().left())
                }.flatMap { userUidEither ->
                    userUidEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = { userUidOption ->
                        userUidOption.fold(ifEmpty = {
                            Single.just(Unit.right())
                        }, ifSome = {
                            remoteRepository.get()
                                    .updateUserLastLoginDate(it)
                                    .map { Unit.right() }
                        })
                    })
                }
    }
}

internal fun isUserSignedIn(
        authenticator: Lazy<AuthAuthenticator>
): Single<Boolean> {
    return authenticator.get()
            .isUserSignedIn()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
}
