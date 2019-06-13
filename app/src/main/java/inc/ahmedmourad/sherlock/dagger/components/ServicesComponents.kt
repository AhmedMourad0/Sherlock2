package inc.ahmedmourad.sherlock.dagger.components

import dagger.Subcomponent
import inc.ahmedmourad.sherlock.dagger.modules.domain.AddChildInteractorModule
import inc.ahmedmourad.sherlock.services.SherlockIntentService

@Subcomponent(modules = [AddChildInteractorModule::class])
interface SherlockIntentServiceComponent {
    fun inject(service: SherlockIntentService)
}
