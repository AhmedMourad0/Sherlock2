package inc.ahmedmourad.sherlock.children.local.mapper

import inc.ahmedmourad.sherlock.children.local.model.RoomCoordinates
import inc.ahmedmourad.sherlock.children.local.model.RoomLocation
import inc.ahmedmourad.sherlock.children.local.model.RoomSimpleChild
import inc.ahmedmourad.sherlock.children.local.model.entities.RoomChildEntity
import inc.ahmedmourad.sherlock.domain.model.DomainCoordinates
import inc.ahmedmourad.sherlock.domain.model.DomainLocation
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild

internal fun Pair<RoomSimpleChild, Int>.toDomainSimpleChild() = DomainSimpleRetrievedChild(
        first.id,
        first.publicationDate,
        first.name.toDomainName(),
        first.notes,
        first.locationName,
        first.locationAddress,
        first.pictureUrl
) to second

internal fun Pair<DomainRetrievedChild, Int>.toRoomChildEntity() = RoomChildEntity(
        first.id,
        first.publicationDate,
        first.name.first,
        first.name.last,
        first.location.toRoomLocation().store(),
        first.notes,
        first.appearance.gender.value,
        first.appearance.skin.value,
        first.appearance.hair.value,
        first.appearance.age.from,
        first.appearance.age.to,
        first.appearance.height.from,
        first.appearance.height.to,
        first.pictureUrl,
        second
)

internal fun DomainRetrievedChild.toRoomChildEntity(score: Int) = (this to score).toRoomChildEntity()

private fun DomainLocation.toRoomLocation() = RoomLocation(
        id,
        name,
        address,
        coordinates.toRoomCoordinates()
)

private fun DomainCoordinates.toRoomCoordinates() = RoomCoordinates(latitude, longitude)
