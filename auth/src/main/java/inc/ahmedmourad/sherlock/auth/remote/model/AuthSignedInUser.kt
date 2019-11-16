package inc.ahmedmourad.sherlock.auth.remote.model

import inc.ahmedmourad.sherlock.domain.model.DomainSignedInUser

data class AuthSignedInUser(
        val id: String,
        val registrationDate: Long,
        val lastLoginDate: Long,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val pictureUrl: String
) {
    fun toDomainUser() = DomainSignedInUser(
            id,
            registrationDate,
            lastLoginDate,
            email,
            name,
            phoneNumber,
            pictureUrl
    )
}
