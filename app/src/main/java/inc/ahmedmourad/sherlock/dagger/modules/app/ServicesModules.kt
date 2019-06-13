package inc.ahmedmourad.sherlock.dagger.modules.app

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.IntentServiceFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.SherlockIntentServiceFactory

@Module
class SherlockIntentServiceModule {
    @Provides
    @Reusable
    fun provideSherlockIntentService(): IntentServiceFactory = SherlockIntentServiceFactory()
}
