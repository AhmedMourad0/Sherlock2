package inc.ahmedmourad.sherlock.dagger.components

import dagger.Subcomponent
import inc.ahmedmourad.sherlock.dagger.modules.FindChildrenControllerModule
import inc.ahmedmourad.sherlock.view.controllers.*

@Subcomponent(modules = [FindChildrenControllerModule::class])
internal interface HomeComponent {
    fun inject(controller: HomeController)
}

@Subcomponent
internal interface AddChildComponent {
    fun inject(controller: AddChildController)
}

@Subcomponent
internal interface DisplayChildComponent {
    fun inject(controller: DisplayChildController)
}

@Subcomponent
internal interface FindChildrenComponent {
    fun inject(controller: FindChildrenController)
}

@Subcomponent
internal interface SearchResultsComponent {
    fun inject(controller: SearchResultsController)
}
