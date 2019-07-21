package inc.ahmedmourad.sherlock.dagger.modules.app.factories

import android.content.Context
import android.content.Intent
import android.widget.RemoteViewsService
import inc.ahmedmourad.sherlock.model.AppUrlChild
import inc.ahmedmourad.sherlock.widget.adapter.ResultsRemoteViewsFactory
import inc.ahmedmourad.sherlock.widget.adapter.ResultsRemoteViewsService

interface ResultsRemoteViewsServiceAbstractFactory {
    fun createIntent(appWidgetId: Int, results: List<Pair<AppUrlChild, Int>>): Intent
}

class ResultsRemoteViewsServiceFactory : ResultsRemoteViewsServiceAbstractFactory {
    override fun createIntent(appWidgetId: Int, results: List<Pair<AppUrlChild, Int>>): Intent {
        return ResultsRemoteViewsService.create(appWidgetId, results)
    }
}

interface ResultsRemoteViewsFactoryAbstractFactory {
    fun create(context: Context, results: List<Pair<AppUrlChild, Int>>): RemoteViewsService.RemoteViewsFactory
}

class ResultsRemoteViewsFactoryFactory : ResultsRemoteViewsFactoryAbstractFactory {
    override fun create(context: Context, results: List<Pair<AppUrlChild, Int>>): RemoteViewsService.RemoteViewsFactory {
        return ResultsRemoteViewsFactory(context, results)
    }
}
