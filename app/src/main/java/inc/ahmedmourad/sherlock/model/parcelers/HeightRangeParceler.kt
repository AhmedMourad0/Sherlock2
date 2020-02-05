package inc.ahmedmourad.sherlock.model.parcelers

import android.os.Parcel
import arrow.core.orNull
import inc.ahmedmourad.sherlock.domain.model.children.HeightRange
import inc.ahmedmourad.sherlock.model.parcelers.utils.write
import kotlinx.android.parcel.Parceler

internal object HeightRangeParceler : Parceler<HeightRange> {

    override fun create(parcel: Parcel): HeightRange {
        return HeightRange.of(
                HeightParceler.create(parcel),
                HeightParceler.create(parcel)
        ).orNull()!!
    }

    override fun HeightRange.write(parcel: Parcel, flags: Int) {
        HeightParceler.write(this.min, parcel, flags)
        HeightParceler.write(this.max, parcel, flags)
    }
}
