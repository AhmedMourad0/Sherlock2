package inc.ahmedmourad.sherlock.auth.model

import inc.ahmedmourad.sherlock.domain.model.auth.IncompleteUser

data class AuthIncompleteUser(
        val id: String,
        val email: String?,
        val name: String?,
        val phoneNumber: String?,
        val pictureUrl: String?
) {
    fun toDomainIncompleteUser() = IncompleteUser(
            id,
            email,
            name,
            phoneNumber,
            pictureUrl
    )
}
