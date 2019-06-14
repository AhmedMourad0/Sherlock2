package inc.ahmedmourad.sherlock.model

import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import org.parceler.Parcel
import org.parceler.ParcelConstructor

@Parcel(Parcel.Serialization.BEAN)
data class AppChildCriteriaRules @ParcelConstructor constructor(
        val firstName: String,
        val lastName: String,
        val location: AppLocation,
        val gender: Gender,
        val skin: Skin,
        val hair: Hair,
        val age: Int,
        val height: Int
)
