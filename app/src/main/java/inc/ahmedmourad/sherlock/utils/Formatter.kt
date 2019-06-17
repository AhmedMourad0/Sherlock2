package inc.ahmedmourad.sherlock.utils

import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.model.AppLocation
import inc.ahmedmourad.sherlock.model.AppName
import inc.ahmedmourad.sherlock.model.AppRange

interface Formatter<T> {

    fun formatSkin(skin: Skin): T

    fun formatHair(hair: Hair): T

    fun formatGender(gender: Gender): T

    fun formatName(name: AppName): T

    fun formatNotes(notes: String): T

    fun formatAge(age: AppRange): T

    fun formatLocation(location: AppLocation): T

    fun formatHeight(height: AppRange): T
}
