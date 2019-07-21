package inc.ahmedmourad.sherlock.data.mapper

import inc.ahmedmourad.sherlock.data.firebase.model.*
import inc.ahmedmourad.sherlock.data.room.entities.RoomChildEntity
import inc.ahmedmourad.sherlock.data.room.model.RoomCoordinates
import inc.ahmedmourad.sherlock.data.room.model.RoomLocation
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.constants.findEnum
import inc.ahmedmourad.sherlock.domain.model.*

object DataModelsMapper {

    fun toRoomChildEntity(child: Pair<DomainUrlChild, Int>) = RoomChildEntity(
            child.first.id,
            child.first.publicationDate,
            child.first.name.first,
            child.first.name.last,
            toRoomLocation(child.first.location).store(),
            child.first.notes,
            child.first.appearance.gender.value,
            child.first.appearance.skin.value,
            child.first.appearance.hair.value,
            child.first.appearance.age.from,
            child.first.appearance.age.to,
            child.first.appearance.height.from,
            child.first.appearance.height.to,
            child.first.pictureUrl,
            child.second
    )

    fun toRoomChildEntity(child: DomainUrlChild) = toRoomChildEntity(child to -1)

    private fun toRoomLocation(location: DomainLocation) = RoomLocation(
            location.id,
            location.name,
            location.address,
            toRoomCoordinates(location.coordinates)
    )

    private fun toRoomCoordinates(coordinates: DomainCoordinates) = RoomCoordinates(
            coordinates.latitude,
            coordinates.longitude
    )

    fun toDomainUrlChild(entity: RoomChildEntity) = DomainUrlChild(
            entity.id,
            entity.publicationDate,
            DomainName(entity.firstName, entity.lastName),
            entity.notes,
            toDomainLocation(RoomLocation.parse(entity.location)),
            DomainAppearance(
                    findEnum(entity.gender, Gender.values()),
                    findEnum(entity.skin, Skin.values()),
                    findEnum(entity.hair, Hair.values()),
                    DomainRange(entity.startAge, entity.endAge),
                    DomainRange(entity.startHeight, entity.endHeight)
            ), entity.pictureUrl
    ) to entity.score

    private fun toDomainLocation(location: RoomLocation) = DomainLocation(
            location.id,
            location.name,
            location.address,
            toDomainCoordinates(location.coordinates)
    )

    private fun toDomainCoordinates(coordinates: RoomCoordinates) = DomainCoordinates(
            coordinates.latitude,
            coordinates.longitude
    )

    fun toDomainUrlChild(child: FirebaseUrlChild) = DomainUrlChild(
            child.id,
            child.publicationDate,
            toDomainName(child.name),
            child.notes,
            toDomainLocation(child.location),
            toDomainAppearance(child.appearance),
            child.pictureUrl
    )

    private fun toDomainLocation(location: FirebaseLocation) = DomainLocation(
            location.id,
            location.name,
            location.address,
            toDomainCoordinates(location.coordinates)
    )

    private fun toDomainCoordinates(coordinates: FirebaseCoordinates) = DomainCoordinates(
            coordinates.latitude,
            coordinates.longitude
    )

    private fun toDomainName(name: FirebaseName) = DomainName(
            name.first,
            name.last
    )

    private fun toDomainAppearance(appearance: FirebaseAppearance) = DomainAppearance(
            appearance.gender,
            appearance.skin,
            appearance.hair,
            toDomainRange(appearance.age),
            toDomainRange(appearance.height)
    )

    private fun toDomainRange(range: FirebaseRange) = DomainRange(
            range.from,
            range.to
    )

    fun toFirebasePictureChild(child: DomainPictureChild) = FirebasePictureChild(
            child.id,
            child.publicationDate,
            toFirebaseName(child.name),
            child.notes,
            toFirebaseLocation(child.location),
            toFirebaseAppearance(child.appearance),
            child.picture
    )

    private fun toFirebaseLocation(location: DomainLocation) = FirebaseLocation(
            location.id,
            location.name,
            location.address,
            toFirebaseCoordinates(location.coordinates)
    )

    private fun toFirebaseCoordinates(coordinates: DomainCoordinates) = FirebaseCoordinates(
            coordinates.latitude,
            coordinates.longitude
    )

    private fun toFirebaseName(name: DomainName) = FirebaseName(
            name.first,
            name.last
    )

    private fun toFirebaseAppearance(appearance: DomainAppearance<DomainRange>) = FirebaseAppearance(
            appearance.gender,
            appearance.skin,
            appearance.hair,
            toFirebaseRange(appearance.age),
            toFirebaseRange(appearance.height)
    )

    private fun toFirebaseRange(range: DomainRange) = FirebaseRange(
            range.from,
            range.to
    )
}
