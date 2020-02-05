package inc.ahmedmourad.sherlock.model.parcelers

import android.os.Parcel
import arrow.core.orNull
import inc.ahmedmourad.sherlock.domain.model.children.Age
import kotlinx.android.parcel.Parceler

internal object AgeParceler : Parceler<Age> {

    override fun create(parcel: Parcel): Age {
        return Age.of(parcel.readInt()).orNull()!!
    }

    override fun Age.write(parcel: Parcel, flags: Int) {
        parcel.writeInt(this.value)
    }
}
