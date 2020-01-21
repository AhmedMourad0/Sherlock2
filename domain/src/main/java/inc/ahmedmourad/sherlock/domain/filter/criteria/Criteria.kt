package inc.ahmedmourad.sherlock.domain.filter.criteria

import arrow.core.Tuple2
import inc.ahmedmourad.sherlock.domain.model.children.DomainExactAppearance
import inc.ahmedmourad.sherlock.domain.model.children.DomainLocation
import inc.ahmedmourad.sherlock.domain.model.children.DomainName

interface Criteria<T> {

    fun apply(result: T): Tuple2<T, Score>

    interface Score {
        fun passes(): Boolean
        fun calculate(): Int
    }
}

data class DomainChildCriteriaRules(
        val name: DomainName,
        val location: DomainLocation,
        val appearance: DomainExactAppearance
)
