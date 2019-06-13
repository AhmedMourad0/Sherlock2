package inc.ahmedmourad.sherlock.domain.model

import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin

data class DomainAppearance(
        val gender: Gender,
        val skin: Skin,
        val hair: Hair,
        val age: DomainRange,
        val height: DomainRange
)
