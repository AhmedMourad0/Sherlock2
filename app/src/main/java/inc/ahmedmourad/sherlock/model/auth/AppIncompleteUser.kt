package inc.ahmedmourad.sherlock.model.auth

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppIncompleteUser(
        val id: String,
        val email: String?,
        val name: String?,
        val phoneNumber: String?,
        val pictureUrl: String?
) : Parcelable
