package inc.ahmedmourad.sherlock.dagger.modules.domain

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.bus.RxBus
import inc.ahmedmourad.sherlock.domain.device.TextManager
import javax.inject.Singleton

@Module
class RxBusModule {
    @Provides
    @Singleton
    fun provideRxBus(): Bus = RxBus()
}

@Module
class PublishingStateProviderModule {
    @Provides
    @Reusable
    fun providePublishingStateProvider(textManager: Lazy<TextManager>): Bus.PublishingState.Provider = Bus.PublishingState.TextManagerProvider(textManager)
}
