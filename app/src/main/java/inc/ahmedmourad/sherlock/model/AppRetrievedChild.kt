package inc.ahmedmourad.sherlock.model

import android.os.Parcelable
import android.widget.ImageView
import com.bumptech.glide.Glide
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import kotlinx.android.parcel.Parcelize
import splitties.init.appCtx

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

    fun loadImage(imageView: ImageView) {
        Glide.with(appCtx)
                .load(pictureUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(imageView)
    }

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
