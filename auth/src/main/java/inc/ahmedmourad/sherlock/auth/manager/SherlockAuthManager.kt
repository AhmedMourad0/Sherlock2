package inc.ahmedmourad.sherlock.auth.manager

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import dagger.Lazy
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthAuthenticator
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthImageRepository
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthRemoteRepository
import inc.ahmedmourad.sherlock.auth.mapper.toAuthCompletedUser
import inc.ahmedmourad.sherlock.auth.mapper.toAuthSignUpUser
import inc.ahmedmourad.sherlock.auth.mapper.toAuthStoredUserDetails
import inc.ahmedmourad.sherlock.auth.model.AuthCompletedUser
import inc.ahmedmourad.sherlock.auth.model.AuthIncompleteUser
import inc.ahmedmourad.sherlock.auth.model.AuthSignedInUser
import inc.ahmedmourad.sherlock.domain.data.AuthManager
import inc.ahmedmourad.sherlock.domain.model.auth.CompletedUser
import inc.ahmedmourad.sherlock.domain.model.auth.IncompleteUser
import inc.ahmedmourad.sherlock.domain.model.auth.SignUpUser
import inc.ahmedmourad.sherlock.domain.model.auth.SignedInUser
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

internal typealias ObserveUserAuthState = () -> @JvmSuppressWildcards Flowable<Boolean>

