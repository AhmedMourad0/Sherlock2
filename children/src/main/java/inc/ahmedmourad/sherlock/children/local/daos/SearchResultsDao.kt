package inc.ahmedmourad.sherlock.children.local.daos

import androidx.room.*
import inc.ahmedmourad.sherlock.children.local.contract.Contract.ChildrenEntry
import inc.ahmedmourad.sherlock.children.local.model.entities.RoomChildEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
internal abstract class SearchResultsDao {

    @Query("""SELECT
            ${ChildrenEntry.COLUMN_ID},
            ${ChildrenEntry.COLUMN_PUBLICATION_DATE},
            ${ChildrenEntry.COLUMN_FIRST_NAME},
            ${ChildrenEntry.COLUMN_LAST_NAME},
            ${ChildrenEntry.COLUMN_PICTURE_URL},
            ${ChildrenEntry.COLUMN_LOCATION},
            ${ChildrenEntry.COLUMN_NOTES},
            ${ChildrenEntry.COLUMN_GENDER},
            ${ChildrenEntry.COLUMN_SKIN},
            ${ChildrenEntry.COLUMN_HAIR},
            ${ChildrenEntry.COLUMN_START_AGE},
            ${ChildrenEntry.COLUMN_END_AGE},
            ${ChildrenEntry.COLUMN_START_HEIGHT},
            ${ChildrenEntry.COLUMN_END_HEIGHT},
            ${ChildrenEntry.COLUMN_SCORE}
            FROM
            ${ChildrenEntry.TABLE_NAME}""")
    abstract fun findAll(): Flowable<List<RoomChildEntity>>

    @Query("""SELECT
            ${ChildrenEntry.COLUMN_SCORE}
            FROM
            ${ChildrenEntry.TABLE_NAME}
            WHERE
            ${ChildrenEntry.COLUMN_ID} = :childId""")
    protected abstract fun findScore(childId: String): Maybe<Int>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun update(child: RoomChildEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun bulkInsert(resultsEntities: List<RoomChildEntity>)

    @Query("DELETE FROM ${ChildrenEntry.TABLE_NAME}")
    abstract fun deleteAll()

    @Transaction
    protected open fun replaceAllTransaction(resultsEntities: List<RoomChildEntity>) {
        deleteAll()
        bulkInsert(resultsEntities)
    }

    fun replaceAll(resultsEntities: List<RoomChildEntity>): Completable {
        return Completable.fromAction { replaceAllTransaction(resultsEntities) }
    }

    fun updateIfExists(child: RoomChildEntity): Maybe<RoomChildEntity> {
        return findScore(child.id).map {
            child.copy(score = it)
        }.flatMap { update(it).andThen(Maybe.just(it)) }
    }
}
