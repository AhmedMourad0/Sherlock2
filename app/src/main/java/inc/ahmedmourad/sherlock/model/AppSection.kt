package inc.ahmedmourad.sherlock.model

import androidx.annotation.DrawableRes
import com.bluelinelabs.conductor.Controller
import dagger.Lazy

data class AppSection(val name: String, @DrawableRes val imageDrawable: Int, val controller: Lazy<out Controller>?)
