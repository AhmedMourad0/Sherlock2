package inc.ahmedmourad.sherlock.dagger.components

import dagger.Subcomponent
import inc.ahmedmourad.sherlock.widget.AppWidget
import inc.ahmedmourad.sherlock.widget.adapter.ResultsRemoteViewsFactory
import inc.ahmedmourad.sherlock.widget.adapter.ResultsRemoteViewsService

@Subcomponent
internal interface ResultsRemoteViewsFactoryComponent {
    fun inject(factory: ResultsRemoteViewsFactory)
}

@Subcomponent
internal interface ResultsRemoteViewsServiceComponent {
    fun inject(service: ResultsRemoteViewsService)
}

@Subcomponent
internal interface AppWidgetComponent {
    fun inject(widget: AppWidget)
}
