package inc.ahmedmourad.sherlock.dagger.components

import dagger.Subcomponent
import inc.ahmedmourad.sherlock.dagger.modules.app.AddChildControllerModule
import inc.ahmedmourad.sherlock.dagger.modules.app.FindChildrenControllerModule
import inc.ahmedmourad.sherlock.view.controllers.*

@Subcomponent(modules = [AddChildControllerModule::class,
    FindChildrenControllerModule::class
])
interface HomeComponent {
    fun inject(controller: HomeController)
}

@Subcomponent
interface AddChildComponent {
    fun inject(controller: AddChildController)
}

@Subcomponent
interface DisplayChildComponent {
    fun inject(controller: DisplayChildController)
}

@Subcomponent
interface FindChildrenComponent {
    fun inject(controller: FindChildrenController)
}

@Subcomponent
interface SearchResultsComponent {
    fun inject(controller: SearchResultsController)
}
