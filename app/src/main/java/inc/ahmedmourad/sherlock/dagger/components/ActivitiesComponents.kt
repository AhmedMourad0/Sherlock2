package inc.ahmedmourad.sherlock.dagger.components

import dagger.Subcomponent
import inc.ahmedmourad.sherlock.dagger.modules.HomeControllerModule
import inc.ahmedmourad.sherlock.view.activity.MainActivity

@Subcomponent(modules = [HomeControllerModule::class])
internal interface MainActivityComponent {
    fun inject(activity: MainActivity)
}
