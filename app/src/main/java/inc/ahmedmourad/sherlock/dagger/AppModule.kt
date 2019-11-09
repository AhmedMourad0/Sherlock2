package inc.ahmedmourad.sherlock.dagger

import dagger.Module
import inc.ahmedmourad.sherlock.dagger.modules.*

//TODO: hmmmmmmmmmmm
@Module(includes = [
    MainActivityModules::class,
    TextFormatterModule::class,
    PlacePickerModule::class,
    ImagePickerModule::class,
    ResultsRecyclerAdapterModule::class,
    SectionsRecyclerAdapterModule::class,
    SherlockServiceModule::class,
    AddChildControllerModule::class,
    DisplayChildControllerModule::class,
    SearchResultsControllerModule::class,
    ResultsRemoteViewsServiceModule::class,
    ResultsRemoteViewsFactoryModule::class,
    MainActivityViewModelModule::class,
    AddChildViewModelModule::class,
    FindChildrenViewModelModule::class,
    SearchResultsViewModelModule::class,
    DisplayChildViewModelModule::class
])
internal object AppModule
