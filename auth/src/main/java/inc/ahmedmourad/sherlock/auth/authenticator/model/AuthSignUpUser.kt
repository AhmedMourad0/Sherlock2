package inc.ahmedmourad.sherlock.auth.authenticator.model

import inc.ahmedmourad.sherlock.domain.model.DomainSignUpUser
import inc.ahmedmourad.sherlock.domain.model.DomainUserData

internal data class AuthSignUpUser(
        val password: String,
        override val email: String,
        override val name: String,
        override val phoneNumber: String,
        val picture: ByteArray
) : AuthUser {

    override fun toDomainUser() = DomainSignUpUser(
            password,
            email,
            name,
            phoneNumber,
            picture
    )

    fun toDomainUserData(id: String) = DomainUserData(
            id,
            email,
            name,
            phoneNumber,
            picture
    )

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as AuthSignUpUser

        if (password != other.password)
            return false

        if (email != other.email)
            return false

        if (name != other.name)
            return false

        if (phoneNumber != other.phoneNumber)
            return false

        if (!picture.contentEquals(other.picture))
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = password.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + picture.contentHashCode()
        return result
    }
}
