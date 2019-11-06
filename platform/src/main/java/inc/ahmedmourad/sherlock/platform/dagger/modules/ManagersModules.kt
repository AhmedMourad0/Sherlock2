package inc.ahmedmourad.sherlock.platform.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.domain.platform.ConnectivityManager
import inc.ahmedmourad.sherlock.domain.platform.DateManager
import inc.ahmedmourad.sherlock.domain.platform.LocationManager
import inc.ahmedmourad.sherlock.domain.platform.TextManager
import inc.ahmedmourad.sherlock.platform.managers.AndroidConnectivityManager
import inc.ahmedmourad.sherlock.platform.managers.AndroidDateManager
import inc.ahmedmourad.sherlock.platform.managers.AndroidLocationManager
import inc.ahmedmourad.sherlock.platform.managers.AndroidTextManager

@Module
internal object AndroidDateManagerModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideAndroidDateManager(): DateManager = AndroidDateManager()
}

@Module
internal object AndroidLocationManagerModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideAndroidLocationManager(): LocationManager = AndroidLocationManager()
}

@Module
internal object AndroidTextManagerModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideAndroidTextManager(): TextManager = AndroidTextManager()
}

@Module
internal object AndroidConnectivityManagerModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideAndroidConnectivityManager(): ConnectivityManager = AndroidConnectivityManager()
}

@Module
internal object AndroidConnectivityEnforcerModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideAndroidConnectivityEnforcer(): ConnectivityManager.ConnectivityEnforcer = AndroidConnectivityManager.AndroidConnectivityEnforcer()
}
