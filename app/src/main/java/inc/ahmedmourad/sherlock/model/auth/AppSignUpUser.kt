package inc.ahmedmourad.sherlock.model.auth

import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignUpUser
import inc.ahmedmourad.sherlock.utils.getImageBitmap
import inc.ahmedmourad.sherlock.utils.getImageBytes

internal data class AppSignUpUser(
        val password: String,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val picturePath: String
) {

    fun toDomainSignUpUser() = DomainSignUpUser(
            password,
            email,
            name,
            phoneNumber,
            getImageBytes(getImageBitmap(picturePath, R.drawable.placeholder))
    )

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as AppSignUpUser

        if (password != other.password)
            return false

        if (email != other.email)
            return false

        if (name != other.name)
            return false

        if (phoneNumber != other.phoneNumber)
            return false

        if (picturePath != other.picturePath)
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = password.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + picturePath.hashCode()
        return result
    }
}
