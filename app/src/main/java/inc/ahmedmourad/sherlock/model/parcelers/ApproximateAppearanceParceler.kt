package inc.ahmedmourad.sherlock.model.parcelers

import android.os.Parcel
import arrow.core.orNull
import inc.ahmedmourad.sherlock.domain.model.children.ApproximateAppearance
import inc.ahmedmourad.sherlock.model.parcelers.utils.createNullable
import inc.ahmedmourad.sherlock.model.parcelers.utils.writeNullable
import kotlinx.android.parcel.Parceler

internal object ApproximateAppearanceParceler : Parceler<ApproximateAppearance> {

    override fun create(parcel: Parcel): ApproximateAppearance {
        return ApproximateAppearance.of(
                GenderParceler.createNullable(parcel),
                SkinParceler.createNullable(parcel),
                HairParceler.createNullable(parcel),
                AgeRangeParceler.createNullable(parcel),
                HeightRangeParceler.createNullable(parcel)
        ).orNull()!!
    }

    override fun ApproximateAppearance.write(parcel: Parcel, flags: Int) {
        GenderParceler.writeNullable(this.gender, parcel, flags)
        SkinParceler.writeNullable(this.skin, parcel, flags)
        HairParceler.writeNullable(this.hair, parcel, flags)
        AgeRangeParceler.writeNullable(this.ageRange, parcel, flags)
        HeightRangeParceler.writeNullable(this.heightRange, parcel, flags)
    }
}
