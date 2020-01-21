package inc.ahmedmourad.sherlock.children.remote.mapper

import inc.ahmedmourad.sherlock.children.remote.model.*
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.children.*

internal fun DomainPublishedChild.toFirebasePublishedChild() = FirebasePublishedChild(
        name.toFirebaseName(),
        notes,
        location.toFirebaseLocation(),
        appearance.toFirebaseEstimatedAppearance(),
        picture
)

internal fun DomainSimpleRetrievedChild.toFirebaseSimpleChild() = FirebaseSimpleRetrievedChild(
        id,
        publicationDate,
        name.toFirebaseName(),
        notes,
        locationName,
        locationAddress,
        pictureUrl
)

internal fun DomainChildCriteriaRules.toFirebaseChildCriteriaRules() = FirebaseChildCriteriaRules(
        name.toFirebaseName(),
        location.toFirebaseLocation(),
        appearance.toFirebaseExactAppearance()
)

private fun DomainLocation.toFirebaseLocation(): FirebaseLocation {
    return FirebaseLocation(
            id,
            name,
            address,
            coordinates.toFirebaseCoordinates()
    )
}

private fun DomainCoordinates.toFirebaseCoordinates() = FirebaseCoordinates(latitude, longitude)

private fun DomainName.toFirebaseName() = FirebaseName(first, last)

private fun DomainEstimatedAppearance.toFirebaseEstimatedAppearance() = FirebaseEstimatedAppearance(
        gender,
        skin,
        hair,
        age.toFirebaseRange(),
        height.toFirebaseRange()
)

private fun DomainRange.toFirebaseRange() = FirebaseRange(from, to)

private fun DomainExactAppearance.toFirebaseExactAppearance() = FirebaseExactAppearance(
        gender,
        skin,
        hair,
        age,
        height
)
