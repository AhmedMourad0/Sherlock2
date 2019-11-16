package inc.ahmedmourad.sherlock.dagger.modules.factories

import android.content.Context
import android.content.Intent
import android.widget.RemoteViewsService
import arrow.core.Tuple2
import inc.ahmedmourad.sherlock.model.AppSimpleRetrievedChild
import inc.ahmedmourad.sherlock.widget.adapter.ResultsRemoteViewsFactory
import inc.ahmedmourad.sherlock.widget.adapter.ResultsRemoteViewsService

internal typealias ResultsRemoteViewsServiceIntentFactory =
        (Int, List<Tuple2<AppSimpleRetrievedChild, Int>>) -> Intent

internal fun resultsRemoteViewsServiceIntentFactory(
        appWidgetId: Int,
        results: List<Tuple2<AppSimpleRetrievedChild, Int>>
): Intent {
    return ResultsRemoteViewsService.create(appWidgetId, results)
}

internal typealias ResultsRemoteViewsFactoryFactory =
        (Context, List<Tuple2<AppSimpleRetrievedChild, Int>>) -> RemoteViewsService.RemoteViewsFactory

internal fun resultsRemoteViewsFactoryFactory(
        context: Context,
        results: List<Tuple2<AppSimpleRetrievedChild, Int>>
): RemoteViewsService.RemoteViewsFactory {
    return ResultsRemoteViewsFactory(context, results)
}
