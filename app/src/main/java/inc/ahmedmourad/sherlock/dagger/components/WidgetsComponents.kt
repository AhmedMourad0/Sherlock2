package inc.ahmedmourad.sherlock.dagger.components

import dagger.Subcomponent
import inc.ahmedmourad.sherlock.dagger.modules.ResultsRemoteViewsFactoryModule
import inc.ahmedmourad.sherlock.dagger.modules.ResultsRemoteViewsServiceModule
import inc.ahmedmourad.sherlock.widget.AppWidget
import inc.ahmedmourad.sherlock.widget.adapter.ResultsRemoteViewsService

@Subcomponent(modules = [
    ResultsRemoteViewsFactoryModule::class
])
internal interface ResultsRemoteViewsServiceComponent {
    fun inject(service: ResultsRemoteViewsService)
}

@Subcomponent(modules = [
    ResultsRemoteViewsServiceModule::class
])
internal interface AppWidgetComponent {
    fun inject(widget: AppWidget)
}
