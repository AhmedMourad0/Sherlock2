package inc.ahmedmourad.sherlock.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import inc.ahmedmourad.sherlock.data.room.contract.RoomContract.SearchResultsEntry

@Entity(tableName = SearchResultsEntry.TABLE_NAME)
data class RoomChildEntity(
        @PrimaryKey
        @ColumnInfo(name = SearchResultsEntry.COLUMN_ID)
        val id: String = "",

        @ColumnInfo(name = SearchResultsEntry.COLUMN_TIME_MILLIS)
        val timeMillis: Long = 0,

        @ColumnInfo(name = SearchResultsEntry.COLUMN_FIRST_NAME)
        val firstName: String = "",

        @ColumnInfo(name = SearchResultsEntry.COLUMN_LAST_NAME)
        val lastName: String = "",

        @ColumnInfo(name = SearchResultsEntry.COLUMN_LOCATION)
        val location: String = "",

        @ColumnInfo(name = SearchResultsEntry.COLUMN_NOTES)
        val notes: String = "",

        @ColumnInfo(name = SearchResultsEntry.COLUMN_GENDER)
        val gender: Int = 0,

        @ColumnInfo(name = SearchResultsEntry.COLUMN_SKIN)
        val skin: Int = 0,

        @ColumnInfo(name = SearchResultsEntry.COLUMN_HAIR)
        val hair: Int = 0,

        @ColumnInfo(name = SearchResultsEntry.COLUMN_START_AGE)
        val startAge: Int = 0,

        @ColumnInfo(name = SearchResultsEntry.COLUMN_END_AGE)
        val endAge: Int = 0,

        @ColumnInfo(name = SearchResultsEntry.COLUMN_START_HEIGHT)
        val startHeight: Int = 0,

        @ColumnInfo(name = SearchResultsEntry.COLUMN_END_HEIGHT)
        val endHeight: Int = 0,

        @ColumnInfo(name = SearchResultsEntry.COLUMN_PICTURE_URL)
        val pictureUrl: String = "",

        @ColumnInfo(name = SearchResultsEntry.COLUMN_SCORE)
        val score: Int = 0
)
