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
    AuthAuthenticatorModule::class,
    AuthRemoteRepositoryModule::class,
    AuthEnforcerModule::class
])
internal object AuthManagerModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideAuthManager(
            authenticator: Lazy<AuthAuthenticator>,
            usersRepository: Lazy<AuthRemoteRepository>,
            connectivityEnforcer: Lazy<ConnectivityManager.ConnectivityEnforcer>
    ): AuthManager = SherlockAuthManager(authenticator, usersRepository, connectivityEnforcer)
}

@Module(includes = [AuthAuthenticatorModule::class])
internal object AuthEnforcerModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideAuthEnforcer(
            connectivityEnforcer: Lazy<ConnectivityManager.ConnectivityEnforcer>,
            authenticator: Lazy<AuthAuthenticator>
    ): AuthManager.AuthEnforcer = SherlockAuthManager.SherlockAuthEnforcer(connectivityEnforcer, authenticator)
}

@Module(includes = [FirebaseAuthModule::class])
internal object AuthAuthenticatorModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideAuthAuthenticator(
            auth: Lazy<FirebaseAuth>,
            connectivityEnforcer: Lazy<ConnectivityManager.ConnectivityEnforcer>
    ): AuthAuthenticator = AuthFirebaseAuthenticator(auth, connectivityEnforcer)
}

@Module(includes = [
    FirebaseFirestoreModule::class,
    AuthImageRepositoryModule::class,
    AuthEnforcerModule::class
])
internal object AuthRemoteRepositoryModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideAuthRemoteRepository(
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
internal object AuthImageRepositoryModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideAuthImageRepository(
            @AuthFirebaseStorageQualifier storage: Lazy<FirebaseStorage>
    ): AuthImageRepository = AuthFirebaseStorageImageRepository(storage)
}
