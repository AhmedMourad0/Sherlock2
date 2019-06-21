package inc.ahmedmourad.sherlock.model

import android.os.Parcelable
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppAppearance<T : Parcelable>(
        val gender: Gender,
        val skin: Skin,
        val hair: Hair,
        val age: T,
        val height: T
) : Parcelable
