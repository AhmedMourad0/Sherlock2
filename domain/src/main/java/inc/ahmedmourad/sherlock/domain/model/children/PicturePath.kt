package inc.ahmedmourad.sherlock.domain.model.children

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.io.File
import java.util.*

class PicturePath private constructor(val value: String) {

    fun component1() = value

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as PicturePath

        if (value != other.value)
            return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "PicturePath(value=$value)"
    }

    companion object {
        fun of(value: String): Either<Exception, PicturePath> {

            if (value.isBlank()) {
                return Exception.BlankPathException.left()
            }

            try {

                val file = File(value)

                return if (!file.exists()) {
                    Exception.NonExistentFileException.left()
                } else if (!file.isFile) {
                    Exception.NonFilePathException.left()
                } else if (!file.canRead()) {
                    Exception.UnreadableFileException.left()
                } else if (file.extension.toLowerCase(Locale.US) == "gif") {
                    Exception.GifPathException.left()
                } else if (file.extension.toLowerCase(Locale.US) !in arrayOf("jpg", "jpeg", "png")) {
                    Exception.NonPicturePathException.left()
                } else {
                    PicturePath(value).right()
                }

            } catch (e: SecurityException) {
                return Exception.SecurityException.left()
            }
        }
    }

    sealed class Exception {
        object BlankPathException : Exception()
        object NonExistentFileException : Exception()
        object NonFilePathException : Exception()
        object NonPicturePathException : Exception()
        object GifPathException : Exception()
        object UnreadableFileException : Exception()
        object SecurityException : Exception()
    }
}
