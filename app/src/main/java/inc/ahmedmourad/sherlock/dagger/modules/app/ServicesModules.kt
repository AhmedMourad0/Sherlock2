package inc.ahmedmourad.sherlock.dagger.modules.app

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.SherlockIntentServiceAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.SherlockIntentServiceFactory

@Module
class SherlockIntentServiceModule {
    @Provides
    @Reusable
    fun provideSherlockIntentService(): SherlockIntentServiceAbstractFactory = SherlockIntentServiceFactory()
}
