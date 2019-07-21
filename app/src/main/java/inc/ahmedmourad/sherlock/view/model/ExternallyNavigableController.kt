package inc.ahmedmourad.sherlock.view.model

import android.content.Intent
import com.bluelinelabs.conductor.Router

interface ExternallyNavigableController {
    fun isDestination(destinationId: Int): Boolean
    fun navigate(router: Router, intent: Intent)
}
