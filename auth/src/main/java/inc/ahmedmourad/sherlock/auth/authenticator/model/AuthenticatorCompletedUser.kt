package inc.ahmedmourad.sherlock.auth.authenticator.model

import inc.ahmedmourad.sherlock.auth.model.AuthCompletedUser

data class AuthenticatorCompletedUser(
        val id: String,
        val email: String,
        val name: String,
        val pictureUrl: String
) {
    fun toAuthCompletedUser() = AuthCompletedUser(
            id,
            email,
            name,
            pictureUrl
    )
}
