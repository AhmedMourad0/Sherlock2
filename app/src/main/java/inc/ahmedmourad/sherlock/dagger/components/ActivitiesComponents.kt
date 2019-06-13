package inc.ahmedmourad.sherlock.dagger.components

import dagger.Subcomponent
import inc.ahmedmourad.sherlock.dagger.modules.app.HomeControllerModule
import inc.ahmedmourad.sherlock.view.activity.MainActivity

@Subcomponent(modules = [HomeControllerModule::class])
interface MainActivityComponent {
    fun inject(activity: MainActivity)
}
