package inc.ahmedmourad.sherlock.model

import org.parceler.Parcel
import org.parceler.ParcelConstructor

@Parcel(Parcel.Serialization.BEAN)
data class AppName @ParcelConstructor constructor(val first: String, val last: String)
