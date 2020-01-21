package inc.ahmedmourad.sherlock.auth.model

import arrow.core.some

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
            email.some(),
            name.some(),
            pictureUrl.some()
    )
}
