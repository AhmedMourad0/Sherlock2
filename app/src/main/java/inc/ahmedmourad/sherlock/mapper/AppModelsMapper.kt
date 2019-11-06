package inc.ahmedmourad.sherlock.mapper

import inc.ahmedmourad.sherlock.domain.model.*
import inc.ahmedmourad.sherlock.model.*

internal fun DomainRetrievedChild.toAppChild() = AppRetrievedChild(
        id,
        publicationDate,
        name.toAppName(),
        notes,
        location.toAppLocation(),
        appearance.toAppEstimatedAppearance(),
        pictureUrl
)

internal fun DomainSimpleRetrievedChild.toAppSimpleChild() = AppSimpleRetrievedChild(
        id,
        publicationDate,
        name.toAppName(),
        notes,
        locationName,
        locationAddress,
        pictureUrl
)

private fun DomainLocation.toAppLocation() = AppLocation(
        id,
        name,
        address,
        coordinates.toAppCoordinates()
)

private fun DomainCoordinates.toAppCoordinates() = AppCoordinates(latitude, longitude)

private fun DomainName.toAppName() = AppName(first, last)

private fun DomainEstimatedAppearance.toAppEstimatedAppearance() = AppEstimatedAppearance(
        gender,
        skin,
        hair,
        age.toAppRange(),
        height.toAppRange()
)

private fun DomainRange.toAppRange() = AppRange(from, to)
