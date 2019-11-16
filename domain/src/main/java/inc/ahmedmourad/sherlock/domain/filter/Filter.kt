package inc.ahmedmourad.sherlock.domain.filter

import arrow.core.Tuple2
import inc.ahmedmourad.sherlock.domain.filter.criteria.Criteria

interface Filter<T> {

    val criteria: Criteria<T>

    //TODO: return a double as a ratio that this is the right child
    fun filter(items: List<T>): List<Tuple2<T, Int>>
}
