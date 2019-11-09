package inc.ahmedmourad.sherlock.dagger.modules.factories

import android.content.Intent
import inc.ahmedmourad.sherlock.model.AppPublishedChild
import inc.ahmedmourad.sherlock.services.SherlockService

internal typealias SherlockServiceIntentFactory = (AppPublishedChild) -> Intent

internal fun sherlockServiceIntentFactory(child: AppPublishedChild): Intent {
    return SherlockService.createIntent(child)
}
