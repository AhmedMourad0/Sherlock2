package inc.ahmedmourad.sherlock.children.local.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import inc.ahmedmourad.sherlock.children.local.contract.Contract.ChildrenEntry
import inc.ahmedmourad.sherlock.children.local.model.RoomLocation
import inc.ahmedmourad.sherlock.children.local.model.RoomName
import inc.ahmedmourad.sherlock.children.local.model.RoomSimpleChild
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.constants.findEnum
import inc.ahmedmourad.sherlock.domain.model.DomainEstimatedAppearance
import inc.ahmedmourad.sherlock.domain.model.DomainName
import inc.ahmedmourad.sherlock.domain.model.DomainRange
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild

@Entity(tableName = ChildrenEntry.TABLE_NAME)
internal data class RoomChildEntity(
        @PrimaryKey
        @ColumnInfo(name = ChildrenEntry.COLUMN_ID)
        val id: String = "",

        @ColumnInfo(name = ChildrenEntry.COLUMN_PUBLICATION_DATE)
        val publicationDate: Long = 0,

        @ColumnInfo(name = ChildrenEntry.COLUMN_FIRST_NAME)
        val firstName: String = "",

        @ColumnInfo(name = ChildrenEntry.COLUMN_LAST_NAME)
        val lastName: String = "",

        @ColumnInfo(name = ChildrenEntry.COLUMN_LOCATION)
        val location: String = "",

        @ColumnInfo(name = ChildrenEntry.COLUMN_NOTES)
        val notes: String = "",

        @ColumnInfo(name = ChildrenEntry.COLUMN_GENDER)
        val gender: Int = 0,

        @ColumnInfo(name = ChildrenEntry.COLUMN_SKIN)
        val skin: Int = 0,

        @ColumnInfo(name = ChildrenEntry.COLUMN_HAIR)
        val hair: Int = 0,

        @ColumnInfo(name = ChildrenEntry.COLUMN_START_AGE)
        val startAge: Int = 0,

        @ColumnInfo(name = ChildrenEntry.COLUMN_END_AGE)
        val endAge: Int = 0,

        @ColumnInfo(name = ChildrenEntry.COLUMN_START_HEIGHT)
        val startHeight: Int = 0,

        @ColumnInfo(name = ChildrenEntry.COLUMN_END_HEIGHT)
        val endHeight: Int = 0,

        @ColumnInfo(name = ChildrenEntry.COLUMN_PICTURE_URL)
        val pictureUrl: String = "",

        @ColumnInfo(name = ChildrenEntry.COLUMN_SCORE)
        val score: Int = -1) {

    fun toDomainRetrievedChild() = DomainRetrievedChild(
            id,
            publicationDate,
            extractDomainName(),
            notes,
            RoomLocation.parse(location).toDomainLocation(),
            extractDomainEstimatedAppearance(),
            pictureUrl
    ) to score

    fun simplify(): Pair<RoomSimpleChild, Int> {

        val roomLocation = RoomLocation.parse(location)

        return RoomSimpleChild(
                id,
                publicationDate,
                extractRoomName(),
                notes,
                roomLocation.name,
                roomLocation.address,
                pictureUrl
        ) to score
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
}
