package inc.ahmedmourad.sherlock.domain.model.children.submodel

import arrow.core.Either
import arrow.core.left
import arrow.core.right

class AgeRange private constructor(val min: Age, val max: Age) {

    fun component1() = min

    fun component2() = max

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as AgeRange

        if (min != other.min)
            return false

        if (max != other.max)
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = min.hashCode()
        result = 31 * result + max.hashCode()
        return result
    }

    override fun toString(): String {
        return "AgeRange(min=$min, max=$max)"
    }

    companion object {
        fun of(from: Age, to: Age): Either<Exception, AgeRange> {
            return if (from.value < to.value) {
                AgeRange(from, to).right()
            } else {
                Exception.MinExceedsMaxException.left()
            }
        }
    }

    sealed class Exception {
        object MinExceedsMaxException : Exception()
    }
}
