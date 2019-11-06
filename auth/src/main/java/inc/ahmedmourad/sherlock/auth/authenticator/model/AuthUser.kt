package inc.ahmedmourad.sherlock.auth.authenticator.model

import inc.ahmedmourad.sherlock.domain.model.DomainUser

internal interface AuthUser {

    val email: String
    val name: String
    val phoneNumber: String

    fun toDomainUser(): DomainUser
}
