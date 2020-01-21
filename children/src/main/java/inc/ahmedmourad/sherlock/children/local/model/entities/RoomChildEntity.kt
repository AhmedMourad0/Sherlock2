package inc.ahmedmourad.sherlock.children.local.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import arrow.core.Tuple2
import arrow.core.toT
import inc.ahmedmourad.sherlock.children.local.contract.Contract.ChildrenEntry
import inc.ahmedmourad.sherlock.children.local.model.RoomName
import inc.ahmedmourad.sherlock.children.local.model.RoomSimpleChild
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.constants.findEnum
import inc.ahmedmourad.sherlock.domain.model.children.*

@Entity(tableName = ChildrenEntry.TABLE_NAME)
internal data class RoomChildEntity(
        @PrimaryKey
        @ColumnInfo(name = ChildrenEntry.COLUMN_ID)
        val id: String,

        @ColumnInfo(name = ChildrenEntry.COLUMN_PUBLICATION_DATE)
        val publicationDate: Long,

        @ColumnInfo(name = ChildrenEntry.COLUMN_FIRST_NAME)
        val firstName: String,

        @ColumnInfo(name = ChildrenEntry.COLUMN_LAST_NAME)
        val lastName: String,

        @ColumnInfo(name = ChildrenEntry.COLUMN_LOCATION_ID)
        val locationId: String,

        @ColumnInfo(name = ChildrenEntry.COLUMN_LOCATION_NAME)
        val locationName: String,

        @ColumnInfo(name = ChildrenEntry.COLUMN_LOCATION_ADDRESS)
        val locationAddress: String,

        @ColumnInfo(name = ChildrenEntry.COLUMN_LOCATION_LATITUDE)
        val locationLatitude: Double,

        @ColumnInfo(name = ChildrenEntry.COLUMN_LOCATION_LONGITUDE)
        val locationLongitude: Double,

        @ColumnInfo(name = ChildrenEntry.COLUMN_NOTES)
        val notes: String,

        @ColumnInfo(name = ChildrenEntry.COLUMN_GENDER)
        val gender: Int,

        @ColumnInfo(name = ChildrenEntry.COLUMN_SKIN)
        val skin: Int,

        @ColumnInfo(name = ChildrenEntry.COLUMN_HAIR)
        val hair: Int,

        @ColumnInfo(name = ChildrenEntry.COLUMN_START_AGE)
        val startAge: Int,

        @ColumnInfo(name = ChildrenEntry.COLUMN_END_AGE)
        val endAge: Int,

        @ColumnInfo(name = ChildrenEntry.COLUMN_START_HEIGHT)
        val startHeight: Int,

        @ColumnInfo(name = ChildrenEntry.COLUMN_END_HEIGHT)
        val endHeight: Int,

        @ColumnInfo(name = ChildrenEntry.COLUMN_PICTURE_URL)
        val pictureUrl: String,

        @ColumnInfo(name = ChildrenEntry.COLUMN_SCORE)
        val score: Int
) {

    fun toDomainRetrievedChild(): Tuple2<DomainRetrievedChild, Int> {
        return DomainRetrievedChild(
                id,
                publicationDate,
                extractDomainName(),
                notes,
                extractDomainLocation(),
                extractDomainEstimatedAppearance(),
                pictureUrl
        ) toT score
    }

    fun simplify(): Tuple2<RoomSimpleChild, Int> {

        return RoomSimpleChild(
                id,
                publicationDate,
                extractRoomName(),
                notes,
                locationName,
                locationAddress,
                pictureUrl
        ) toT score
    }

    private fun extractRoomName() = RoomName(firstName, lastName)

    private fun extractDomainName() = DomainName(firstName, lastName)

    private fun extractDomainEstimatedAppearance() = DomainEstimatedAppearance(
            findEnum(gender, Gender.values()),
            findEnum(skin, Skin.values()),
            findEnum(hair, Hair.values()),
            extractDomainAge(),
            extractDomainHeight()
    )

    private fun extractDomainAge() = DomainRange(startAge, endAge)

    private fun extractDomainHeight() = DomainRange(startHeight, endHeight)

    private fun extractDomainLocation() = DomainLocation(
            locationId,
            locationName,
            locationAddress,
            DomainCoordinates(locationLatitude, locationLongitude)
    )
}
