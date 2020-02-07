package inc.ahmedmourad.sherlock.domain.model.children

import arrow.core.Either
import arrow.core.left
import arrow.core.right

class Weight private constructor(val value: Double) {

    fun component1() = value

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as Weight

        if (value != other.value)
            return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "Weight(value=$value)"
    }

    companion object {
        fun of(value: Double): Either<Exception, Weight> {
            return if (value in 0.0..1.0) {
                Weight(value).right()
            } else {
                Exception.WeightOutOfRangeException(0.0, 1.0).left()
            }
        }
    }

    sealed class Exception {
        data class WeightOutOfRangeException(val min: Double, val max: Double) : Exception()
    }
}
