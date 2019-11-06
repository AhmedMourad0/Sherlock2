package inc.ahmedmourad.sherlock.model

import android.os.Parcelable
import android.widget.ImageView
import inc.ahmedmourad.sherlock.domain.model.DomainChild

internal interface AppChild : Parcelable {

    val name: AppName
    val notes: String
    val location: AppLocation
    val appearance: AppEstimatedAppearance

    fun loadImage(imageView: ImageView)

    fun toDomainChild(): DomainChild
}
