package inc.ahmedmourad.sherlock.dagger.modules.data

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.data.firebase.repositories.FirebaseDatabaseCloudRepository
import inc.ahmedmourad.sherlock.data.firebase.repositories.FirebaseFirestoreCloudRepository
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
class FirebaseDatabaseCloudRepositoryModule {
    @Provides
    @Reusable
    fun provideFirebaseDatabaseCloudRepository(
            db: Lazy<FirebaseDatabase>,
            storage: Lazy<FirebaseStorage>,
            bus: Lazy<Bus>
    ): CloudRepository = FirebaseDatabaseCloudRepository(db, storage, bus)
}

@Module
class FirebaseFirestoreCloudRepositoryModule {
    @Provides
    @Reusable
    fun provideFirebaseStoreCloudRepository(
            db: Lazy<FirebaseFirestore>,
            storage: Lazy<FirebaseStorage>,
            bus: Lazy<Bus>
    ): CloudRepository = FirebaseFirestoreCloudRepository(db, storage, bus)
}

@Module
class RoomLocalRepositoryModule {
    @Provides
    @Reusable
    fun provideRoomLocalRepository(db: Lazy<SherlockDatabase>): LocalRepository = RoomLocalRepository(db)
}
