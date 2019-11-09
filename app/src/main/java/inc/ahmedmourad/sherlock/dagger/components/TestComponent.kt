package inc.ahmedmourad.sherlock.dagger.components

import dagger.Subcomponent
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.platform.DateManager
import inc.ahmedmourad.sherlock.utils.formatter.Formatter

@Subcomponent
internal interface TestComponent {
    fun bus(): Bus
    fun childrenRepository(): ChildrenRepository
    fun formatter(): Formatter
    fun dateManager(): DateManager
}
