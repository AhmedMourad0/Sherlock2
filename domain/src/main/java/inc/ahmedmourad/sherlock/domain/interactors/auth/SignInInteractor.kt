package inc.ahmedmourad.sherlock.domain.interactors.auth

import arrow.core.Either
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.AuthManager
import inc.ahmedmourad.sherlock.domain.model.auth.IncompleteUser
import inc.ahmedmourad.sherlock.domain.model.auth.SignedInUser
import inc.ahmedmourad.sherlock.domain.model.auth.submodel.Email
import inc.ahmedmourad.sherlock.domain.model.auth.submodel.Password
import io.reactivex.Single

typealias SignInInteractor =
        (@JvmSuppressWildcards Email, @JvmSuppressWildcards Password) ->
        @JvmSuppressWildcards Single<Either<Throwable, Either<IncompleteUser, SignedInUser>>>

internal fun signIn(
        authManager: Lazy<AuthManager>,
        email: Email,
        password: Password
): Single<Either<Throwable, Either<IncompleteUser, SignedInUser>>> {
    return authManager.get().signIn(email, password)
}
