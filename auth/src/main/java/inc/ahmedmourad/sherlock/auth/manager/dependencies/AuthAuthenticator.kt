package inc.ahmedmourad.sherlock.auth.manager.dependencies

import inc.ahmedmourad.sherlock.domain.exceptions.NoSignedInUserException
import inc.ahmedmourad.sherlock.domain.model.DomainSignUpUser
import inc.ahmedmourad.sherlock.domain.model.DomainUserData
import inc.ahmedmourad.sherlock.domain.model.Either
import inc.ahmedmourad.sherlock.domain.model.Optional
import io.reactivex.Completable
import io.reactivex.Single

internal interface AuthAuthenticator {

    fun isUserSignedIn(): Single<Boolean>

    fun getCurrentUserId(): Single<Optional<String>>

    fun signIn(email: String, password: String): Single<String>

    fun signUp(user: DomainSignUpUser): Single<DomainUserData>

    fun requireUserSignedIn(): Completable

    fun requireUserSignedInEither(): Single<Either<Nothing?, NoSignedInUserException>>

    fun signInWithGoogle(): Single<String>

    fun signInWithFacebook(): Single<String>

    fun signInWithTwitter(): Single<String>

    fun sendPasswordResetEmail(email: String): Completable

    fun signOut(): Single<Optional<String>>
}
