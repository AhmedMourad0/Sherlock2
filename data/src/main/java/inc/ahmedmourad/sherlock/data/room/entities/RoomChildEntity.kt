package inc.ahmedmourad.sherlock.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import inc.ahmedmourad.sherlock.data.room.contract.RoomContract.ChildrenEntry

@Entity(tableName = ChildrenEntry.TABLE_NAME)
data class RoomChildEntity(
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
        val score: Int = 0
)
