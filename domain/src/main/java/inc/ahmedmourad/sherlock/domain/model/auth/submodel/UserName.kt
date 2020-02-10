package inc.ahmedmourad.sherlock.domain.model.auth.submodel

import arrow.core.Either
import arrow.core.left
import arrow.core.right

class UserName private constructor(val value: String) {

    fun component1() = value

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as UserName

        if (value != other.value)
            return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "UserName(value='$value')"
    }

    companion object {
        fun of(value: String): Either<Exception, UserName> {
            return when {
                value.isBlank() -> Exception.BlankUserNameException.left()
                value.trim().contains(" ") -> Exception.UserNameContainsWhiteSpacesException.left()
                value.trim().length < 2 -> Exception.UserNameTooShortException(2).left()
                value.trim().length > 20 -> Exception.UserNameTooLongException(20).left()
                else -> UserName(value.trim()).right()
            }
        }
    }

    sealed class Exception {
        object BlankUserNameException : Exception()
        object UserNameContainsWhiteSpacesException : Exception()
        data class UserNameTooShortException(val minLength: Int) : Exception()
        data class UserNameTooLongException(val maxLength: Int) : Exception()
    }
}
