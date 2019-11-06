package inc.ahmedmourad.sherlock.children.remote.model

import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.model.DomainAppearance
import inc.ahmedmourad.sherlock.domain.model.DomainEstimatedAppearance
import inc.ahmedmourad.sherlock.domain.model.DomainExactAppearance

private interface FirebaseAppearance<T> {

    val gender: Gender
    val skin: Skin
    val hair: Hair
    val age: T
    val height: T

    fun toDomainAppearance(): DomainAppearance<*>
}

internal data class FirebaseExactAppearance(
        override val gender: Gender,
        override val skin: Skin,
        override val hair: Hair,
        override val age: Int,
        override val height: Int) : FirebaseAppearance<Int> {

    override fun toDomainAppearance() = DomainExactAppearance(
            gender,
            skin,
            hair,
            age,
            height
    )
}

internal data class FirebaseEstimatedAppearance(
        override val gender: Gender,
        override val skin: Skin,
        override val hair: Hair,
        override val age: FirebaseRange,
        override val height: FirebaseRange) : FirebaseAppearance<FirebaseRange> {

    override fun toDomainAppearance() = DomainEstimatedAppearance(
            gender,
            skin,
            hair,
            age.toDomainRange(),
            height.toDomainRange()
    )
}
