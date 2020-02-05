package inc.ahmedmourad.sherlock.model.parcelers

import android.os.Parcel
import arrow.core.orNull
import inc.ahmedmourad.sherlock.domain.model.children.Name
import kotlinx.android.parcel.Parceler

internal object NameParceler : Parceler<Name> {

    override fun create(parcel: Parcel): Name {
        return Name.of(parcel.readString()!!).orNull()!!
    }

    override fun Name.write(parcel: Parcel, flags: Int) {
        parcel.writeString(this.value)
    }
}
