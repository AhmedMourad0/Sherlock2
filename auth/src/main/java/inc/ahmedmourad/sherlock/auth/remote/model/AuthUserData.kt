package inc.ahmedmourad.sherlock.auth.remote.model

import inc.ahmedmourad.sherlock.auth.remote.contract.Contract

internal data class AuthUserData(
        val id: String,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val picture: ByteArray
) {

    fun toAuthSignedInUser(registrationDate: Long, lastLoginDate: Long, pictureUrl: String) = AuthSignedInUser(
            id,
            registrationDate,
            lastLoginDate,
            email,
            name,
            phoneNumber,
            pictureUrl
    )

    fun toMap(registrationDate: Long, lastLoginDate: Long, pictureUrl: String): Map<String, Any> = hashMapOf(
            Contract.Database.Users.REGISTRATION_DATE to registrationDate,
            Contract.Database.Users.LAST_LOGIN_DATE to lastLoginDate,
            Contract.Database.Users.EMAIL to email,
            Contract.Database.Users.NAME to name,
            Contract.Database.Users.PHONE_NUMBER to phoneNumber,
            Contract.Database.Users.PICTURE_URL to pictureUrl
    )

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as AuthUserData

        if (id != other.id)
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
        var result = id.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + picture.contentHashCode()
        return result
    }
}
