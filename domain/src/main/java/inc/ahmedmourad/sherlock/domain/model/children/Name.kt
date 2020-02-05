package inc.ahmedmourad.sherlock.domain.model.children

import arrow.core.Either
import arrow.core.left
import arrow.core.right

class Name private constructor(val value: String) {

    fun component1() = value

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as Name

        if (value != other.value)
            return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "Name(value='$value')"
    }

    companion object {
        fun of(value: String): Either<Exception, Name> {
            return if (value.isBlank()) {
                Exception.BlankNameException.left()
            } else if (value.trim().contains(" ")) {
                Exception.NameContainsWhiteSpacesException.left()
            } else if (value.trim().length < 2) {
                Exception.NameTooShortException(2).left()
            } else if (value.trim().length > 20) {
                Exception.NameTooLongException(20).left()
            } else if (!value.trim().toCharArray().all(Char::isLetter)) {
                Exception.NameContainsNumbersOrSymbols.left()
            } else {
                Name(value.trim()).right()
            }
        }
    }

    sealed class Exception {
        object BlankNameException : Exception()
        object NameContainsWhiteSpacesException : Exception()
        data class NameTooShortException(val minLength: Int) : Exception()
        data class NameTooLongException(val maxLength: Int) : Exception()
        object NameContainsNumbersOrSymbols : Exception()
    }
}
