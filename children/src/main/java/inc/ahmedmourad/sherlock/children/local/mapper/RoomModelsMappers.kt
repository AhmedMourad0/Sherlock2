package inc.ahmedmourad.sherlock.children.local.mapper

import arrow.core.Tuple2
import arrow.core.extensions.tuple2.bifunctor.mapLeft
import arrow.core.toT
import inc.ahmedmourad.sherlock.children.local.model.RoomCoordinates
import inc.ahmedmourad.sherlock.children.local.model.RoomLocation
import inc.ahmedmourad.sherlock.children.local.model.RoomSimpleChild
import inc.ahmedmourad.sherlock.children.local.model.entities.RoomChildEntity
import inc.ahmedmourad.sherlock.domain.model.DomainCoordinates
import inc.ahmedmourad.sherlock.domain.model.DomainLocation
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild

internal fun Tuple2<RoomSimpleChild, Int>.toDomainSimpleChild(): Tuple2<DomainSimpleRetrievedChild, Int> {
    return this.mapLeft {
        DomainSimpleRetrievedChild(
                it.id,
                it.publicationDate,
                it.name.toDomainName(),
                it.notes,
                it.locationName,
                it.locationAddress,
                it.pictureUrl
        )
    }
}

internal fun Tuple2<DomainRetrievedChild, Int>.toRoomChildEntity() = RoomChildEntity(
        a.id,
        a.publicationDate,
        a.name.first,
        a.name.last,
        a.location.toRoomLocation().store(),
        a.notes,
        a.appearance.gender.value,
        a.appearance.skin.value,
        a.appearance.hair.value,
        a.appearance.age.from,
        a.appearance.age.to,
        a.appearance.height.from,
        a.appearance.height.to,
        a.pictureUrl,
        b
)

internal fun DomainRetrievedChild.toRoomChildEntity(score: Int): RoomChildEntity {
    return (this toT score).toRoomChildEntity()
}

private fun DomainLocation.toRoomLocation() = RoomLocation(
        id,
        name,
        address,
        coordinates.toRoomCoordinates()
)

private fun DomainCoordinates.toRoomCoordinates() = RoomCoordinates(latitude, longitude)
