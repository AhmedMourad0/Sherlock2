package inc.ahmedmourad.sherlock.dagger.modules.app.factories

import android.content.Intent
import inc.ahmedmourad.sherlock.model.AppPictureChild
import inc.ahmedmourad.sherlock.services.SherlockIntentService

interface SherlockIntentServiceAbstractFactory {
    fun createIntent(child: AppPictureChild): Intent
}

class SherlockIntentServiceFactory : SherlockIntentServiceAbstractFactory {
    override fun createIntent(child: AppPictureChild): Intent {
        return SherlockIntentService.createIntent(child)
    }
}
