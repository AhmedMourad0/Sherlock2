package inc.ahmedmourad.sherlock.model.auth

import android.os.Parcelable
import arrow.core.Option
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class AppIncompleteUser(
        val id: String,
        val email: @RawValue Option<String>,
        val name: @RawValue Option<String>,
        val phoneNumber: @RawValue Option<String>,
        val pictureUrl: @RawValue Option<String>
) : Parcelable
