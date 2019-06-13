package inc.ahmedmourad.sherlock.dagger.modules.device

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.device.AndroidDateManager
import inc.ahmedmourad.sherlock.device.AndroidLocationManager
import inc.ahmedmourad.sherlock.device.AndroidTextManager
import inc.ahmedmourad.sherlock.domain.device.DateManager
import inc.ahmedmourad.sherlock.domain.device.LocationManager
import inc.ahmedmourad.sherlock.domain.device.TextManager

@Module
class AndroidDateManagerModule {
    @Provides
    @Reusable
    fun provideAndroidDateManager(): DateManager = AndroidDateManager()
}

@Module
class AndroidLocationManagerModule {
    @Provides
    @Reusable
    fun provideAndroidLocationManager(): LocationManager = AndroidLocationManager()
}

@Module
class AndroidTextManagerModule {
    @Provides
    @Reusable
    fun provideAndroidTextManager(): TextManager = AndroidTextManager()
}
