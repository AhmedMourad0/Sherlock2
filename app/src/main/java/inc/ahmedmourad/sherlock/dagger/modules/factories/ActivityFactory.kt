package inc.ahmedmourad.sherlock.dagger.modules.factories

import android.content.Intent
import inc.ahmedmourad.sherlock.view.activity.MainActivity

internal interface MainActivityAbstractFactory {
    fun createIntent(destinationId: Int): Intent
}

internal class MainActivityFactory : MainActivityAbstractFactory {
    override fun createIntent(destinationId: Int): Intent = MainActivity.createIntent(destinationId)
}
