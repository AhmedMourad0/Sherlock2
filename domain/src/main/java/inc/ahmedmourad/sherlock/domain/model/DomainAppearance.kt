package inc.ahmedmourad.sherlock.domain.model

import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin

interface DomainAppearance<T> {
    val gender: Gender
    val skin: Skin
    val hair: Hair
    val age: T
    val height: T
}

data class DomainExactAppearance(
        override val gender: Gender,
        override val skin: Skin,
        override val hair: Hair,
        override val age: Int,
        override val height: Int
) : DomainAppearance<Int>

data class DomainEstimatedAppearance(
        override val gender: Gender,
        override val skin: Skin,
        override val hair: Hair,
        override val age: DomainRange,
        override val height: DomainRange
) : DomainAppearance<DomainRange>
