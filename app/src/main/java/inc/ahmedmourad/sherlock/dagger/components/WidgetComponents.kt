package inc.ahmedmourad.sherlock.dagger.components

import dagger.Subcomponent
import inc.ahmedmourad.sherlock.widget.AppWidget
import inc.ahmedmourad.sherlock.widget.adapter.ResultsRemoteViewsFactory
import inc.ahmedmourad.sherlock.widget.adapter.ResultsRemoteViewsService

@Subcomponent
interface ResultsRemoteViewsFactoryComponent {
    fun inject(factory: ResultsRemoteViewsFactory)
}

@Subcomponent
interface ResultsRemoteViewsServiceComponent {
    fun inject(service: ResultsRemoteViewsService)
}

@Subcomponent
interface AppWidgetComponent {
    fun inject(widget: AppWidget)
}
