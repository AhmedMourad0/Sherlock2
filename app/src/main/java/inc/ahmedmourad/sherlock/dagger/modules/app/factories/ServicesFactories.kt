package inc.ahmedmourad.sherlock.dagger.modules.app.factories

import android.content.Intent
import inc.ahmedmourad.sherlock.model.AppPictureChild
import inc.ahmedmourad.sherlock.services.SherlockIntentService

interface SherlockIntentServiceAbstractFactory {
    fun create(child: AppPictureChild): Intent
}

class SherlockIntentServiceFactory : SherlockIntentServiceAbstractFactory {
    override fun create(child: AppPictureChild): Intent {
        return SherlockIntentService.create(child)
    }
}
