package inc.ahmedmourad.sherlock.model.parcelers

import android.os.Parcel
import arrow.core.orNull
import inc.ahmedmourad.sherlock.domain.model.children.Height
import kotlinx.android.parcel.Parceler

internal object HeightParceler : Parceler<Height> {

    override fun create(parcel: Parcel): Height {
        return Height.of(parcel.readInt()).orNull()!!
    }

    override fun Height.write(parcel: Parcel, flags: Int) {
        parcel.writeInt(this.value)
    }
}
