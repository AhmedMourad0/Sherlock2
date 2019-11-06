package inc.ahmedmourad.sherlock.dagger.modules.factories

import android.content.Context
import android.content.Intent
import android.widget.RemoteViewsService
import inc.ahmedmourad.sherlock.model.AppSimpleRetrievedChild
import inc.ahmedmourad.sherlock.widget.adapter.ResultsRemoteViewsFactory
import inc.ahmedmourad.sherlock.widget.adapter.ResultsRemoteViewsService

internal interface ResultsRemoteViewsServiceAbstractFactory {
    fun createIntent(appWidgetId: Int, results: List<Pair<AppSimpleRetrievedChild, Int>>): Intent
}

internal class ResultsRemoteViewsServiceFactory : ResultsRemoteViewsServiceAbstractFactory {
    override fun createIntent(appWidgetId: Int, results: List<Pair<AppSimpleRetrievedChild, Int>>): Intent {
        return ResultsRemoteViewsService.create(appWidgetId, results)
    }
}

internal interface ResultsRemoteViewsFactoryAbstractFactory {
    fun create(context: Context, results: List<Pair<AppSimpleRetrievedChild, Int>>): RemoteViewsService.RemoteViewsFactory
}

internal class ResultsRemoteViewsFactoryFactory : ResultsRemoteViewsFactoryAbstractFactory {
    override fun create(context: Context, results: List<Pair<AppSimpleRetrievedChild, Int>>): RemoteViewsService.RemoteViewsFactory {
        return ResultsRemoteViewsFactory(context, results)
    }
}
