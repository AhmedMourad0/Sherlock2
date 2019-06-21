package inc.ahmedmourad.sherlock.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppName(val first: String, val last: String) : Parcelable
