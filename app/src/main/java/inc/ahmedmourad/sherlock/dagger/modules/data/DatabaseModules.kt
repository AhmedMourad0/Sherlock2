package inc.ahmedmourad.sherlock.dagger.modules.data

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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

@Module
class FirebaseDatabaseModule {
    @Provides
    @Reusable
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()
}

@Module
class FirebaseFirestoreModule {
    @Provides
    @Reusable
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}

@Module
class FirebaseStorageModule {
    @Provides
    @Reusable
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}

