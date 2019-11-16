package inc.ahmedmourad.sherlock.auth.authenticator

import android.content.Intent
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
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthAuthenticator
import inc.ahmedmourad.sherlock.auth.remote.mapper.toAuthSignUpUser
import inc.ahmedmourad.sherlock.domain.exceptions.NoInternetConnectionException
import inc.ahmedmourad.sherlock.domain.exceptions.NoSignedInUserException
import inc.ahmedmourad.sherlock.domain.model.DomainSignUpUser
import inc.ahmedmourad.sherlock.domain.model.DomainUserData
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import splitties.init.appCtx

internal class AuthFirebaseAuthenticator(
        private val auth: Lazy<FirebaseAuth>,
        private val connectivityManager: Lazy<ConnectivityManager>
) : AuthAuthenticator {

    override fun isUserSignedIn(): Single<Boolean> {
        return Single.just(auth.get().currentUser != null)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }

    override fun getCurrentUserId(): Single<Either<Throwable, Option<String>>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        Single.just(auth.get().currentUser?.uid.toOption().right())
                    else
                        Single.just(NoInternetConnectionException().left())
                }
    }

    override fun signIn(email: String, password: String): Single<Either<Throwable, String>> {
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

    private fun createSignInSingle(email: String, password: String): Single<Either<Throwable, String>> {

        return Single.create<Either<Throwable, String>> { emitter ->

            val successListener = { result: AuthResult ->

                val user = result.user

                if (user != null)
                    emitter.onSuccess(user.uid.right())
                else
                    emitter.onSuccess(NoSignedInUserException().left())
            }

            val failureListener = { throwable: Throwable ->
                emitter.onSuccess(throwable.left())
            }

            auth.get().signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun signUp(user: DomainSignUpUser): Single<Either<Throwable, DomainUserData>> {
        return connectivityManager.get()
                .isInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { isInternetConnected ->
                    if (isInternetConnected)
                        createSignUpSingle(user)
                    else
                        Single.just(NoInternetConnectionException().left())
                }
    }

    private fun createSignUpSingle(user: DomainSignUpUser): Single<Either<Throwable, DomainUserData>> {

        return Single.create<Either<Throwable, DomainUserData>> { emitter ->

            val authUser = user.toAuthSignUpUser()

            val successListener = { result: AuthResult ->

                val createdUser = result.user

                if (createdUser != null)
                    emitter.onSuccess(authUser.toDomainUserData(createdUser.uid).right())
                else // Wait, that's illegal
                    emitter.onSuccess(NoSignedInUserException().left())
            }

            val failureListener = { throwable: Throwable ->
                emitter.onSuccess(throwable.left())
            }

            auth.get().createUserWithEmailAndPassword(authUser.email, authUser.password)
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun signInWithGoogle(): Single<Either<Throwable, String>> {
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

    private fun createSignInWithGoogleSingle(): Single<Either<Throwable, String>> {

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

    override fun signInWithFacebook(): Single<Either<Throwable, String>> {
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

    private fun createSignInWithFacebookSingle(): Single<Either<Throwable, String>> {

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

    override fun signInWithTwitter(): Single<Either<Throwable, String>> {
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

    private fun createSignInWithTwitterSingle(): Single<Either<Throwable, String>> {

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
            if (twitterAuthToken.isExpired)
                getAuthCredentials { it.signInWithTwitter() }
            else
                Single.just(TwitterAuthProvider.getCredential(twitterAuthToken.token, twitterAuthToken.secret).right())
        })
    }

    private fun getAuthCredentials(createIntent: (AuthActivityFactory) -> Intent): Single<Either<Throwable, AuthCredential>> {
        appCtx.startActivity(createIntent(AuthSignInActivity.Companion))
        return AuthenticatorBus.signInCompletion
                .buffer(1)
                .map(List<Either<Throwable, AuthCredential>>::first)
                .single(NoSignedInUserException().left())
    }

    private fun signInWithCredentials(credentialEither: Either<Throwable, AuthCredential>): Single<Either<Throwable, String>> {

        return Single.create<Either<Throwable, String>> { emitter ->

            credentialEither.fold(ifLeft = {

                Single.just(it.left())

            }, ifRight = {

                val successListener = { result: AuthResult ->

                    val user = result.user

                    if (user != null)
                        emitter.onSuccess(user.uid.right())
                    else
                        emitter.onSuccess(NoSignedInUserException().left())
                }

                val failureListener = { throwable: Throwable ->
                    emitter.onSuccess(throwable.left())
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
                emitter.onSuccess(throwable.left())
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
                                .flatMap { either ->
                                    either.fold(ifLeft = {
                                        Single.just(it.left())
                                    }, ifRight = {
                                        signOutFromFacebook().andThen(signOutFromTwitter())
                                                .andThen(Single.just(id.right()))
                                    })
                                }
                    else
                        Single.just(NoInternetConnectionException().left())
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
