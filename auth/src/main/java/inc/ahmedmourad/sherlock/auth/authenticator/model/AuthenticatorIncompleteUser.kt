package inc.ahmedmourad.sherlock.auth.authenticator.model

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import inc.ahmedmourad.sherlock.auth.model.AuthIncompleteUser

data class AuthenticatorIncompleteUser(
        val id: String,
        val email: String?,
        val name: String?,
        val pictureUrl: String?
) {

    fun toAuthIncompleteUser() = AuthIncompleteUser(
            id,
            email,
            name,
            null,
            pictureUrl
    )

    fun orComplete(): Either<AuthenticatorIncompleteUser, AuthenticatorCompletedUser> {
        return complete()?.right() ?: this.left()
    }

    private fun complete(): AuthenticatorCompletedUser? {

        if (email == null || name == null || pictureUrl == null) {
            return null
        }

        return AuthenticatorCompletedUser(
                id,
                email,
                name,
                pictureUrl
        )
    }
}
