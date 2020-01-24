package inc.ahmedmourad.sherlock.model.children

import android.os.Parcelable
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class AppChildCriteriaRules(
        val name: AppName,
        val location: AppLocation?,
        val appearance: AppExactAppearance
) : Parcelable {
    fun toDomainChildCriteriaRules() = DomainChildCriteriaRules(
            name.toDomainName(),
            location?.toDomainLocation(),
            appearance.toDomainAppearance()
    )
}