internal class SherlockAuthManager(
        private val authenticator: Lazy<AuthAuthenticator>,
        private val remoteRepository: Lazy<AuthRemoteRepository>,
        private val imageRepository: Lazy<AuthImageRepository>
) : AuthManager {

    override fun observeUserAuthState(): Flowable<Boolean> {
        return authenticator.get()
                .observeUserAuthState()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }

    override fun findSignedInUser(): Flowable<Either<Throwable, Either<IncompleteUser, SignedInUser>>> {
        return authenticator.get()
                .getCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { incompleteUserEither ->
                    incompleteUserEither.fold(ifLeft = {
                        Flowable.just(it.left())
                    }, ifRight = { userEither ->
                        userEither.fold(ifLeft = { incompleteUser ->
                            Flowable.just(incompleteUser.left().right())
                        }, ifRight = { completedUser ->
                            remoteRepository.get()
                                    .findUser(completedUser.id)
                                    .map { either ->
                                        either.map { user ->
                                            if (user == null) {
                                                completedUser.incomplete().left()
                                            } else {
                                                completedUser.toAuthSignedInUser(user).right()
                                            }
                                        }
                                    }
                        })
                    })
                }.flatMap { either ->
                    either.fold(ifLeft = {
                        Flowable.just(it.left())
                    }, ifRight = { userEither ->
                        userEither.fold(ifLeft = { incompleteUser ->
                            Flowable.just(incompleteUser.left().right())
                        }, ifRight = { signedInUser ->
                            remoteRepository.get()
                                    .updateUserLastLoginDate(signedInUser.id)
                                    .map { signedInUser.right().right() }
                                    .toFlowable()
                        })
                    })
                }.map { either ->
                    either.map { userEither ->
                        userEither.bimap(
                                AuthIncompleteUser::toDomainIncompleteUser,
                                AuthSignedInUser::toDomainSignedInUser
                        )
                    }
                }
    }

    override fun signIn(email: String, password: String): Single<Either<Throwable, Either<IncompleteUser, SignedInUser>>> {
        return authenticator.get()
                .signIn(email, password).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { either ->
                    either.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = { userEither ->
                        userEither.fold(ifLeft = {
                            Single.just(it.left().right())
                        }, ifRight = { completedUser ->
                            remoteRepository.get()
                                    .findUser(completedUser.id)
                                    .map { userEither ->
                                        userEither.map { user ->
                                            if (user == null) {
                                                completedUser.incomplete().left()
                                            } else {
                                                completedUser.toAuthSignedInUser(user).right()
                                            }
                                        }
                                    }.firstOrError()
                        })
                    })
                }.map { either ->
                    either.map { userEither ->
                        userEither.bimap(
                                AuthIncompleteUser::toDomainIncompleteUser,
                                AuthSignedInUser::toDomainSignedInUser
                        )
                    }
                }
    }

    override fun signUp(user: SignUpUser): Single<Either<Throwable, SignedInUser>> {
        val picture = user.picture
        val signUpUser = user.toAuthSignUpUser()
        return authenticator.get()
                .signUp(signUpUser.email, signUpUser.password)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { domainUserDetailsEither ->
                    domainUserDetailsEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = { userId ->
                        remoteRepository.get()
                                .storeUserDetails(signUpUser.toAuthUserDetails(userId))
                    })
                }.flatMap { detailsEither ->
                    detailsEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = { userDetails ->
                        imageRepository.get()
                                .storeUserPicture(userDetails.id, picture)
                                .map { urlEither ->
                                    urlEither.map { url ->
                                        signUpUser.toAuthCompletedUser(userDetails.id, url) toT userDetails
                                    }
                                }
                    })
                }.flatMap { signedInUserEither ->
                    signedInUserEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = { (completedUser, userDetails) ->
                        authenticator.get()
                                .completeUserData(completedUser)
                                .map { either ->
                                    either.map {
                                        it.toAuthSignedInUser(userDetails).toDomainSignedInUser()
                                    }
                                }
                    })
                }
    }

    override fun completeSignUp(completedUser: CompletedUser): Single<Either<Throwable, SignedInUser>> {
        return imageRepository.get()
                .storeUserPicture(completedUser.id, completedUser.picture)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { urlEither ->
                    urlEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = { url ->
                        authenticator.get()
                                .completeUserData(completedUser.toAuthCompletedUser(url))
                    })
                }.flatMap { completedUserEither ->
                    completedUserEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = { authCompletedUser ->
                        remoteRepository.get()
                                .storeUserDetails(completedUser.toAuthStoredUserDetails())
                                .map { detailsEither ->
                                    detailsEither.map {
                                        authCompletedUser.toAuthSignedInUser(it).toDomainSignedInUser()
                                    }
                                }
                    })
                }
    }

    override fun signInWithGoogle(): Single<Either<Throwable, Either<IncompleteUser, SignedInUser>>> {
        return createSignInWithProvider(AuthAuthenticator::signInWithGoogle)
    }

    override fun signInWithFacebook(): Single<Either<Throwable, Either<IncompleteUser, SignedInUser>>> {
        return createSignInWithProvider(AuthAuthenticator::signInWithFacebook)
    }

    override fun signInWithTwitter(): Single<Either<Throwable, Either<IncompleteUser, SignedInUser>>> {
        return createSignInWithProvider(AuthAuthenticator::signInWithTwitter)
    }

    private fun createSignInWithProvider(
            signInWithProvider: AuthAuthenticator.() -> Single<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>>
    ): Single<Either<Throwable, Either<IncompleteUser, SignedInUser>>> {
        return authenticator.get()
                .signInWithProvider()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { either ->
                    either.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = { userEither ->
                        userEither.fold(ifLeft = {
                            Single.just(it.left().right())
                        }, ifRight = { completedUser ->
                            remoteRepository.get()
                                    .findUser(completedUser.id)
                                    .map { userEither ->
                                        userEither.map { user ->
                                            if (user == null) {
                                                completedUser.incomplete().left()
                                            } else {
                                                completedUser.toAuthSignedInUser(user).right()
                                            }
                                        }
                                    }.firstOrError()
                        })
                    })
                }.map { either ->
                    either.map { userEither ->
                        userEither.bimap(
                                AuthIncompleteUser::toDomainIncompleteUser,
                                AuthSignedInUser::toDomainSignedInUser
                        )
                    }
                }
    }

    override fun sendPasswordResetEmail(email: String): Single<Either<Throwable, Unit>> {
        return authenticator.get()
                .sendPasswordResetEmail(email)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }

    override fun signOut(): Single<Either<Throwable, Unit>> {
        return authenticator.get()
                .signOut()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { userUidEither ->
                    userUidEither.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = { userUid ->
                        if (userUid == null) {
                            Single.just(Unit.right())
                        } else {
                            remoteRepository.get()
                                    .updateUserLastLoginDate(userUid)
                                    .map { Unit.right() }
                        }
                    })
                }
    }
}
