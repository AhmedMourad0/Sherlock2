package inc.ahmedmourad.sherlock.domain.interactors.auth

import arrow.core.Either
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.AuthManager
import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignUpUser
import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignedInUser
import io.reactivex.Single

typealias SignUpInteractor =
        (@JvmSuppressWildcards DomainSignUpUser) -> @JvmSuppressWildcards Single<Either<Throwable, DomainSignedInUser>>

internal fun signUp(
        authManager: Lazy<AuthManager>,
        user: DomainSignUpUser
): Single<Either<Throwable, DomainSignedInUser>> {
    return authManager.get().signUp(user)
}
