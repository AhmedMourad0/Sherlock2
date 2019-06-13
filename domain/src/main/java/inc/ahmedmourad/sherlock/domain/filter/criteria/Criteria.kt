package inc.ahmedmourad.sherlock.domain.filter.criteria

import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.model.DomainLocation

interface Criteria<T> {

    fun apply(result: T): Pair<T, Score>

    interface Score {
        fun passes(): Boolean
        fun calculate(): Int
    }
}

data class DomainChildCriteriaRules(
        val firstName: String,
        val lastName: String,
        val location: DomainLocation,
        val gender: Gender,
        val skin: Skin,
        val hair: Hair,
        val age: Int,
        val height: Int
)
