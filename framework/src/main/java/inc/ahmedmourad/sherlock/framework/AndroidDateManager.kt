package inc.ahmedmourad.sherlock.framework

import android.text.format.DateUtils
import inc.ahmedmourad.sherlock.domain.framework.DateManager
import splitties.init.appCtx

class AndroidDateManager : DateManager {
    override fun getRelativeDateTimeString(timeMillis: Long, minResolution: Long, transitionResolution: Long): String {
        return DateUtils.getRelativeDateTimeString(appCtx,
                timeMillis,
                minResolution,
                transitionResolution,
                DateUtils.FORMAT_ABBREV_MONTH or DateUtils.FORMAT_ABBREV_WEEKDAY
        ).toString()
    }
}
