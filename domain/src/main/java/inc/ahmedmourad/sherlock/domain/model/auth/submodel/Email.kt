package inc.ahmedmourad.sherlock.domain.model.auth.submodel

import arrow.core.Either
import arrow.core.left
import arrow.core.right

class Email private constructor(val value: String) {

    fun component1() = value

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as Email

        if (value != other.value)
            return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "Email(value='$value')"
    }

    companion object {

        private val EMAIL_REGEX =
                Regex("""[a-zA-Z0-9+._%\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\-]{0,64}(\.[a-zA-Z0-9][a-zA-Z0-9\-]{0,25})+""")

        fun of(value: String): Either<Exception, Email> {
            return when {
                value.isBlank() -> Exception.BlankEmailException.left()
                value.trim().contains(" ") -> Exception.EmailContainsWhiteSpacesException.left()
                EMAIL_REGEX.matches(value) -> Exception.MalformedEmailException.left()
                else -> Email(value.trim()).right()
            }
        }
    }

    sealed class Exception {
        object BlankEmailException : Exception()
        object EmailContainsWhiteSpacesException : Exception()
        object MalformedEmailException : Exception()
    }
}
