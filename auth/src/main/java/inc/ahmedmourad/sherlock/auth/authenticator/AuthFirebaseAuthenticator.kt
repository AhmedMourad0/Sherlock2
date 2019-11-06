package inc.ahmedmourad.sherlock.auth.authenticator

import android.content.Intent
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
import inc.ahmedmourad.sherlock.domain.exceptions.NoSignedInUserException
import inc.ahmedmourad.sherlock.domain.model.*
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import splitties.init.appCtx

internal class AuthFirebaseAuthenticator(
        private val auth: Lazy<FirebaseAuth>,
        private val connectivityEnforcer: Lazy<ConnectivityManager.ConnectivityEnforcer>
) : AuthAuthenticator {

    override fun isUserSignedIn(): Single<Boolean> {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(Single.just(auth.get().currentUser != null))
    }

    override fun getCurrentUserId(): Single<Optional<String>> {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(Single.just(auth.get().currentUser?.uid.asOptional()))
    }

    override fun signIn(email: String, password: String): Single<String> {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(createSignInSingle(email, password))
    }

    private fun createSignInSingle(email: String, password: String): Single<String> {

        return Single.create<String> { emitter ->

            val successListener = { result: AuthResult ->

                val user = result.user

                if (user != null)
                    emitter.onSuccess(user.uid)
                else
                    emitter.onError(NoSignedInUserException())
            }

            val failureListener = emitter::onError

            auth.get().signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun signUp(user: DomainSignUpUser): Single<DomainUserData> {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(createSignUpSingle(user))
    }

    private fun createSignUpSingle(user: DomainSignUpUser): Single<DomainUserData> {

        return Single.create<DomainUserData> { emitter ->

            val authUser = user.toAuthSignUpUser()

            val successListener = { result: AuthResult ->

                val createdUser = result.user

                if (createdUser != null)
                    emitter.onSuccess(authUser.toDomainUserData(createdUser.uid))
                else // Wait, that's illegal
                    emitter.onError(NoSignedInUserException())
            }

            val failureListener = emitter::onError

            auth.get().createUserWithEmailAndPassword(authUser.email, authUser.password)
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun requireUserSignedIn(): Completable {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(createRequireUserSignedInCompletable())
    }

    private fun createRequireUserSignedInCompletable(): Completable {

        return Completable.create {

            if (auth.get().currentUser != null)
                it.onComplete()
            else
                it.onError(NoSignedInUserException())

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun requireUserSignedInEither(): Single<Either<Nothing?, NoSignedInUserException>> {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(createRequireUserSignedInEitherSingle())
    }

    private fun createRequireUserSignedInEitherSingle(): Single<Either<Nothing?, NoSignedInUserException>> {

        return Single.create<Either<Nothing?, NoSignedInUserException>> {

            if (auth.get().currentUser != null)
                it.onSuccess(Either.NULL)
            else
                it.onSuccess(NoSignedInUserException().asEither())

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun signInWithGoogle(): Single<String> {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(createSignInWithGoogleSingle())
    }

    private fun createSignInWithGoogleSingle(): Single<String> {

        return Single.create<Optional<GoogleSignInAccount>> { emitter ->

            emitter.setCancellable { AuthenticatorBus.signInCancellation.accept(Unit) }

            emitter.onSuccess(GoogleSignIn.getLastSignedInAccount(appCtx).asOptional())

        }.flatMap(this::getGoogleAuthCredentials)
                .flatMap(this::signInWithCredentials)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }

    private fun getGoogleAuthCredentials(accountOptional: Optional<GoogleSignInAccount>): Single<AuthCredential> {

        val (account) = accountOptional

        return if (account == null || account.isExpired)
            getAuthCredentials { it.signInWithGoogle() }
        else
            Single.just(GoogleAuthProvider.getCredential(account.idToken, null))
    }

    override fun signInWithFacebook(): Single<String> {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(createSignInWithFacebookSingle())
    }

    private fun createSignInWithFacebookSingle(): Single<String> {

        return Single.create<Optional<AccessToken>> { emitter ->

            emitter.setCancellable { AuthenticatorBus.signInCancellation.accept(Unit) }

            emitter.onSuccess(AccessToken.getCurrentAccessToken().asOptional())

        }.flatMap(this::getFacebookAuthCredentials)
                .flatMap(this::signInWithCredentials)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }

    private fun getFacebookAuthCredentials(accessTokenOptional: Optional<AccessToken>): Single<AuthCredential> {

        val (accessToken) = accessTokenOptional

        return if (accessToken == null || accessToken.isExpired)
            getAuthCredentials { it.signInWithFacebook() }
        else
            Single.just(FacebookAuthProvider.getCredential(accessToken.token))
    }

    override fun signInWithTwitter(): Single<String> {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(createSignInWithTwitterSingle())
    }

    private fun createSignInWithTwitterSingle(): Single<String> {

        return Single.create<Optional<TwitterAuthToken>> { emitter ->

            emitter.setCancellable { AuthenticatorBus.signInCancellation.accept(Unit) }

            emitter.onSuccess(TwitterCore.getInstance().sessionManager.activeSession.authToken.asOptional())

        }.flatMap(this::getTwitterAuthCredentials)
                .flatMap(this::signInWithCredentials)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }

    private fun getTwitterAuthCredentials(authTokenOptional: Optional<TwitterAuthToken>): Single<AuthCredential> {

        val (authToken) = authTokenOptional

        return if (authToken == null || authToken.isExpired)
            getAuthCredentials { it.signInWithTwitter() }
        else
            Single.just(TwitterAuthProvider.getCredential(
                    authToken.token,
                    authToken.secret
            ))
    }

    private fun getAuthCredentials(createIntent: (AuthActivityFactory) -> Intent): Single<AuthCredential> {
        appCtx.startActivity(createIntent(AuthSignInActivity.Companion))
        return AuthenticatorBus.signInCompletion
                .buffer(1)
                .map(List<Either<AuthCredential, Throwable>>::first)
                .unwrapEither()
                .singleOrError()
    }

    private fun signInWithCredentials(credential: AuthCredential): Single<String> {

        return Single.create<String> { emitter ->

            val successListener = { result: AuthResult ->

                val user = result.user

                if (user != null)
                    emitter.onSuccess(user.uid)
                else
                    emitter.onError(NoSignedInUserException())
            }

            val failureListener = emitter::onError

            auth.get().signInWithCredential(credential)
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun sendPasswordResetEmail(email: String): Completable {
        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(createSendPasswordResetEmailCompletable(email))
    }

    private fun createSendPasswordResetEmailCompletable(email: String): Completable {

        return Completable.create { emitter ->

            val successListener = { _: Void ->
                emitter.onComplete()
            }

            val failureListener = emitter::onError

            auth.get().sendPasswordResetEmail(email)
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener)

        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
    }

    override fun signOut(): Single<Optional<String>> {

        val id = auth.get().currentUser?.uid.asOptional()

        return connectivityEnforcer.get()
                .requireInternetConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .andThen(signOutFromFirebaseAuth())
                .andThen(signOutFromGoogle())
                .andThen(signOutFromFacebook())
                .andThen(signOutFromTwitter())
                .andThen(Single.just(id))
    }

    private fun signOutFromFirebaseAuth(): Completable {
        return Completable.fromAction(auth.get()::signOut)
    }

    private fun signOutFromGoogle(): Completable {

        return Completable.create { emitter ->

            val successListener = { _: Void ->
                emitter.onComplete()
            }

            val failureListener = emitter::onError

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
