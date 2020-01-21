package inc.ahmedmourad.sherlock.children.local.mapper

import arrow.core.Tuple2
import arrow.core.extensions.tuple2.bifunctor.mapLeft
import arrow.core.toT
import inc.ahmedmourad.sherlock.children.local.model.RoomSimpleChild
import inc.ahmedmourad.sherlock.children.local.model.entities.RoomChildEntity
import inc.ahmedmourad.sherlock.domain.model.children.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.children.DomainSimpleRetrievedChild

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

internal fun Tuple2<DomainRetrievedChild, Int>.toRoomChildEntity(): RoomChildEntity {
    return RoomChildEntity(
            a.id,
            a.publicationDate,
            a.name.first,
            a.name.last,
            a.location.id,
            a.location.name,
            a.location.address,
            a.location.coordinates.latitude,
            a.location.coordinates.longitude,
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
}

internal fun DomainRetrievedChild.toRoomChildEntity(score: Int): RoomChildEntity {
    return (this toT score).toRoomChildEntity()
}
