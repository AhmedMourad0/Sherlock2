package inc.ahmedmourad.sherlock.dagger.modules.data

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.data.firebase.repository.FirebaseCloudRepository
import inc.ahmedmourad.sherlock.data.repositories.CloudRepository
import inc.ahmedmourad.sherlock.data.repositories.LocalRepository
import inc.ahmedmourad.sherlock.data.repositories.SherlockRepository
import inc.ahmedmourad.sherlock.data.room.database.SherlockDatabase
import inc.ahmedmourad.sherlock.data.room.repository.RoomLocalRepository
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.repository.Repository

@Module
class SherlockRepositoryModule {
    @Provides
    @Reusable
    fun provideSherlockRepository(localRepository: Lazy<LocalRepository>, cloudRepository: Lazy<CloudRepository>): Repository = SherlockRepository(localRepository, cloudRepository)
}

@Module
class FirebaseCloudRepositoryModule {
    @Provides
    @Reusable
    fun provideFirebaseCloudRepository(bus: Lazy<Bus>, provider: Lazy<Bus.PublishingState.Provider>): CloudRepository = FirebaseCloudRepository(bus, provider)
}

@Module
class RoomLocalRepositoryModule {
    @Provides
    @Reusable
    fun provideRoomLocalRepository(db: Lazy<SherlockDatabase>): LocalRepository = RoomLocalRepository(db)
}
