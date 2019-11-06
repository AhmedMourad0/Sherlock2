package inc.ahmedmourad.sherlock.model

import android.os.Parcelable
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.model.DomainAppearance
import inc.ahmedmourad.sherlock.domain.model.DomainEstimatedAppearance
import inc.ahmedmourad.sherlock.domain.model.DomainExactAppearance
import kotlinx.android.parcel.Parcelize

private interface AppAppearance<T> : Parcelable {

    val gender: Gender
    val skin: Skin
    val hair: Hair
    val age: T
    val height: T

    fun toDomainAppearance(): DomainAppearance<*>
}

@Parcelize
internal data class AppExactAppearance(
        override val gender: Gender,
        override val skin: Skin,
        override val hair: Hair,
        override val age: Int,
        override val height: Int) : AppAppearance<Int> {

    override fun toDomainAppearance() = DomainExactAppearance(
            gender,
            skin,
            hair,
            age,
            height
    )
}

@Parcelize
internal data class AppEstimatedAppearance(
        override val gender: Gender,
        override val skin: Skin,
        override val hair: Hair,
        override val age: AppRange,
        override val height: AppRange) : AppAppearance<AppRange> {

    override fun toDomainAppearance() = DomainEstimatedAppearance(
            gender,
            skin,
            hair,
            age.toDomainRange(),
            height.toDomainRange()
    )
}
