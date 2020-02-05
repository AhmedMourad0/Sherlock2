package inc.ahmedmourad.sherlock.domain.filter.criteria

import arrow.core.Tuple2

interface Criteria<T> {

    fun apply(result: T): Tuple2<T, Score>

    interface Score {
        fun passes(): Boolean
        fun calculate(): Int
    }
}

