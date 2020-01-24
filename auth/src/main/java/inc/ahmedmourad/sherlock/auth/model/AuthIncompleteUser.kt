package inc.ahmedmourad.sherlock.auth.model

import inc.ahmedmourad.sherlock.domain.model.auth.DomainIncompleteUser

data class AuthIncompleteUser(
        val id: String,
        val email: String?,
        val name: String?,
        val phoneNumber: String?,
        val pictureUrl: String?
) {
    fun toDomainIncompleteUser() = DomainIncompleteUser(
            id,
            email,
            name,
            phoneNumber,
            pictureUrl
    )
}
