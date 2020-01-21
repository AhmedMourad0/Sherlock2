package inc.ahmedmourad.sherlock.domain.interactors.auth

import arrow.core.Either
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.AuthManager
import inc.ahmedmourad.sherlock.domain.model.auth.DomainCompletedUser
import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignedInUser
import io.reactivex.Single

typealias CompleteSignUpInteractor =
        (@JvmSuppressWildcards DomainCompletedUser) -> @JvmSuppressWildcards Single<Either<Throwable, DomainSignedInUser>>

internal fun completeSignUp(
        authManager: Lazy<AuthManager>,
        completedUser: DomainCompletedUser
): Single<Either<Throwable, DomainSignedInUser>> {
    return authManager.get().completeSignUp(completedUser)
}
