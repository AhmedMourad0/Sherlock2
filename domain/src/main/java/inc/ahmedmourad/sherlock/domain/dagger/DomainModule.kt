package inc.ahmedmourad.sherlock.domain.dagger

import dagger.Module
import inc.ahmedmourad.sherlock.domain.dagger.modules.*

@Module(includes = [
    FilterModule::class,
    BusModule::class,
    AddChildInteractorModule::class,
    FindChildrenInteractorModule::class,
    FindChildInteractorModule::class,
    FindLastSearchResultsInteractorModule::class,
    ObserveInternetConnectivityInteractorModule::class,
    CheckInternetConnectivityInteractorModule::class,
    ObservePublishingStateInteractorModule::class,
    CheckPublishingStateInteractorModule::class
])
object DomainModule
