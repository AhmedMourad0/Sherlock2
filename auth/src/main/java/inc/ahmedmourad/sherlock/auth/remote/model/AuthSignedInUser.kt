package inc.ahmedmourad.sherlock.auth.remote.model

import inc.ahmedmourad.sherlock.domain.model.DomainSignedInUser

data class AuthSignedInUser(
        val id: String,
        val registrationDate: Long,
        val lastLoginDate: Long,
        override val email: String,
        override val name: String,
        override val phoneNumber: String,
        val pictureUrl: String
) : AuthUser {
    override fun toDomainUser() = DomainSignedInUser(
            id,
            registrationDate,
            lastLoginDate,
            email,
            name,
            phoneNumber,
            pictureUrl
    )
}
