package inc.ahmedmourad.sherlock.domain.filter

import arrow.core.Tuple2
import inc.ahmedmourad.sherlock.domain.filter.criteria.Criteria

internal class ResultsFilter<T>(private val criteria: Criteria<T>) : Filter<T> {
    override fun filter(items: List<T>): List<Tuple2<T, Int>> {
        return ArrayList(items).map { criteria.apply(it) }
                .filter { (_, score) -> score.passes() }
                .map { result -> result.map { it.calculate().coerceAtLeast(0) } }
                .sortedByDescending { it.b }
    }
}
