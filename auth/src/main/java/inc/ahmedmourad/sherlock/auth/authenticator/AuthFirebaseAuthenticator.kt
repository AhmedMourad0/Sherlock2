package inc.ahmedmourad.sherlock.auth.authenticator

import android.content.Intent
import android.net.Uri
import arrow.core.*
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import com.twitter.sdk.android.core.TwitterAuthToken
import com.twitter.sdk.android.core.TwitterCore
import dagger.Lazy
import inc.ahmedmourad.sherlock.auth.authenticator.activity.AuthSignInActivity
import inc.ahmedmourad.sherlock.auth.authenticator.bus.AuthenticatorBus
import inc.ahmedmourad.sherlock.auth.authenticator.mapper.toAuthenticatorUserDetails
import inc.ahmedmourad.sherlock.auth.authenticator.model.AuthenticatorCompletedUser
import inc.ahmedmourad.sherlock.auth.authenticator.model.AuthenticatorIncompleteUser
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthAuthenticator
import inc.ahmedmourad.sherlock.auth.model.AuthCompletedUser
import inc.ahmedmourad.sherlock.auth.model.AuthIncompleteUser
import inc.ahmedmourad.sherlock.domain.exceptions.*
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import splitties.init.appCtx

internal class AuthFirebaseAuthenticator(
        private val auth: Lazy<FirebaseAuth>,
        private val connectivityManager: Lazy<ConnectivityManager>
) : AuthAuthenticator {

    override fun observeUserAuthState(): Flowable<Boolean> {
        return createObserveUserAuthState()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }

    private fun createObserveUserAuthState(): Flowable<Boolean> {
        return Flowable.create<Boolean>({ emitter ->

            val authStateListener = { firebaseAuth: FirebaseAuth ->
                emitter.onNext(firebaseAuth.currentUser != null)
            }

            auth.get().addAuthStateListener(authStateListener)

            emitter.setCancellable { auth.get().removeAuthStateListener(authStateListener) }

        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun getCurrentUser(): Flowable<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>> {
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
                            createGetCurrentUserFlowable()
                        } else {
                            Flowable.just(NoSignedInUserException().left())
                        }
                    })
                }
    }

    private fun createGetCurrentUserFlowable(): Flowable<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>> {
        return Flowable.just<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>>(
                auth.get().currentUser
                        ?.toAuthenticatorUser()
                        ?.rightIfNotNull { NoSignedInUserException() }
        ).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun signIn(
            email: String,
            password: String
    ): Single<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        createSignInSingle(email, password)
                    else
                        Single.just(NoInternetConnectionException().left())
                }
    }

    private fun createSignInSingle(
            email: String,
            password: String
    ): Single<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>> {

        return Single.create<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>> { emitter ->

            val successListener = { result: AuthResult ->

                val user = result.user

                if (user != null) {
                    emitter.onSuccess(user.toAuthenticatorUser().right())
                } else {
                    emitter.onSuccess(NoSignedInUserException().left())
                }
            }

            val failureListener = { throwable: Throwable ->
                emitter.onSuccess(when (throwable) {

                    is FirebaseAuthInvalidUserException -> InvalidUserException(
                            "The user account corresponding to the email does not exist or has been disabled!"
                    )

                    is FirebaseAuthInvalidCredentialsException -> InvalidCredentialsException(
                            "Wrong password!"
                    )

                    else -> throwable

                }.left())
            }

            auth.get().signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun signUp(email: String, password: String): Single<Either<Throwable, String>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        createSignUpSingle(email, password)
                    else
                        Single.just(NoInternetConnectionException().left())
                }
    }

    private fun createSignUpSingle(email: String, password: String): Single<Either<Throwable, String>> {

        return Single.create<Either<Throwable, String>> { emitter ->

            val successListener = { result: AuthResult ->

                val createdUser = result.user

                if (createdUser != null) {
                    emitter.onSuccess(createdUser.uid.right())
                } else { // Wait, that's illegal
                    emitter.onSuccess(NoSignedInUserException().left())
                }
            }

            val failureListener = { throwable: Throwable ->
                emitter.onSuccess(when (throwable) {

                    is FirebaseAuthWeakPasswordException -> WeakPasswordException(
                            "The password is not strong enough!"
                    )

                    is FirebaseAuthInvalidCredentialsException -> InvalidCredentialsException(
                            "The email address is malformed!"
                    )

                    is FirebaseAuthUserCollisionException -> UserCollisionException(
                            "There already exists an account with the given email address!"
                    )

                    else -> throwable

                }.left())
            }

            auth.get().createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun completeUserData(completedUser: AuthCompletedUser): Single<Either<Throwable, AuthCompletedUser>> {
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
                            createCompleteUserData(completedUser)
                        } else {
                            Single.just(NoSignedInUserException().left())
                        }
                    })
                }
    }

    private fun createCompleteUserData(completedUser: AuthCompletedUser): Single<Either<Throwable, AuthCompletedUser>> {

        return Single.create<Either<Throwable, AuthCompletedUser>> { emitter ->

            val details = completedUser.toAuthenticatorUserDetails()
            val user = auth.get().currentUser

            if (user == null) {
                emitter.onSuccess(NoSignedInUserException().left())
                return@create
            }

            val successListener = { _: Void ->
                emitter.onSuccess(completedUser.right())
            }

            val failureListener = { throwable: Throwable ->
                emitter.onSuccess(when (throwable) {

                    is FirebaseAuthInvalidCredentialsException -> InvalidCredentialsException(
                            "The email address is malformed!"
                    )

                    is FirebaseAuthUserCollisionException -> UserCollisionException(
                            "There already exists an account with the given email address!"
                    )

                    is FirebaseAuthInvalidUserException -> InvalidUserException(
                            "The current user's account has been disabled, deleted, or its credentials are no longer valid!"
                    )

                    //TODO: can be handled, maybe, check docs
                    is FirebaseAuthRecentLoginRequiredException -> RecentLoginRequiredException(
                            "The user's last sign-in time does not meet the security threshold!"
                    )

                    else -> throwable

                }.left())
            }

            user.updateProfile(
                    UserProfileChangeRequest.Builder()
                            .setDisplayName(details.name)
                            .setPhotoUri(Uri.parse(details.pictureUrl))
                            .build()
            ).continueWithTask {
                user.updateEmail(details.email)
            }.addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun signInWithGoogle(): Single<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        createSignInWithGoogleSingle()
                    else
                        Single.just(NoInternetConnectionException().left())
                }
    }

    private fun createSignInWithGoogleSingle(): Single<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>> {

        return Single.create<Option<GoogleSignInAccount>> { emitter ->

            emitter.setCancellable { AuthenticatorBus.signInCancellation.accept(Unit) }

            emitter.onSuccess(GoogleSignIn.getLastSignedInAccount(appCtx).toOption())

        }.flatMap(this::getGoogleAuthCredentials)
                .flatMap(this::signInWithCredentials)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }

    private fun getGoogleAuthCredentials(accountOptional: Option<GoogleSignInAccount>): Single<Either<Throwable, AuthCredential>> {
        return accountOptional.fold(ifEmpty = {
            getAuthCredentials { it.signInWithGoogle() }
        }, ifSome = { googleSignInAccount ->
            if (googleSignInAccount.isExpired)
                getAuthCredentials { it.signInWithGoogle() }
            else
                Single.just(GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null).right())
        })
    }

    override fun signInWithFacebook(): Single<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        createSignInWithFacebookSingle()
                    else
                        Single.just(NoInternetConnectionException().left())
                }
    }

    private fun createSignInWithFacebookSingle(): Single<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>> {

        return Single.create<Option<AccessToken>> { emitter ->

            emitter.setCancellable { AuthenticatorBus.signInCancellation.accept(Unit) }

            emitter.onSuccess(AccessToken.getCurrentAccessToken().toOption())

        }.flatMap(this::getFacebookAuthCredentials)
                .flatMap(this::signInWithCredentials)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }

    private fun getFacebookAuthCredentials(accessTokenOptional: Option<AccessToken>): Single<Either<Throwable, AuthCredential>> {
        return accessTokenOptional.fold(ifEmpty = {
            getAuthCredentials { it.signInWithFacebook() }
        }, ifSome = { accessToken ->
            if (accessToken.isExpired)
                getAuthCredentials { it.signInWithFacebook() }
            else
                Single.just(FacebookAuthProvider.getCredential(accessToken.token).right())
        })
    }

    override fun signInWithTwitter(): Single<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        createSignInWithTwitterSingle()
                    else
                        Single.just(NoInternetConnectionException().left())
                }
    }

    private fun createSignInWithTwitterSingle(): Single<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>> {

        return Single.create<Option<TwitterAuthToken>> { emitter ->

            emitter.setCancellable { AuthenticatorBus.signInCancellation.accept(Unit) }

            emitter.onSuccess(TwitterCore.getInstance().sessionManager.activeSession.authToken.toOption())

        }.flatMap(this::getTwitterAuthCredentials)
                .flatMap(this::signInWithCredentials)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }

    private fun getTwitterAuthCredentials(authTokenOptional: Option<TwitterAuthToken>): Single<Either<Throwable, AuthCredential>> {
        return authTokenOptional.fold(ifEmpty = {
            getAuthCredentials { it.signInWithTwitter() }
        }, ifSome = { twitterAuthToken ->
            if (twitterAuthToken.isExpired) {
                getAuthCredentials { it.signInWithTwitter() }
            } else {
                Single.just(TwitterAuthProvider.getCredential(twitterAuthToken.token, twitterAuthToken.secret).right())
            }
        })
    }

    private fun getAuthCredentials(createIntent: (AuthActivityFactory) -> Intent): Single<Either<Throwable, AuthCredential>> {
        appCtx.startActivity(createIntent(AuthSignInActivity.Companion))
        return AuthenticatorBus.signInCompletion
                .buffer(1)
                .map(List<Either<Throwable, AuthCredential>>::first)
                .single(NoSignedInUserException().left())
    }

    private fun signInWithCredentials(
            credentialEither: Either<Throwable, AuthCredential>
    ): Single<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>> {

        return Single.create<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>> { emitter ->

            credentialEither.fold(ifLeft = {

                Single.just(it.left())

            }, ifRight = {

                val successListener = { result: AuthResult ->

                    val user = result.user

                    if (user != null)
                        emitter.onSuccess(user.toAuthenticatorUser().right())
                    else
                        emitter.onSuccess(NoSignedInUserException().left())
                }

                val failureListener = { throwable: Throwable ->
                    emitter.onSuccess(when (throwable) {

                        is FirebaseAuthInvalidUserException -> InvalidUserException(
                                "The user account you are trying to sign in to has been disabled!"
                        )

                        is FirebaseAuthInvalidCredentialsException -> InvalidCredentialsException(
                                "The credential is malformed or has expired!"
                        )

                        //TODO: can be handled, check docs
                        is FirebaseAuthUserCollisionException -> UserCollisionException(
                                "There already exists an account with the email address asserted by the credential!"
                        )

                        else -> throwable

                    }.left())
                }

                auth.get().signInWithCredential(it)
                        .addOnSuccessListener(successListener)
                        .addOnFailureListener(failureListener)
            })

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun sendPasswordResetEmail(email: String): Single<Either<Throwable, Unit>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        createSendPasswordResetEmailSingle(email)
                    else
                        Single.just(NoInternetConnectionException().left())
                }
    }

    private fun createSendPasswordResetEmailSingle(email: String): Single<Either<Throwable, Unit>> {

        return Single.create<Either<Throwable, Unit>> { emitter ->

            val successListener = { _: Void ->
                emitter.onSuccess(Unit.right())
            }

            val failureListener = { throwable: Throwable ->
                emitter.onSuccess(when (throwable) {

                    is FirebaseAuthInvalidUserException -> InvalidUserException(
                            "There is no user corresponding to the given email address!"
                    )

                    else -> throwable

                }.left())
            }

            auth.get().sendPasswordResetEmail(email)
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun signOut(): Single<Either<Throwable, Option<String>>> {

        val id = auth.get().currentUser?.uid.toOption()

        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        signOutFromFirebaseAuth()
                                .andThen(signOutFromGoogle())
                    else
                        Single.just(NoInternetConnectionException().left())
                }.flatMap { either ->
                    either.fold(ifLeft = {
                        Single.just(it.left())
                    }, ifRight = {
                        signOutFromFacebook()
                                .andThen(signOutFromTwitter())
                                .andThen(Single.just(id.right()))
                    })
                }
    }

    private fun signOutFromFirebaseAuth(): Completable {
        return Completable.fromAction(auth.get()::signOut)
    }

    private fun signOutFromGoogle(): Single<Either<Throwable, Unit>> {

        return Single.create<Either<Throwable, Unit>> { emitter ->

            val successListener = { _: Void ->
                emitter.onSuccess(Unit.right())
            }

            val failureListener = { throwable: Throwable ->
                emitter.onSuccess(throwable.left())
            }

            GoogleSignIn.getClient(
                    appCtx,
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            ).signOut()
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    private fun signOutFromFacebook(): Completable {
        return Completable.fromAction(LoginManager.getInstance()::logOut)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }

    private fun signOutFromTwitter(): Completable {
        return Completable.fromAction(TwitterCore.getInstance().sessionManager::clearActiveSession)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }
}

interface AuthActivityFactory {
    fun signInWithGoogle(): Intent
    fun signInWithFacebook(): Intent
    fun signInWithTwitter(): Intent
}

private fun FirebaseUser.toAuthenticatorUser(): Either<AuthIncompleteUser, AuthCompletedUser> {
    return AuthenticatorIncompleteUser(
            this.uid,
            this.email.toOption(),
            this.displayName.toOption(),
            this.photoUrl.toOption().map(Uri::toString)
    ).orComplete().bimap(
            AuthenticatorIncompleteUser::toAuthIncompleteUser,
            AuthenticatorCompletedUser::toAuthCompletedUser
    )
}
