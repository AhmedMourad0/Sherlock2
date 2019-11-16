package inc.ahmedmourad.sherlock.auth.manager.dependencies

import arrow.core.Either
import arrow.core.Option
import inc.ahmedmourad.sherlock.domain.model.DomainSignUpUser
import inc.ahmedmourad.sherlock.domain.model.DomainUserData
import io.reactivex.Single

internal interface AuthAuthenticator {

    fun isUserSignedIn(): Single<Boolean>

    fun getCurrentUserId(): Single<Either<Throwable, Option<String>>>

    fun signIn(email: String, password: String): Single<Either<Throwable, String>>

    fun signUp(user: DomainSignUpUser): Single<Either<Throwable, DomainUserData>>

    fun signInWithGoogle(): Single<Either<Throwable, String>>

    fun signInWithFacebook(): Single<Either<Throwable, String>>

    fun signInWithTwitter(): Single<Either<Throwable, String>>

    fun sendPasswordResetEmail(email: String): Single<Either<Throwable, Unit>>

    fun signOut(): Single<Either<Throwable, Option<String>>>
}
