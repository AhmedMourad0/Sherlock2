package inc.ahmedmourad.sherlock.auth.authenticator.model

internal data class AuthenticatorSignUpUser(
        val password: String,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val pictureUrl: String
)
