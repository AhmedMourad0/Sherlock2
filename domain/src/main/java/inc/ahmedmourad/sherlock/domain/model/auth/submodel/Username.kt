package inc.ahmedmourad.sherlock.domain.model.auth.submodel

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import inc.ahmedmourad.sherlock.domain.model.children.submodel.Name
import kotlin.math.min
import kotlin.math.pow

class Username private constructor(val value: String) {

    fun component1() = value

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as Username

        if (value != other.value)
            return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "Username(value='$value')"
    }

    companion object {

        private const val SUFFIX_LENGTH = 5
        private const val MIN_LENGTH = DisplayName.MIN_LENGTH
        private const val MAX_LENGTH_WITHOUT_SUFFIX = DisplayName.MAX_LENGTH
        private const val MAX_LENGTH = MAX_LENGTH_WITHOUT_SUFFIX + SUFFIX_LENGTH

        fun from(displayName: DisplayName): Either<Exception, Username> {
            val suffix = System.currentTimeMillis() % 10.0.pow(SUFFIX_LENGTH).toLong()
            return if (displayName.value.replace(" ", "").matches(Regex("[a-zA-Z]+"))) {
                of(displayName.value.replace(" ", "_") + suffix)
            } else {
                of(createRandomName(suffix.toString()))
            }
        }

        private fun createRandomName(suffix: String): String {

            val allowedCharacters = ('a'..'z') + ('A'..'Z')

            fun randomName(maxLength: Int = Name.MAX_LENGTH): String {
                val nameLength = (Name.MIN_LENGTH..maxLength).random()
                return (1..nameLength).map { allowedCharacters.random() }.joinToString("")
            }

            tailrec fun create(name: String = randomName()): String {

                val maxAllowedWordLength = (MAX_LENGTH_WITHOUT_SUFFIX / 2) - name.length - 1
                if (maxAllowedWordLength < Name.MIN_LENGTH) {
                    return name
                }

                val nameLength = min(Name.MAX_LENGTH, maxAllowedWordLength).coerceIn(Name.MIN_LENGTH, Name.MAX_LENGTH)
                return create(name + "_" + randomName(nameLength))
            }

            return (create() + suffix).toCharArray().toList().shuffled().joinToString("")
        }

        fun of(value: String): Either<Exception, Username> {
            val trimmedValue = value.trim()
            return when {

                trimmedValue.isBlank() -> Exception.BlankUsernameException.left()

                trimmedValue.contains(" ") -> Exception.UsernameContainsWhiteSpacesException.left()

                trimmedValue.length < MIN_LENGTH -> Exception.UsernameTooShortException(trimmedValue.length, MIN_LENGTH).left()

                trimmedValue.length > MAX_LENGTH -> Exception.UsernameTooLongException(trimmedValue.length, MAX_LENGTH).left()

                trimmedValue.replace("_", "").all(Character::isLetterOrDigit).not() ->
                    Exception.UsernameContainsNonUnderscoreSymbolsException.left()

                trimmedValue.replace("_", "").isBlank() ->
                    Exception.UsernameOnlyContainsUnderscoresException.left()

                trimmedValue.all(Character::isDigit) -> Exception.UsernameOnlyContainsDigitsException.left()

                trimmedValue.none(Character::isLetter) -> Exception.NoLettersException.left()

                trimmedValue.any { it.isLetter() && it.toLowerCase() !in 'a'..'z' } ->
                    Exception.UsernameContainsNonEnglishLettersException.left()

                else -> Username(trimmedValue).right()
            }
        }
    }

    sealed class Exception {
        object BlankUsernameException : Exception()
        object UsernameContainsWhiteSpacesException : Exception()
        data class UsernameTooShortException(val length: Int, val minLength: Int) : Exception()
        data class UsernameTooLongException(val length: Int, val maxLength: Int) : Exception()
        object UsernameContainsNonUnderscoreSymbolsException : Exception()
        object UsernameOnlyContainsUnderscoresException : Exception()
        object UsernameOnlyContainsDigitsException : Exception()
        object NoLettersException : Exception()
        object UsernameContainsNonEnglishLettersException : Exception()
    }
}
