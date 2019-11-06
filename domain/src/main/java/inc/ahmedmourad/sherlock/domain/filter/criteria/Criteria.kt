package inc.ahmedmourad.sherlock.domain.filter.criteria

import inc.ahmedmourad.sherlock.domain.model.DomainExactAppearance
import inc.ahmedmourad.sherlock.domain.model.DomainLocation
import inc.ahmedmourad.sherlock.domain.model.DomainName

interface Criteria<T> {

    fun apply(result: T): Pair<T, Score>

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
