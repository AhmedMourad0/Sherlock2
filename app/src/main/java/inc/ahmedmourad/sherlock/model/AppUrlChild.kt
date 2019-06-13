package inc.ahmedmourad.sherlock.model

import android.widget.ImageView
import com.squareup.picasso.Picasso
import org.parceler.Parcel
import org.parceler.ParcelConstructor

@Parcel(Parcel.Serialization.BEAN)
data class AppUrlChild @ParcelConstructor constructor(
        override val id: String,
        override val timeMillis: Long,
        override val name: AppName,
        override val notes: String,
        override val location: AppLocation,
        override val appearance: AppAppearance,
        val pictureUrl: String
) : AppChild {
    override fun loadImage(imageView: ImageView) {
        Picasso.get().load(pictureUrl).into(imageView)
    }
}
