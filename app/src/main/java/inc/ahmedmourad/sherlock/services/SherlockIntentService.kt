package inc.ahmedmourad.sherlock.services

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.Lazy
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.AddChildControllerAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.app.factories.DisplayChildControllerAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.AddChildInteractorAbstractFactory
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.mapper.AppModelsMapper
import inc.ahmedmourad.sherlock.model.AppPictureChild
import inc.ahmedmourad.sherlock.utils.backgroundContextChannelId
import io.reactivex.disposables.Disposable
import splitties.init.appCtx
import javax.inject.Inject

class SherlockIntentService : IntentService("SherlockIntentService") {

    @Inject
    lateinit var addChildInteractor: AddChildInteractorAbstractFactory

    @Inject
    lateinit var addChildControllerFactory: AddChildControllerAbstractFactory

    @Inject
    lateinit var displayChildControllerFactory: DisplayChildControllerAbstractFactory

    @Inject
    lateinit var bus: Lazy<Bus>

    private var disposable: Disposable? = null

    override fun onCreate() {
        super.onCreate()
        SherlockComponent.Services.sherlockServiceComponent.get().inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {

        if (intent == null || intent.action == null) {
            bus.get().publishingState.notify(Bus.PublishingState.FAILURE)
            return
        }

        when (intent.action) {
            ACTION_PUBLISH_CHILD -> handleActionPublishFound(intent.getParcelableExtra(EXTRA_CHILD))
        }
    }

    private fun handleActionPublishFound(child: AppPictureChild) {

        startForeground(NOTIFICATION_ID_PUBLISH_CHILD, createPublishingNotification(child))

        disposable = addChildInteractor.create(AppModelsMapper.toDomainPictureChild(child))
                .execute()
                .doOnSuccess { disposable?.dispose() }
                .doOnError { disposable?.dispose() }
                .subscribe({
                    showPublishedSuccessfullyNotification(child)
                    Toast.makeText(this, getString(R.string.published_successfully), Toast.LENGTH_LONG).show()
                }, {
                    it.printStackTrace()
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                    showPublishingFailedNotification(it, child)
                })
    }

    private fun createPublishingNotification(child: AppPictureChild): Notification {

        val pendingIntent = addChildControllerFactory.createIntent(child).let {
            PendingIntent.getActivity(this, REQUEST_CODE_PUBLISH_CHILD, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val contentText = if (child.name.first.isEmpty() && child.name.last.isEmpty())
            getString(R.string.publishing_child_data)
        else
            getString(R.string.publishing_child_data_with_name, "${child.name.first} ${child.name.last}".trim())

        return NotificationCompat.Builder(this, backgroundContextChannelId(this))
                .setContentTitle(getString(R.string.publishing))
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_sherlock)
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.publishing))
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .build()
    }

    private fun showPublishedSuccessfullyNotification(child: AppPictureChild) {

        val pendingIntent = displayChildControllerFactory.createIntent(child.id).let {
            PendingIntent.getActivity(this, REQUEST_CODE_PUBLISHED_SUCCESSFULLY, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val contentText = if (child.name.first.isEmpty() && child.name.last.isEmpty())
            getString(R.string.published_child_data_successfully)
        else
            getString(R.string.published_child_data_successfully_with_name, "${child.name.first} ${child.name.last}".trim())

        val notification = NotificationCompat.Builder(this, backgroundContextChannelId(this))
                .setContentTitle(getString(R.string.published_successfully))
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_sherlock)
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.published_successfully))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .build()

        checkNotNull(ContextCompat.getSystemService(this, NotificationManager::class.java)) {
            "NotificationManager is null!"
        }.notify(NOTIFICATION_ID_PUBLISHED_SUCCESSFULLY, notification)
    }

    private fun showPublishingFailedNotification(throwable: Throwable, child: AppPictureChild) {

        val pendingIntent = createIntent(child).let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PendingIntent.getForegroundService(this, REQUEST_CODE_PUBLISHING_FAILED, it, PendingIntent.FLAG_UPDATE_CURRENT)
            } else {
                PendingIntent.getService(this, REQUEST_CODE_PUBLISHING_FAILED, it, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        val contentTitle = if (child.name.first.isEmpty() && child.name.last.isEmpty())
            getString(R.string.publishing_failed)
        else
            getString(R.string.publishing_failed_with_name, "${child.name.first} ${child.name.last}".trim())

        val notification = NotificationCompat.Builder(this, backgroundContextChannelId(this))
                .setContentTitle(contentTitle)
                .setContentText(getString(R.string.click_to_retry_with_reason, throwable.localizedMessage))
                .setSmallIcon(R.drawable.ic_sherlock)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setTicker(getString(R.string.publishing_failed))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .build()

        checkNotNull(ContextCompat.getSystemService(this, NotificationManager::class.java)) {
            "NotificationManager is null!"
        }.notify(NOTIFICATION_ID_PUBLISHING_FAILED, notification)
    }

    override fun onDestroy() {
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

        fun createIntent(child: AppPictureChild) = Intent(appCtx, SherlockIntentService::class.java).apply {
            action = ACTION_PUBLISH_CHILD
            putExtra(EXTRA_CHILD, child)
        }
    }
}
