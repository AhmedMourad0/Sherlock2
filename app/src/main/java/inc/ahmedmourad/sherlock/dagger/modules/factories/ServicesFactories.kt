package inc.ahmedmourad.sherlock.dagger.modules.factories

import android.content.Intent
import inc.ahmedmourad.sherlock.model.AppPublishedChild
import inc.ahmedmourad.sherlock.services.SherlockService

internal interface SherlockServiceAbstractFactory {
    fun createIntent(child: AppPublishedChild): Intent
}

internal class SherlockServiceFactory : SherlockServiceAbstractFactory {
    override fun createIntent(child: AppPublishedChild): Intent {
        return SherlockService.createIntent(child)
    }
}
