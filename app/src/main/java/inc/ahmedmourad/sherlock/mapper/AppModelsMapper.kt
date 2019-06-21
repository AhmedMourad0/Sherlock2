package inc.ahmedmourad.sherlock.mapper

import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.*
import inc.ahmedmourad.sherlock.model.*
import inc.ahmedmourad.sherlock.utils.getImageBytes

object AppModelsMapper {

    fun toDomainChildCriteriaRules(rules: AppChildCriteriaRules) = DomainChildCriteriaRules(
            toDomainName(rules.name),
            toDomainLocation(rules.location),
            toDomainIntAppearance(rules.appearance)
    )

    private fun toDomainIntAppearance(appearance: AppAppearance<PInt>) = DomainAppearance(
            appearance.gender,
            appearance.skin,
            appearance.hair,
            appearance.age.value,
            appearance.height.value
    )

    fun toAppChild(child: DomainUrlChild) = AppUrlChild(
            child.id,
            child.publicationDate,
            toAppName(child.name),
            child.notes,
            toAppLocation(child.location),
            toAppAppearance(child.appearance),
            child.pictureUrl
    )

    private fun toAppLocation(location: DomainLocation) = AppLocation(
            location.id,
            location.name,
            location.address,
            toAppCoordinates(location.coordinates)
    )

    private fun toAppCoordinates(coordinates: DomainCoordinates) = AppCoordinates(
            coordinates.latitude,
            coordinates.longitude
    )

    private fun toAppName(name: DomainName) = AppName(
            name.first,
            name.last
    )

    private fun toAppAppearance(appearance: DomainAppearance<DomainRange>) = AppAppearance(
            appearance.gender,
            appearance.skin,
            appearance.hair,
            toAppRange(appearance.age),
            toAppRange(appearance.height)
    )

    private fun toAppRange(range: DomainRange) = AppRange(
            range.from,
            range.to
    )

    fun toDomainPictureChild(child: AppPictureChild) = DomainPictureChild(
            child.id,
            child.publicationDate,
            toDomainName(child.name),
            child.notes,
            toDomainLocation(child.location),
            toDomainRangeAppearance(child.appearance),
            child.picturePath.run { if (isNotBlank()) getImageBytes(this) else getImageBytes(R.drawable.placeholder) }
    )

    private fun toDomainLocation(location: AppLocation) = DomainLocation(location.id,
            location.name,
            location.address,
            toDomainCoordinates(location.coordinates)
    )

    private fun toDomainCoordinates(coordinates: AppCoordinates) = DomainCoordinates(coordinates.latitude,
            coordinates.longitude
    )

    private fun toDomainName(name: AppName) = DomainName(
            name.first,
            name.last
    )

    private fun toDomainRangeAppearance(appearance: AppAppearance<AppRange>) = DomainAppearance(
            appearance.gender,
            appearance.skin,
            appearance.hair,
            toDomainRange(appearance.age),
            toDomainRange(appearance.height)
    )

    private fun toDomainRange(range: AppRange) = DomainRange(
            range.from,
            range.to
    )
}
