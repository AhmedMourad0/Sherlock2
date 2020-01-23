package inc.ahmedmourad.sherlock.auth.model

import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignedInUser

data class AuthSignedInUser(
        val id: String,
        val registrationDate: Long,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val pictureUrl: String
) {
    fun toDomainSignedInUser() = DomainSignedInUser(
            id,
            registrationDate,
            email,
            name,
            phoneNumber,
            pictureUrl
    )
}