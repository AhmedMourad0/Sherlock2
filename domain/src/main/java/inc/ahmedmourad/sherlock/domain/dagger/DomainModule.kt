package inc.ahmedmourad.sherlock.domain.dagger

import dagger.Module
import inc.ahmedmourad.sherlock.domain.dagger.modules.*

@Module(includes = [
    ResultsFilterModule::class,
    RxBusModule::class,
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
