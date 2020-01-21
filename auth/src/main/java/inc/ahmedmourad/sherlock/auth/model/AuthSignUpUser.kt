package inc.ahmedmourad.sherlock.auth.model

data class AuthSignUpUser(
        val password: String,
        val email: String,
        val name: String,
        val phoneNumber: String
) {

    fun toAuthCompletedUser(id: String, pictureUrl: String) = AuthCompletedUser(
            id,
            email,
            name,
            pictureUrl
    )

    fun toAuthUserDetails(id: String) = AuthStoredUserDetails(
            id,
            phoneNumber
    )
}
