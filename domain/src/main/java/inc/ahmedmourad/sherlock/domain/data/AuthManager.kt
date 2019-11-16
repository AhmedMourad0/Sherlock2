package inc.ahmedmourad.sherlock.domain.data

import arrow.core.Either
import arrow.core.Option
import inc.ahmedmourad.sherlock.domain.model.DomainSignUpUser
import inc.ahmedmourad.sherlock.domain.model.DomainSignedInUser
import io.reactivex.Single

interface AuthManager {

    fun isUserSignedIn(): Single<Boolean>

    fun getCurrentUser(): Single<Either<Throwable, Option<DomainSignedInUser>>>

    fun signIn(email: String, password: String): Single<Either<Throwable, Option<DomainSignedInUser>>>

    fun signUp(user: DomainSignUpUser): Single<Either<Throwable, DomainSignedInUser>>

    fun signInWithGoogle(): Single<Either<Throwable, Option<DomainSignedInUser>>>

    fun signInWithFacebook(): Single<Either<Throwable, Option<DomainSignedInUser>>>

    fun signInWithTwitter(): Single<Either<Throwable, Option<DomainSignedInUser>>>

    fun sendPasswordResetEmail(email: String): Single<Either<Throwable, Unit>>

    fun signOut(): Single<Either<Throwable, Unit>>
}
