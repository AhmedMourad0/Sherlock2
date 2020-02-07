package inc.ahmedmourad.sherlock.domain.model.children.submodel

import arrow.core.Either
import arrow.core.left
import arrow.core.right

class Age private constructor(val value: Int) {

    fun component1() = value

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as Age

        if (value != other.value)
            return false

        return true
    }

    override fun hashCode(): Int {
        return value
    }

    override fun toString(): String {
        return "Age(value=$value)"
    }

    companion object {
        fun of(value: Int): Either<Exception, Age> {
            return if (value in 0..200) {
                Age(value).right()
            } else {
                Exception.AgeOutOfRangeException(0, 200).left()
            }
        }
    }

    sealed class Exception {
        data class AgeOutOfRangeException(val min: Int, val max: Int) : Exception()
    }
}
