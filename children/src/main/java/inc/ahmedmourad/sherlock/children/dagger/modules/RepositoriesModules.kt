package inc.ahmedmourad.sherlock.children.dagger.modules

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.children.dagger.modules.qualifiers.ChildrenFirebaseFirestoreQualifier
import inc.ahmedmourad.sherlock.children.dagger.modules.qualifiers.ChildrenFirebaseStorageQualifier
import inc.ahmedmourad.sherlock.children.images.repository.ChildrenFirebaseStorageImageRepository
import inc.ahmedmourad.sherlock.children.local.database.SherlockDatabase
import inc.ahmedmourad.sherlock.children.local.repository.ChildrenRoomLocalRepository
import inc.ahmedmourad.sherlock.children.remote.repositories.ChildrenFirebaseFirestoreRemoteRepository
import inc.ahmedmourad.sherlock.children.repository.SherlockChildrenRepository
import inc.ahmedmourad.sherlock.children.repository.dependencies.ChildrenImageRepository
import inc.ahmedmourad.sherlock.children.repository.dependencies.ChildrenLocalRepository
import inc.ahmedmourad.sherlock.children.repository.dependencies.ChildrenRemoteRepository
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.data.AuthManager
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager

@Module(includes = [
    ChildrenRemoteRepositoryModule::class,
    ChildrenLocalRepositoryModule::class
])
internal object ChildrenRepositoryModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideChildrenRepository(
            childrenLocalRepository: Lazy<ChildrenLocalRepository>,
            childrenRemoteRepository: Lazy<ChildrenRemoteRepository>,
            bus: Lazy<Bus>
    ): ChildrenRepository = SherlockChildrenRepository(childrenLocalRepository, childrenRemoteRepository, bus)
}

@Module(includes = [FirebaseFirestoreModule::class, ChildrenImageRepositoryModule::class])
internal object ChildrenRemoteRepositoryModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideChildrenRemoteRepository(
            @ChildrenFirebaseFirestoreQualifier db: Lazy<FirebaseFirestore>,
            childrenImageRepository: Lazy<ChildrenImageRepository>,
            authEnforcer: Lazy<AuthManager.AuthEnforcer>,
            connectivityEnforcer: Lazy<ConnectivityManager.ConnectivityEnforcer>
    ): ChildrenRemoteRepository = ChildrenFirebaseFirestoreRemoteRepository(
            db,
            childrenImageRepository,
            authEnforcer,
            connectivityEnforcer
    )
}

@Module(includes = [FirebaseStorageModule::class])
internal object ChildrenImageRepositoryModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideChildrenImageRepository(
            @ChildrenFirebaseStorageQualifier storage: Lazy<FirebaseStorage>
    ): ChildrenImageRepository = ChildrenFirebaseStorageImageRepository(storage)
}

@Module(includes = [SherlockDatabaseModule::class])
internal object ChildrenLocalRepositoryModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideChildrenLocalRepository(db: Lazy<SherlockDatabase>): ChildrenLocalRepository = ChildrenRoomLocalRepository(db)
}
