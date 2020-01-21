package inc.ahmedmourad.sherlock.model.children

import android.os.Parcelable
import inc.ahmedmourad.sherlock.domain.model.children.DomainSimpleRetrievedChild
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class AppRetrievedChild(
        val id: String,
        val publicationDate: Long,
        val name: AppName,
        val notes: String,
        val location: AppLocation,
        val appearance: AppEstimatedAppearance,
        val pictureUrl: String
) : Parcelable {
    fun simplify() = AppSimpleRetrievedChild(
            id,
            publicationDate,
            name,
            notes,
            location.name,
            location.address,
            pictureUrl
    )
}

@Parcelize
internal data class AppSimpleRetrievedChild(
        val id: String,
        val publicationDate: Long,
        val name: AppName,
        val notes: String,
        val locationName: String,
        val locationAddress: String,
        val pictureUrl: String) : Parcelable {
    fun toDomainSimpleChild() = DomainSimpleRetrievedChild(
            id,
            publicationDate,
            name.toDomainName(),
            notes,
            locationName,
            locationAddress,
            pictureUrl
    )
}
