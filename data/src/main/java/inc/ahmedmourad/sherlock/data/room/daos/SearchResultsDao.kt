package inc.ahmedmourad.sherlock.data.room.daos

import androidx.room.*
import inc.ahmedmourad.sherlock.data.room.contract.RoomContract.SearchResultsEntry
import inc.ahmedmourad.sherlock.data.room.entities.RoomChildEntity
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
abstract class SearchResultsDao {

    @Query("""SELECT
            ${SearchResultsEntry.COLUMN_ID},
            ${SearchResultsEntry.COLUMN_TIME_MILLIS},
            ${SearchResultsEntry.COLUMN_FIRST_NAME},
            ${SearchResultsEntry.COLUMN_LAST_NAME},
            ${SearchResultsEntry.COLUMN_PICTURE_URL},
            ${SearchResultsEntry.COLUMN_LOCATION},
            ${SearchResultsEntry.COLUMN_NOTES},
            ${SearchResultsEntry.COLUMN_GENDER},
            ${SearchResultsEntry.COLUMN_SKIN},
            ${SearchResultsEntry.COLUMN_HAIR},
            ${SearchResultsEntry.COLUMN_START_AGE},
            ${SearchResultsEntry.COLUMN_END_AGE},
            ${SearchResultsEntry.COLUMN_START_HEIGHT},
            ${SearchResultsEntry.COLUMN_END_HEIGHT},
            ${SearchResultsEntry.COLUMN_SCORE}
            FROM
            ${SearchResultsEntry.TABLE_NAME}""")
    abstract fun getResults(): Flowable<List<RoomChildEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun bulkInsert(resultsEntities: List<RoomChildEntity>)

    @Query("DELETE FROM ${SearchResultsEntry.TABLE_NAME}")
    protected abstract fun deleteAll()

    @Transaction
    protected open fun replaceResultsTransaction(resultsEntities: List<RoomChildEntity>) {
        deleteAll()
        bulkInsert(resultsEntities)
    }

    fun replaceResults(resultsEntities: List<RoomChildEntity>): Completable {
        return Completable.fromAction { replaceResultsTransaction(resultsEntities) }
    }
}
