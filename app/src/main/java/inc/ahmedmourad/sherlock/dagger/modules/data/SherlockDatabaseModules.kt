package inc.ahmedmourad.sherlock.dagger.modules.data

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.data.room.database.SherlockDatabase

@Module
class SherlockDatabaseModule {
    @Provides
    @Reusable
    fun provideSherlockDatabase(): SherlockDatabase = SherlockDatabase.getInstance()
}
