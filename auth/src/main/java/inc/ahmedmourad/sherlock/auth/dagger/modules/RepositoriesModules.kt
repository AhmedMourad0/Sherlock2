package inc.ahmedmourad.sherlock.auth.dagger.modules

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.auth.authenticator.AuthFirebaseAuthenticator
import inc.ahmedmourad.sherlock.auth.dagger.modules.qualifiers.AuthFirebaseFirestoreQualifier
import inc.ahmedmourad.sherlock.auth.dagger.modules.qualifiers.AuthFirebaseStorageQualifier
import inc.ahmedmourad.sherlock.auth.images.repository.AuthFirebaseStorageImageRepository
import inc.ahmedmourad.sherlock.auth.manager.SherlockAuthManager
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthAuthenticator
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthImageRepository
import inc.ahmedmourad.sherlock.auth.manager.dependencies.AuthRemoteRepository
import inc.ahmedmourad.sherlock.auth.remote.repository.AuthFirebaseFirestoreRemoteRepository
import inc.ahmedmourad.sherlock.domain.data.AuthManager
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager

@Module(includes = [
    AuthFirebaseAuthenticatorModule::class,
    AuthFirebaseFirestoreRemoteRepositoryModule::class,
    FirebaseAuthEnforcerModule::class
])
internal object FirebaseAuthManagerModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideFirebaseAuthManager(
            authenticator: Lazy<AuthAuthenticator>,
            usersRepository: Lazy<AuthRemoteRepository>,
            connectivityEnforcer: Lazy<ConnectivityManager.ConnectivityEnforcer>
    ): AuthManager = SherlockAuthManager(authenticator, usersRepository, connectivityEnforcer)
}

@Module(includes = [AuthFirebaseAuthenticatorModule::class])
internal object FirebaseAuthEnforcerModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideFirebaseAuthEnforcer(
            connectivityEnforcer: Lazy<ConnectivityManager.ConnectivityEnforcer>,
            authenticator: Lazy<AuthAuthenticator>
    ): AuthManager.AuthEnforcer = SherlockAuthManager.SherlockAuthEnforcer(connectivityEnforcer, authenticator)
}

@Module(includes = [FirebaseAuthModule::class])
internal object AuthFirebaseAuthenticatorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideAuthFirebaseAuthenticator(
            auth: Lazy<FirebaseAuth>,
            connectivityEnforcer: Lazy<ConnectivityManager.ConnectivityEnforcer>
    ): AuthAuthenticator = AuthFirebaseAuthenticator(auth, connectivityEnforcer)
}

@Module(includes = [
    FirebaseFirestoreModule::class,
    AuthFirebaseStorageImageRepositoryModule::class,
    FirebaseAuthEnforcerModule::class
])
internal object AuthFirebaseFirestoreRemoteRepositoryModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideAuthFirebaseFirestoreRemoteRepository(
            @AuthFirebaseFirestoreQualifier db: Lazy<FirebaseFirestore>,
            authImageRepository: Lazy<AuthImageRepository>,
            connectivityEnforcer: Lazy<ConnectivityManager.ConnectivityEnforcer>,
            authEnforcer: Lazy<AuthManager.AuthEnforcer>
    ): AuthRemoteRepository = AuthFirebaseFirestoreRemoteRepository(
            db,
            authImageRepository,
            connectivityEnforcer,
            authEnforcer
    )
}

@Module(includes = [FirebaseStorageModule::class])
internal object AuthFirebaseStorageImageRepositoryModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideAuthFirebaseStorageImageRepository(
            @AuthFirebaseStorageQualifier storage: Lazy<FirebaseStorage>
    ): AuthImageRepository = AuthFirebaseStorageImageRepository(storage)
}
