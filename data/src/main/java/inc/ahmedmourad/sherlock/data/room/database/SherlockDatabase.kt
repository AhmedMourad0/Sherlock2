package inc.ahmedmourad.sherlock.data.room.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import inc.ahmedmourad.sherlock.data.room.contract.RoomContract
import inc.ahmedmourad.sherlock.data.room.daos.SearchResultsDao
import inc.ahmedmourad.sherlock.data.room.entities.RoomChildEntity
import splitties.init.appCtx

@Database(entities = [RoomChildEntity::class], version = 1)
abstract class SherlockDatabase : RoomDatabase() {

    abstract fun resultsDao(): SearchResultsDao

    companion object {

        @Volatile
        private var INSTANCE: SherlockDatabase? = null

        fun getInstance() = INSTANCE ?: synchronized(SherlockDatabase::class.java) {
            INSTANCE ?: buildDatabase().also { INSTANCE = it }
        }

        private fun buildDatabase() = Room.databaseBuilder(
                appCtx,
                SherlockDatabase::class.java,
                RoomContract.DATABASE_NAME
        ).build()
    }
}
