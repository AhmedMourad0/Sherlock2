package inc.ahmedmourad.sherlock.domain.data

import arrow.core.Either
import inc.ahmedmourad.sherlock.domain.model.auth.DomainCompletedUser
import inc.ahmedmourad.sherlock.domain.model.auth.DomainIncompleteUser
import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignUpUser
import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignedInUser
import io.reactivex.Single

interface AuthManager {

    fun isUserSignedIn(): Single<Boolean>

    fun findSignedInUser(): Single<Either<Throwable, Either<DomainIncompleteUser, DomainSignedInUser>>>

    fun signIn(email: String, password: String): Single<Either<Throwable, Either<DomainIncompleteUser, DomainSignedInUser>>>

    fun signUp(user: DomainSignUpUser): Single<Either<Throwable, DomainSignedInUser>>

    fun completeSignUp(completedUser: DomainCompletedUser): Single<Either<Throwable, DomainSignedInUser>>

    fun signInWithGoogle(): Single<Either<Throwable, Either<DomainIncompleteUser, DomainSignedInUser>>>

    fun signInWithFacebook(): Single<Either<Throwable, Either<DomainIncompleteUser, DomainSignedInUser>>>

    fun signInWithTwitter(): Single<Either<Throwable, Either<DomainIncompleteUser, DomainSignedInUser>>>

    fun sendPasswordResetEmail(email: String): Single<Either<Throwable, Unit>>

    fun signOut(): Single<Either<Throwable, Unit>>
}
