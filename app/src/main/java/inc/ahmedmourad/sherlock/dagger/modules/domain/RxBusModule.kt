package inc.ahmedmourad.sherlock.dagger.modules.domain

import dagger.Module
import dagger.Provides
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.bus.RxBus
import javax.inject.Singleton

@Module
class RxBusModule {
    @Provides
    @Singleton
    fun provideRxBus(): Bus = RxBus()
}
