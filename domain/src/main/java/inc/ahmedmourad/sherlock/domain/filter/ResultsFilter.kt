package inc.ahmedmourad.sherlock.domain.filter

import inc.ahmedmourad.sherlock.domain.filter.criteria.Criteria
import inc.ahmedmourad.sherlock.domain.model.DomainChild

class ResultsFilter<T : DomainChild>(override val criteria: Criteria<T>) : Filter<T> {
    override fun filter(items: List<T>) = ArrayList(items).map { criteria.apply(it) }
            .filter { (_, score) -> score.passes() }
            .map { (result, score) -> result to score.calculate().coerceAtLeast(0) }
            .sortedByDescending { it.second }
}
