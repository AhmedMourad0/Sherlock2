package inc.ahmedmourad.sherlock.platform.dagger

import dagger.Module
import inc.ahmedmourad.sherlock.platform.dagger.modules.*

@Module(includes = [
    DateManagerModule::class,
    LocationManagerModule::class,
    TextManagerModule::class,
    ConnectivityManagerModule::class,
    ConnectivityEnforcerModule::class
])
object PlatformModule
