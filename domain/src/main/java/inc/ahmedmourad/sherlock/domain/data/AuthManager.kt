package inc.ahmedmourad.sherlock.domain.data

import inc.ahmedmourad.sherlock.domain.model.DomainSignUpUser
import inc.ahmedmourad.sherlock.domain.model.DomainSignedInUser
import inc.ahmedmourad.sherlock.domain.model.Either
import inc.ahmedmourad.sherlock.domain.model.Optional
import io.reactivex.Completable
import io.reactivex.Single

interface AuthManager {

    fun isUserSignedIn(): Single<Boolean>

    fun getCurrentUser(): Single<Optional<DomainSignedInUser>>

    fun signIn(email: String, password: String): Single<Optional<DomainSignedInUser>>

    fun signUp(user: DomainSignUpUser): Single<DomainSignedInUser>

    fun signInWithGoogle(): Single<Optional<DomainSignedInUser>>

    fun signInWithFacebook(): Single<Optional<DomainSignedInUser>>

    fun signInWithTwitter(): Single<Optional<DomainSignedInUser>>

    fun sendPasswordResetEmail(email: String): Completable

    fun signOut(): Completable

    interface AuthEnforcer {

        fun requireUserSignedIn(): Completable

        fun requireUserSignedInEither(): Single<Either<Nothing?, Throwable>>
    }
}
