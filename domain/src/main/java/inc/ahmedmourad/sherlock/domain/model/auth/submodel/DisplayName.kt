package inc.ahmedmourad.sherlock.domain.model.auth.submodel

import arrow.core.Either
import arrow.core.left
import arrow.core.right

class DisplayName private constructor(val value: String) {

    fun component1() = value

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as DisplayName

        if (value != other.value)
            return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "DisplayName(value='$value')"
    }

    companion object {
        fun of(value: String): Either<Exception, DisplayName> {
            return if (value.isBlank()) {
                Exception.BlankDisplayNameException.left()
            } else if (value.trim().length < 2) {
                Exception.DisplayNameTooShortException(2).left()
            } else if (value.trim().length > 30) {
                Exception.DisplayNameTooLongException(30).left()
            } else if (!value.trim().toCharArray().all(Char::isLetter)) {
                Exception.DisplayNameContainsNumbersOrSymbols.left()
            } else {
                DisplayName(value.trim()).right()
            }
        }
    }

    sealed class Exception {
        object BlankDisplayNameException : Exception()
        data class DisplayNameTooShortException(val minLength: Int) : Exception()
        data class DisplayNameTooLongException(val maxLength: Int) : Exception()
        object DisplayNameContainsNumbersOrSymbols : Exception()
    }
}
