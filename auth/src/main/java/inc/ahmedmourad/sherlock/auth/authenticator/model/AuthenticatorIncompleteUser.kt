package inc.ahmedmourad.sherlock.auth.authenticator.model

import arrow.core.Either
import arrow.core.Option
import arrow.core.left
import arrow.core.right
import inc.ahmedmourad.sherlock.auth.model.AuthIncompleteUser

data class AuthenticatorIncompleteUser(
        val id: String,
        val email: Option<String>,
        val name: Option<String>,
        val pictureUrl: Option<String>
) {

    fun toAuthIncompleteUser() = AuthIncompleteUser(
            id,
            email,
            name,
            pictureUrl
    )

    fun orComplete(): Either<AuthenticatorIncompleteUser, AuthenticatorCompletedUser> {
        return if (isComplete()) {
            complete().right()
        } else {
            this.left()
        }
    }

    private fun isComplete(): Boolean {
        return email.isDefined() &&
                name.isDefined() &&
                pictureUrl.isDefined()
    }

    private fun complete(): AuthenticatorCompletedUser {
        return AuthenticatorCompletedUser(
                id,
                email.orNull()!!,
                name.orNull()!!,
                pictureUrl.orNull()!!
        )
    }
}
