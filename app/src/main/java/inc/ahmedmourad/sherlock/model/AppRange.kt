package inc.ahmedmourad.sherlock.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppRange(val from: Int, val to: Int) : Parcelable
