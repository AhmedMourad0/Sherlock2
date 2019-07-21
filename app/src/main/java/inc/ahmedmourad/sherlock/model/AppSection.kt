package inc.ahmedmourad.sherlock.model

import androidx.annotation.DrawableRes
import dagger.Lazy
import inc.ahmedmourad.sherlock.view.model.TaggedController

data class AppSection(val name: String, @DrawableRes val imageDrawable: Int, val controller: Lazy<out TaggedController>?)
