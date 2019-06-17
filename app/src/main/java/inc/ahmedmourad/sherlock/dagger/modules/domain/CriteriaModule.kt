package inc.ahmedmourad.sherlock.dagger.modules.domain

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.CriteriaAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.LooseCriteriaFactory
import inc.ahmedmourad.sherlock.domain.device.LocationManager

@Module
class CriteriaModule {
    @Provides
    @Reusable
    fun provideLooseCriteria(locationManager: Lazy<LocationManager>): CriteriaAbstractFactory = LooseCriteriaFactory(locationManager)
}
