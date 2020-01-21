package inc.ahmedmourad.sherlock.domain.interactors.auth

import arrow.core.Either
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.AuthManager
import io.reactivex.Single

typealias SendPasswordResetEmailInteractor = (@JvmSuppressWildcards String) -> @JvmSuppressWildcards Single<Either<Throwable, Unit>>

internal fun sendPasswordResetEmail(
        authManager: Lazy<AuthManager>,
        email: String
): Single<Either<Throwable, Unit>> {
    return authManager.get().sendPasswordResetEmail(email)
}
