package inc.ahmedmourad.sherlock.view.model

import androidx.annotation.DrawableRes
import com.bluelinelabs.conductor.Controller
import dagger.Lazy

internal data class AppSection(
        val name: String,
        @DrawableRes val imageDrawable: Int,
        val controller: Lazy<out TaggedController<Controller>>?
)
