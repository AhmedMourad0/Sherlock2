package inc.ahmedmourad.sherlock.mapper

import inc.ahmedmourad.sherlock.domain.model.auth.DomainIncompleteUser
import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignedInUser
import inc.ahmedmourad.sherlock.domain.model.auth.DomainUser
import inc.ahmedmourad.sherlock.domain.model.children.*
import inc.ahmedmourad.sherlock.model.auth.AppIncompleteUser
import inc.ahmedmourad.sherlock.model.auth.AppSignedInUser
import inc.ahmedmourad.sherlock.model.auth.AppUser
import inc.ahmedmourad.sherlock.model.children.*

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

internal fun DomainSignedInUser.toAppSignedInUser() = AppSignedInUser(
        id,
        registrationDate,
        email,
        name,
        phoneNumber,
        pictureUrl
)

internal fun DomainUser.toAppSignedInUser() = AppUser(
        id,
        registrationDate,
        lastLoginDate,
        email,
        name,
        phoneNumber,
        pictureUrl
)

internal fun DomainIncompleteUser.toAppIncompleteUser() = AppIncompleteUser(
        id,
        email,
        name,
        phoneNumber,
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
