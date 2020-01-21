package inc.ahmedmourad.sherlock.dagger.modules.factories

import android.content.Context
import android.content.Intent
import android.widget.RemoteViewsService
import arrow.core.Tuple2
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.platform.DateManager
import inc.ahmedmourad.sherlock.model.children.AppSimpleRetrievedChild
import inc.ahmedmourad.sherlock.utils.formatter.Formatter
import inc.ahmedmourad.sherlock.widget.adapter.ResultsRemoteViewsFactory
import inc.ahmedmourad.sherlock.widget.adapter.ResultsRemoteViewsService

internal typealias ResultsRemoteViewsServiceIntentFactory =
        (@JvmSuppressWildcards Int, @JvmSuppressWildcards List<Tuple2<AppSimpleRetrievedChild, Int>>)
        -> @JvmSuppressWildcards Intent

internal fun resultsRemoteViewsServiceIntentFactory(
        appWidgetId: Int,
        results: List<Tuple2<AppSimpleRetrievedChild, Int>>
): Intent {
    return ResultsRemoteViewsService.create(appWidgetId, results)
}

internal typealias ResultsRemoteViewsFactoryFactory =
        (@JvmSuppressWildcards Context, @JvmSuppressWildcards List<Tuple2<AppSimpleRetrievedChild, Int>>)
        -> @JvmSuppressWildcards RemoteViewsService.RemoteViewsFactory

internal fun resultsRemoteViewsFactoryFactory(
        formatter: Lazy<Formatter>,
        dateManager: Lazy<DateManager>,
        context: Context,
        results: List<Tuple2<AppSimpleRetrievedChild, Int>>
): RemoteViewsService.RemoteViewsFactory {
    return ResultsRemoteViewsFactory(
            context,
            results,
            formatter,
            dateManager
    )
}
