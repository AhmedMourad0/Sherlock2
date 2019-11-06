package inc.ahmedmourad.sherlock.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import dagger.Lazy

import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.factories.ResultsRemoteViewsServiceAbstractFactory
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.FindLastSearchResultsInteractorAbstractFactory
import inc.ahmedmourad.sherlock.mapper.toAppSimpleChild
import inc.ahmedmourad.sherlock.utils.DisposablesSparseArray
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

internal class AppWidget : AppWidgetProvider() {

    @Inject
    lateinit var interactor: FindLastSearchResultsInteractorAbstractFactory

    @Inject
    lateinit var bus: Lazy<Bus>

    @Inject
    lateinit var resultsRemoteViewsServiceFactory: Lazy<ResultsRemoteViewsServiceAbstractFactory>

    private val disposables = DisposablesSparseArray()

    init {
        SherlockComponent.Widget.appWidgetComponent.get().inject(this)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds)
            disposables.put(appWidgetId, updateAppWidget(context, appWidgetManager, appWidgetId))
    }

    /**
     * Used to update the ui of a certain widget
     *
     * @param context          The Context in which this receiver is running.
     * @param appWidgetManager A AppWidgetManager object you can call AppWidgetManager.updateAppWidget on.
     * @param appWidgetId      The appWidgetIds for which an update is needed.
     */
    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int): Disposable {
        return interactor.create()
                .execute()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Flowable.fromIterable(it)
                            .map { (child, score) ->
                                child.toAppSimpleChild() to score
                            }.toList()
                            .toFlowable()
                }.subscribe({

                    val views = RemoteViews(context.packageName, R.layout.app_widget)

                    views.setImageViewResource(R.id.widget_icon, R.drawable.ic_sherlock)

                    views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view)

                    views.setRemoteAdapter(R.id.widget_list_view,
                            resultsRemoteViewsServiceFactory.get().createIntent(appWidgetId, it)
                    )

                    appWidgetManager.updateAppWidget(appWidgetId, views)

                }, Timber::e) //TODO: show retry view
    }

    private fun retry(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        disposables.dispose(appWidgetIds)
        super.onDeleted(context, appWidgetIds)
    }

    override fun onDisabled(context: Context) {
        disposables.dispose()
        super.onDisabled(context)
    }
}
