package inc.ahmedmourad.sherlock.dagger.modules.device

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.domain.framework.DateManager
import inc.ahmedmourad.sherlock.domain.framework.LocationManager
import inc.ahmedmourad.sherlock.framework.AndroidDateManager
import inc.ahmedmourad.sherlock.framework.AndroidLocationManager

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
