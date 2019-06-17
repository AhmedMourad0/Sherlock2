package inc.ahmedmourad.sherlock.dagger.modules.app

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.utils.Formatter
import inc.ahmedmourad.sherlock.utils.TextFormatter

@Module
class TextFormatterModule {
    @Provides
    @Reusable
    fun provideTextFormatter(): Formatter<String> = TextFormatter()
}
