package inc.ahmedmourad.sherlock.model.parcelers

import android.os.Parcel
import arrow.core.orNull
import inc.ahmedmourad.sherlock.domain.model.children.Coordinates
import kotlinx.android.parcel.Parceler

internal object CoordinatesParceler : Parceler<Coordinates> {

    override fun create(parcel: Parcel): Coordinates {
        return Coordinates.of(
                parcel.readDouble(),
                parcel.readDouble()
        ).orNull()!!
    }

    override fun Coordinates.write(parcel: Parcel, flags: Int) {
        parcel.writeDouble(this.latitude)
        parcel.writeDouble(this.longitude)
    }
}
