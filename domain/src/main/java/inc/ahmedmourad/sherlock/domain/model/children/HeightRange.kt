package inc.ahmedmourad.sherlock.domain.model.children

import arrow.core.Either
import arrow.core.left
import arrow.core.right

class HeightRange private constructor(val min: Height, val max: Height) {

    fun component1() = min

    fun component2() = max

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as HeightRange

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
        return "HeightRange(min=$min, max=$max)"
    }

    companion object {
        fun of(min: Height, max: Height): Either<Exception, HeightRange> {
            return if (min.value < max.value) {
                HeightRange(min, max).right()
            } else {
                Exception.MinExceedsMaxException.left()
            }
        }
    }

    sealed class Exception {
        object MinExceedsMaxException : Exception()
    }
}
