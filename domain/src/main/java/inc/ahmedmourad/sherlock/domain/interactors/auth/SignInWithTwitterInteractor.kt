package inc.ahmedmourad.sherlock.domain.interactors.auth

import arrow.core.Either
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.AuthManager
import inc.ahmedmourad.sherlock.domain.model.auth.DomainIncompleteUser
import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignedInUser
import io.reactivex.Single

typealias SignInWithTwitterInteractor =
        () -> @JvmSuppressWildcards Single<Either<Throwable, Either<DomainIncompleteUser, DomainSignedInUser>>>

internal fun signInWithTwitter(
        authManager: Lazy<AuthManager>
): Single<Either<Throwable, Either<DomainIncompleteUser, DomainSignedInUser>>> {
    return authManager.get().signInWithTwitter()
}