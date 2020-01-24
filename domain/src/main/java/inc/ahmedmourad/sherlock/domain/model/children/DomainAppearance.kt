package inc.ahmedmourad.sherlock.domain.model.children

import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin

data class DomainExactAppearance(
        val gender: Gender,
        val skin: Skin,
        val hair: Hair,
        val age: Int,
        val height: Int
)

data class DomainEstimatedAppearance(
        val gender: Gender,
        val skin: Skin,
        val hair: Hair,
        val age: DomainRange,
        val height: DomainRange
)
