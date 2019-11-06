package inc.ahmedmourad.sherlock.auth.dagger

import dagger.Module
import inc.ahmedmourad.sherlock.auth.dagger.modules.FirebaseAuthManagerModule

@Module(includes = [FirebaseAuthManagerModule::class])
object AuthModule
