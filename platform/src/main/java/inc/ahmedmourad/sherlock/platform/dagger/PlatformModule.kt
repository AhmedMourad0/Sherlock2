package inc.ahmedmourad.sherlock.platform.dagger

import dagger.Module
import inc.ahmedmourad.sherlock.platform.dagger.modules.*

@Module(includes = [
    AndroidDateManagerModule::class,
    AndroidLocationManagerModule::class,
    AndroidTextManagerModule::class,
    AndroidConnectivityManagerModule::class,
    AndroidConnectivityEnforcerModule::class
])
object PlatformModule
