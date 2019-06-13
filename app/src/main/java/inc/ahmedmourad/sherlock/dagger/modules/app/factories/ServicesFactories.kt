package inc.ahmedmourad.sherlock.dagger.modules.app.factories

import android.content.Intent
import inc.ahmedmourad.sherlock.model.AppPictureChild
import inc.ahmedmourad.sherlock.services.SherlockIntentService
import org.parceler.Parcels
import splitties.init.appCtx

interface IntentServiceFactory {
    fun create(child: AppPictureChild): Intent
}

class SherlockIntentServiceFactory : IntentServiceFactory {
    override fun create(child: AppPictureChild): Intent {
        return Intent(appCtx, SherlockIntentService::class.java).apply {
            action = SherlockIntentService.ACTION_PUBLISH_FOUND
            putExtra(SherlockIntentService.EXTRA_FOUND, Parcels.wrap(child))
        }
    }
}
