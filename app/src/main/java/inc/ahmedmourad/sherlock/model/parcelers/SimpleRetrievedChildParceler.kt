package inc.ahmedmourad.sherlock.model.parcelers

import android.os.Parcel
import arrow.core.orNull
import inc.ahmedmourad.sherlock.domain.model.children.ChildId
import inc.ahmedmourad.sherlock.domain.model.children.SimpleRetrievedChild
import inc.ahmedmourad.sherlock.model.parcelers.utils.*
import kotlinx.android.parcel.Parceler

internal object SimpleRetrievedChildParceler : Parceler<SimpleRetrievedChild> {

    override fun create(parcel: Parcel): SimpleRetrievedChild {
        return SimpleRetrievedChild.of(
                ChildId(parcel.readString()!!),
                parcel.readLong(),
                parcel.readEitherNullable({ NameParceler.create(parcel) }, { FullNameParceler.create(parcel) }),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                UrlParceler.createNullable(parcel)
        ).orNull()!!
    }

    override fun SimpleRetrievedChild.write(parcel: Parcel, flags: Int) {
        parcel.writeString(this.id.value)
        parcel.writeLong(this.publicationDate)
        parcel.writeEitherNullable(
                this.name,
                { NameParceler.write(it, parcel, flags) },
                { FullNameParceler.write(it, parcel, flags) }
        )
        parcel.writeString(this.notes)
        parcel.writeString(this.locationName)
        parcel.writeString(this.locationAddress)
        UrlParceler.writeNullable(this.pictureUrl, parcel, flags)
    }
}
