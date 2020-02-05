package inc.ahmedmourad.sherlock.model.parcelers

import android.os.Parcel
import arrow.core.orNull
import inc.ahmedmourad.sherlock.domain.model.children.Url
import kotlinx.android.parcel.Parceler

internal object UrlParceler : Parceler<Url> {

    override fun create(parcel: Parcel): Url {
        return Url.of(parcel.readString()!!).orNull()!!
    }

    override fun Url.write(parcel: Parcel, flags: Int) {
        parcel.writeString(this.value)
    }
}
