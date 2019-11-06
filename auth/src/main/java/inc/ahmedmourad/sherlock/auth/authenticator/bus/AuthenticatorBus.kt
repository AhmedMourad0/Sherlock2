package inc.ahmedmourad.sherlock.auth.authenticator.bus

import com.google.firebase.auth.AuthCredential
import com.jakewharton.rxrelay2.PublishRelay
import inc.ahmedmourad.sherlock.domain.model.Either

internal object AuthenticatorBus {
    val signInCompletion = PublishRelay.create<Either<AuthCredential, Throwable>>()
    val signInCancellation = PublishRelay.create<Unit>()
}
