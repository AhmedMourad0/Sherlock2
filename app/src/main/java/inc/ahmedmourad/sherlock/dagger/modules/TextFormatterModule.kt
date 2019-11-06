package inc.ahmedmourad.sherlock.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import inc.ahmedmourad.sherlock.domain.platform.TextManager
import inc.ahmedmourad.sherlock.formatter.Formatter
import inc.ahmedmourad.sherlock.formatter.TextFormatter

@Module
internal object TextFormatterModule {
    @Provides
    @Reusable
    @JvmStatic
    fun provideTextFormatter(textManager: TextManager): Formatter = TextFormatter(textManager)
}
