package inc.ahmedmourad.sherlock.model.core

import android.os.Parcelable
import inc.ahmedmourad.sherlock.domain.model.children.ChildQuery
import inc.ahmedmourad.sherlock.domain.model.children.SimpleRetrievedChild
import inc.ahmedmourad.sherlock.model.children.AppPublishedChild
import inc.ahmedmourad.sherlock.model.parcelers.AppPublishedChildParceler
import inc.ahmedmourad.sherlock.model.parcelers.ChildQueryParceler
import inc.ahmedmourad.sherlock.model.parcelers.SimpleRetrievedChildParceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import kotlinx.android.parcel.TypeParceler

@Parcelize
@TypeParceler<SimpleRetrievedChild, SimpleRetrievedChildParceler>
@TypeParceler<AppPublishedChild, AppPublishedChildParceler>
@TypeParceler<ChildQuery, ChildQueryParceler>
class ParcelableWrapper<T>(val value: @RawValue T) : Parcelable

fun <T> T.parcelize() = ParcelableWrapper(this)
