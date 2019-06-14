package inc.ahmedmourad.sherlock.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import dagger.Lazy

import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.RemoteViewsServiceFactory
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.GetLastSearchResultsInteractorAbstractFactory
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.mapper.AppModelsMapper
import inc.ahmedmourad.sherlock.utils.DisposablesSparseArray
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class AppWidget : AppWidgetProvider() {

    @Inject
    lateinit var interactor: Lazy<GetLastSearchResultsInteractorAbstractFactory>

    @Inject
    lateinit var bus: Lazy<Bus>

    @Inject
    lateinit var resultsRemoteViewsServiceFactory: Lazy<RemoteViewsServiceFactory>

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
        return interactor.get()
                .create()
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    val views = RemoteViews(context.packageName, R.layout.app_widget)

                    views.setImageViewResource(R.id.widget_icon, R.drawable.ic_sherlock)

                    views.setEmptyView(R.id.widget_list_view, R.id.widget_empty)

                    views.setRemoteAdapter(R.id.widget_list_view,
                            resultsRemoteViewsServiceFactory.get().create(context,
                                    appWidgetId,
                                    it.map { (child, score) ->
                                        AppModelsMapper.toAppChild(child) to score
                                    }
                            )
                    )

                    appWidgetManager.updateAppWidget(appWidgetId, views)

                }, {
                    //TODO: try the retry or repeat operator, maybe in other places too
                    //TODO: message
                    bus.get().widget.retriableErrors.notify(Bus.RetriableError("", it) {
                        updateAppWidget(context, appWidgetManager, appWidgetId)
                    })
                })
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
