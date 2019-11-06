package inc.ahmedmourad.sherlock.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.factories.SherlockServiceAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.SherlockServiceFactory

@Module
internal object SherlockServiceModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideSherlockService(): SherlockServiceAbstractFactory = SherlockServiceFactory()
}
