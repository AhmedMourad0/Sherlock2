package inc.ahmedmourad.sherlock.children.local.mapper

import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.toT
import inc.ahmedmourad.sherlock.children.local.entities.RoomChildEntity
import inc.ahmedmourad.sherlock.domain.model.children.RetrievedChild

internal fun Tuple2<RetrievedChild, Int?>.toRoomChildEntity(): RoomChildEntity {

    val (firstName, lastName) = when (val name = a.name) {
        is Either.Left -> name.a.value to null
        is Either.Right -> name.b.first.value to name.b.first.value
        else -> null to null
    }

    return RoomChildEntity(
            a.id.value,
            a.publicationDate,
            firstName,
            lastName,
            a.location?.id,
            a.location?.name,
            a.location?.address,
            a.location?.coordinates?.latitude,
            a.location?.coordinates?.longitude,
            a.notes,
            a.appearance.gender?.value,
            a.appearance.skin?.value,
            a.appearance.hair?.value,
            a.appearance.ageRange?.min?.value,
            a.appearance.ageRange?.max?.value,
            a.appearance.heightRange?.min?.value,
            a.appearance.heightRange?.max?.value,
            a.pictureUrl?.value,
            b
    )
}

internal fun RetrievedChild.toRoomChildEntity(score: Int?): RoomChildEntity {
    return (this toT score).toRoomChildEntity()
}
