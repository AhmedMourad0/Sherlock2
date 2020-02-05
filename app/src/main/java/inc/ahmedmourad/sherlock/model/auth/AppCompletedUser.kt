package inc.ahmedmourad.sherlock.model.auth

import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.domain.model.auth.DomainCompletedUser
import inc.ahmedmourad.sherlock.utils.getImageBitmap
import inc.ahmedmourad.sherlock.utils.getImageBytes

data class AppCompletedUser(
        val id: String,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val picturePath: String
) {

    fun toDomainCompletedUser() = DomainCompletedUser(
            id,
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

        other as AppCompletedUser

        if (id != other.id)
            return false

        if (email != other.email)
            return false

        if (name != other.name)
            return false

        if (phoneNumber != other.phoneNumber)
            return false

        if (!picturePath.contentEquals(other.picturePath))
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + picturePath.hashCode()
        return result
    }
}
