package inc.ahmedmourad.sherlock.model

import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import org.parceler.Parcel
import org.parceler.ParcelConstructor

@Parcel(Parcel.Serialization.BEAN)
data class AppAppearance @ParcelConstructor constructor(
        val gender: Gender,
        val skin: Skin,
        val hair: Hair,
        val age: AppRange,
        val height: AppRange
)
