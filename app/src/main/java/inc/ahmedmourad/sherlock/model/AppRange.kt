package inc.ahmedmourad.sherlock.model

import org.parceler.Parcel
import org.parceler.ParcelConstructor

@Parcel(Parcel.Serialization.BEAN)
data class AppRange @ParcelConstructor constructor(val from: Int, val to: Int)
