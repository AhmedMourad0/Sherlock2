package inc.ahmedmourad.sherlock.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.factories.MainActivityAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.MainActivityFactory

@Module
internal object MainActivityModules {
    @Provides
    @Reusable
    @JvmStatic
    fun provideMainActivity(): MainActivityAbstractFactory = MainActivityFactory()
}
