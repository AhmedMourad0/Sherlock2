package inc.ahmedmourad.sherlock.auth.manager.dependencies

import arrow.core.Either
import arrow.core.Option
import inc.ahmedmourad.sherlock.auth.model.AuthCompletedUser
import inc.ahmedmourad.sherlock.auth.model.AuthIncompleteUser
import io.reactivex.Flowable
import io.reactivex.Single

internal interface AuthAuthenticator {

    fun observeUserAuthState(): Flowable<Boolean>

    fun getCurrentUser(): Flowable<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>>

    fun signIn(email: String, password: String): Single<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>>

    fun signUp(email: String, password: String): Single<Either<Throwable, String>>

    fun completeUserData(completedUser: AuthCompletedUser): Single<Either<Throwable, AuthCompletedUser>>

    fun signInWithGoogle(): Single<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>>

    fun signInWithFacebook(): Single<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>>

    fun signInWithTwitter(): Single<Either<Throwable, Either<AuthIncompleteUser, AuthCompletedUser>>>

    fun sendPasswordResetEmail(email: String): Single<Either<Throwable, Unit>>

    fun signOut(): Single<Either<Throwable, Option<String>>>
}
