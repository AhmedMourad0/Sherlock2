package inc.ahmedmourad.sherlock.domain.model.auth

import arrow.core.Either
import arrow.core.right
import inc.ahmedmourad.sherlock.domain.model.auth.submodel.*
import inc.ahmedmourad.sherlock.domain.model.common.Url

class SignedInUser(
        val id: UserId,
        val registrationDate: Long,
        val email: Email,
        val displayName: DisplayName,
        val userName: UserName,
        val phoneNumber: PhoneNumber,
        val pictureUrl: Url?
) {

    fun component1() = id

    fun component2() = registrationDate

    fun component3() = email

    fun component4() = displayName

    fun component5() = userName

    fun component6() = phoneNumber

    fun component7() = pictureUrl

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as SignedInUser

        if (id != other.id)
            return false

        if (registrationDate != other.registrationDate)
            return false

        if (email != other.email)
            return false

        if (displayName != other.displayName)
            return false

        if (userName != other.userName)
            return false

        if (phoneNumber != other.phoneNumber)
            return false

        if (pictureUrl != other.pictureUrl)
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + registrationDate.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + (pictureUrl?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "SignedInUser(" +
                "id=$id, " +
                "registrationDate=$registrationDate, " +
                "email=$email, " +
                "displayName=$displayName, " +
                "userName=$userName, " +
                "phoneNumber=$phoneNumber, " +
                "pictureUrl=$pictureUrl" +
                ")"
    }

    companion object {
        fun of(id: UserId,
               registrationDate: Long,
               email: Email,
               displayName: DisplayName,
               userName: UserName,
               phoneNumber: PhoneNumber,
               pictureUrl: Url?
        ): Either<Exception, SignedInUser> {
            return SignedInUser(
                    id,
                    registrationDate,
                    email,
                    displayName,
                    userName,
                    phoneNumber,
                    pictureUrl
            ).right()
        }
    }

    sealed class Exception
}
