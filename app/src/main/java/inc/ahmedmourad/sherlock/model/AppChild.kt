package inc.ahmedmourad.sherlock.model

import android.widget.ImageView

interface AppChild {

    val id: String
    val timeMillis: Long
    val name: AppName
    val notes: String
    val location: AppLocation
    val appearance: AppAppearance

    fun loadImage(imageView: ImageView)
}
