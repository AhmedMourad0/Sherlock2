package inc.ahmedmourad.sherlock.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.factories.AddChildControllerIntentFactory
import inc.ahmedmourad.sherlock.dagger.modules.factories.DisplayChildControllerIntentFactory
import inc.ahmedmourad.sherlock.dagger.modules.qualifiers.AddChildControllerIntentQualifier
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.interactors.AddChildInteractor
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.domain.model.disposable
import inc.ahmedmourad.sherlock.mapper.toAppChild
import inc.ahmedmourad.sherlock.model.AppPublishedChild
import inc.ahmedmourad.sherlock.model.AppSimpleRetrievedChild
import inc.ahmedmourad.sherlock.utils.backgroundContextChannelId
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import splitties.init.appCtx
import timber.log.Timber
import javax.inject.Inject

internal class SherlockService : Service() {

    @Inject
    lateinit var bus: Bus

    @Inject
    lateinit var addChildInteractor: AddChildInteractor

    @Inject
    @field:AddChildControllerIntentQualifier
    lateinit var addChildControllerFactory: AddChildControllerIntentFactory

    @Inject
    lateinit var displayChildControllerFactory: DisplayChildControllerIntentFactory

    private var addChildDisposable by disposable()

    override fun onCreate() {
        super.onCreate()
        SherlockComponent.Services.sherlockServiceComponent.get().inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        checkNotNull(intent)
        checkNotNull(intent.action)

        when (intent.action) {
            ACTION_PUBLISH_CHILD -> handleActionPublishFound(requireNotNull(intent.getParcelableExtra(EXTRA_CHILD)))
        }

        return START_REDELIVER_INTENT
    }

    private fun handleActionPublishFound(child: AppPublishedChild) {

        startForeground(NOTIFICATION_ID_PUBLISH_CHILD, createPublishingNotification(child))

        addChildDisposable = addChildInteractor(child.toDomainChild())
                .map { it.map(DomainRetrievedChild::toAppChild) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    Toast.makeText(applicationContext, getString(R.string.published_successfully), Toast.LENGTH_LONG).show()
                }.doOnError {
                    Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
                }.doFinally {
                    stopForeground(true)
                    stopSelf()
                }.subscribe({ childEither ->
                    childEither.fold(ifLeft = {
                        Timber.e(it)
                        showPublishingFailedNotification(it, child)
                    }, ifRight = {
                        this.showPublishedSuccessfullyNotification(it.simplify())
                    })
                }, {
                    Timber.e(it)
                    showPublishingFailedNotification(it, child)
                })
    }

    private fun createPublishingNotification(child: AppPublishedChild): Notification {

        val pendingIntent = addChildControllerFactory(child).let {
            PendingIntent.getActivity(applicationContext, REQUEST_CODE_PUBLISH_CHILD, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val contentText = if (child.name.first.isEmpty() && child.name.last.isEmpty())
            getString(R.string.publishing_child_data)
        else
            getString(R.string.publishing_child_data_with_name, "${child.name.first} ${child.name.last}".trim())

        return NotificationCompat.Builder(applicationContext, backgroundContextChannelId(applicationContext))
                .setContentTitle(getString(R.string.publishing))
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_sherlock)
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.publishing))
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                .build()
    }

    private fun showPublishedSuccessfullyNotification(child: AppSimpleRetrievedChild) {

        val pendingIntent = displayChildControllerFactory(child).let {
            PendingIntent.getActivity(applicationContext, REQUEST_CODE_PUBLISHED_SUCCESSFULLY, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val contentText = if (child.name.first.isEmpty() && child.name.last.isEmpty())
            getString(R.string.published_child_data_successfully)
        else
            getString(R.string.published_child_data_successfully_with_name, "${child.name.first} ${child.name.last}".trim())

        val notification = NotificationCompat.Builder(applicationContext, backgroundContextChannelId(applicationContext))
                .setContentTitle(getString(R.string.success))
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_sherlock)
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.published_successfully))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                .build()

        checkNotNull(ContextCompat.getSystemService(applicationContext, NotificationManager::class.java))
                .notify(NOTIFICATION_ID_PUBLISHED_SUCCESSFULLY, notification)
    }

    private fun showPublishingFailedNotification(throwable: Throwable, child: AppPublishedChild) {

        val pendingIntent = createIntent(child).let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PendingIntent.getForegroundService(applicationContext, REQUEST_CODE_PUBLISHING_FAILED, it, PendingIntent.FLAG_UPDATE_CURRENT)
            } else {
                PendingIntent.getService(applicationContext, REQUEST_CODE_PUBLISHING_FAILED, it, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        val contentTitle = if (child.name.first.isEmpty() && child.name.last.isEmpty())
            getString(R.string.publishing_failed)
        else
            getString(R.string.publishing_failed_with_name, "${child.name.first} ${child.name.last}".trim())

        val notification = NotificationCompat.Builder(applicationContext, backgroundContextChannelId(applicationContext))
                .setContentTitle(contentTitle)
                .setContentText(getString(R.string.click_to_retry_with_reason, throwable.localizedMessage))
                .setSmallIcon(R.drawable.ic_sherlock)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setTicker(getString(R.string.publishing_failed))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                .build()

        checkNotNull(ContextCompat.getSystemService(applicationContext, NotificationManager::class.java))
                .notify(NOTIFICATION_ID_PUBLISHING_FAILED, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        addChildDisposable?.dispose()
        SherlockComponent.Services.sherlockServiceComponent.release()
        super.onDestroy()
    }

    companion object {

        const val ACTION_PUBLISH_CHILD = "inc.ahmedmourad.sherlock.services.action.PUBLISH_CHILd"
        const val EXTRA_CHILD = "inc.ahmedmourad.sherlock.services.extra.CHILD"
        const val REQUEST_CODE_PUBLISH_CHILD = 4278
        const val REQUEST_CODE_PUBLISHED_SUCCESSFULLY = 5778
        const val REQUEST_CODE_PUBLISHING_FAILED = 2427
        const val NOTIFICATION_ID_PUBLISH_CHILD = 7542
        const val NOTIFICATION_ID_PUBLISHED_SUCCESSFULLY = 1427
        const val NOTIFICATION_ID_PUBLISHING_FAILED = 3675

        fun createIntent(child: AppPublishedChild) = Intent(appCtx, SherlockService::class.java).apply {
            action = ACTION_PUBLISH_CHILD
            putExtra(EXTRA_CHILD, child)
        }
    }
}
