package inc.ahmedmourad.sherlock.domain.model.children.submodel

import arrow.core.Either
import arrow.core.left
import arrow.core.right

class Height private constructor(val value: Int) {

    fun component1() = value

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as Height

        if (value != other.value)
            return false

        return true
    }

    override fun hashCode(): Int {
        return value
    }

    override fun toString(): String {
        return "Height(value=$value)"
    }

    companion object {
        fun of(value: Int): Either<Exception, Height> {
            return if (value in 20..300) {
                Height(value).right()
            } else {
                Exception.HeightOutOfRangeException(20, 300).left()
            }
        }
    }

    sealed class Exception {
        data class HeightOutOfRangeException(val min: Int, val max: Int) : Exception()
    }
}
