package inc.ahmedmourad.sherlock.model.parcelers

import android.os.Parcel
import arrow.core.orNull
import inc.ahmedmourad.sherlock.domain.model.children.AgeRange
import inc.ahmedmourad.sherlock.model.parcelers.utils.write
import kotlinx.android.parcel.Parceler

internal object AgeRangeParceler : Parceler<AgeRange> {

    override fun create(parcel: Parcel): AgeRange {
        return AgeRange.of(
                AgeParceler.create(parcel),
                AgeParceler.create(parcel)
        ).orNull()!!
    }

    override fun AgeRange.write(parcel: Parcel, flags: Int) {
        AgeParceler.write(this.min, parcel, flags)
        AgeParceler.write(this.max, parcel, flags)
    }
}
