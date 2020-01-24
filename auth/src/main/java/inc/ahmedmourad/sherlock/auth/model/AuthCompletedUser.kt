package inc.ahmedmourad.sherlock.auth.model

data class AuthCompletedUser(
        val id: String,
        val email: String,
        val name: String,
        val pictureUrl: String
) {

    fun toAuthSignedInUser(details: AuthRetrievedUserDetails) = AuthSignedInUser(
            id,
            details.registrationDate,
            email,
            name,
            details.phoneNumber,
            pictureUrl
    )

    fun incomplete() = AuthIncompleteUser(
            id,
            email,
            name,
            null,
            pictureUrl
    )
}
