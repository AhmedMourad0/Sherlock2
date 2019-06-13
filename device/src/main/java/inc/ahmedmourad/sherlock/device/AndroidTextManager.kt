package inc.ahmedmourad.sherlock.device

import inc.ahmedmourad.sherlock.domain.device.TextManager
import splitties.init.appCtx

class AndroidTextManager : TextManager {

    override fun publishing(): String = appCtx.getString(R.string.publishing)

    override fun somethingWentWrong(): String = appCtx.getString(R.string.something_went_wrong)

    override fun publishedSuccessfully(): String = appCtx.getString(R.string.published_successfully)
}
