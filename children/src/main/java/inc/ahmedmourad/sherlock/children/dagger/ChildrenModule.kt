package inc.ahmedmourad.sherlock.children.dagger

import dagger.Module
import inc.ahmedmourad.sherlock.children.dagger.modules.SherlockChildrenRepositoryModule

@Module(includes = [SherlockChildrenRepositoryModule::class])
object ChildrenModule
