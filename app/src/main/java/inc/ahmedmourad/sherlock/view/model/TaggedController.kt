package inc.ahmedmourad.sherlock.view.model

import com.bluelinelabs.conductor.Controller

internal class TaggedController<out T : Controller>(val controller: T, val tag: String)
