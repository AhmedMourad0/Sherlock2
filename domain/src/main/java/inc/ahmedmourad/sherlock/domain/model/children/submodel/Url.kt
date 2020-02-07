package inc.ahmedmourad.sherlock.domain.model.children.submodel

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.net.URL

class Url private constructor(val value: String) {

    fun component1() = value

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as Url

        if (value != other.value)
            return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "Url(value=$value)"
    }

    companion object {
        fun of(value: String): Either<Exception, Url> {

            if (value.isBlank()) {
                return Exception.BlankUrlException.left()
            }

            return try {
                URL(value).toURI()
                Url(value).right()
            } catch (e: MalformedURLException) {
                Exception.MalformedUrlException.left()
            } catch (e: URISyntaxException) {
                Exception.MalformedUrlException.left()
            }
        }
    }

    sealed class Exception {
        object MalformedUrlException : Exception()
        object BlankUrlException : Exception()
    }
}
