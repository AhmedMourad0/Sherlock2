package inc.ahmedmourad.sherlock.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppChildCriteriaRules(
        val name: AppName,
        val location: AppLocation,
        val appearance: AppAppearance<PInt>
) : Parcelable

@Parcelize
data class PInt(val value: Int) : Parcelable
