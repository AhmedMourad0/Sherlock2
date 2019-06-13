package inc.ahmedmourad.sherlock.domain.filter

import inc.ahmedmourad.sherlock.domain.filter.criteria.Criteria

interface Filter<T> {

    val criteria: Criteria<T>

    fun filter(items: List<T>): List<Pair<T, Int>>
}
