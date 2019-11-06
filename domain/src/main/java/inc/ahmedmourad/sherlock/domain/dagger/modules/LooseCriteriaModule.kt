package inc.ahmedmourad.sherlock.domain.dagger.modules

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.CriteriaAbstractFactory
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.LooseCriteriaFactory
import inc.ahmedmourad.sherlock.domain.platform.LocationManager

@Module
internal object LooseCriteriaModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideLooseCriteria(locationManager: Lazy<LocationManager>): CriteriaAbstractFactory = LooseCriteriaFactory(locationManager)
}
