package inc.ahmedmourad.sherlock.model.children

import android.os.Parcelable
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.model.children.DomainEstimatedAppearance
import inc.ahmedmourad.sherlock.domain.model.children.DomainExactAppearance
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class AppExactAppearance(
        val gender: Gender,
        val skin: Skin,
        val hair: Hair,
        val age: Int,
        val height: Int
) : Parcelable {
    fun toDomainAppearance() = DomainExactAppearance(
            gender,
            skin,
            hair,
            age,
            height
    )
}

@Parcelize
internal data class AppEstimatedAppearance(
        val gender: Gender,
        val skin: Skin,
        val hair: Hair,
        val age: AppRange,
        val height: AppRange
) : Parcelable {
    fun toDomainAppearance() = DomainEstimatedAppearance(
            gender,
            skin,
            hair,
            age.toDomainRange(),
            height.toDomainRange()
    )
}
