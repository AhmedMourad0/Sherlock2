package inc.ahmedmourad.sherlock.domain.model.auth.submodel

import arrow.core.Either
import arrow.core.left
import arrow.core.right

class Password private constructor(val value: String) {

    fun component1() = value

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as Password

        if (value != other.value)
            return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "Password(value='$value')"
    }

    companion object {
        fun of(value: String): Either<Exception, Password> {
            val chars = value.toCharArray()
            return when {

                value.isBlank() -> Exception.BlankPasswordException.left()

                value.length < 7 -> Exception.PasswordTooShortException(7).left()

                chars.none(Char::isLetter) -> Exception.NoLettersException.left()

                chars.none(Char::isDigit) -> Exception.NoDigitsException.left()

                chars.all(Char::isLetterOrDigit) -> Exception.NoSymbolsException.left()

                chars.distinct().size < 4 -> Exception.FewDistinctCharactersException(4).left()

                else -> Password(value).right()
            }
        }
    }

    sealed class Exception {
        object BlankPasswordException : Exception()
        data class PasswordTooShortException(val minLength: Int) : Exception()
        object NoLettersException : Exception()
        object NoDigitsException : Exception()
        object NoSymbolsException : Exception()
        data class FewDistinctCharactersException(val min: Int) : Exception()
    }
}
