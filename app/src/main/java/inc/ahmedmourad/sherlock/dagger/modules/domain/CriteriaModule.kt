package inc.ahmedmourad.sherlock.dagger.modules.domain

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.CriteriaFactory
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.LooseCriteriaFactory
import inc.ahmedmourad.sherlock.domain.device.LocationManager

@Module
class CriteriaModule {
    @Provides
    @Reusable
    fun provideLooseCriteria(locationManager: Lazy<LocationManager>): CriteriaFactory = LooseCriteriaFactory(locationManager)
}
