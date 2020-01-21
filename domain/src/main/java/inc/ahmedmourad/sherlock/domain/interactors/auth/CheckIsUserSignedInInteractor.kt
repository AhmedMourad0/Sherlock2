package inc.ahmedmourad.sherlock.domain.interactors.auth

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.AuthManager
import io.reactivex.Single

typealias CheckIsUserSignedInInteractor = () -> @JvmSuppressWildcards Single<Boolean>

internal fun checkIsUserSignedIn(authManager: Lazy<AuthManager>): Single<Boolean> {
    return authManager.get().isUserSignedIn()
}
