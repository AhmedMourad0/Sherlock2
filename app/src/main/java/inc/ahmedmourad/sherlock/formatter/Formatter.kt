package inc.ahmedmourad.sherlock.formatter

import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.model.AppLocation
import inc.ahmedmourad.sherlock.model.AppName
import inc.ahmedmourad.sherlock.model.AppRange

internal interface Formatter {

    fun formatSkin(skin: Skin): String

    fun formatHair(hair: Hair): String

    fun formatGender(gender: Gender): String

    fun formatName(name: AppName): String

    fun formatNotes(notes: String): String

    fun formatAge(age: AppRange): String

    fun formatLocation(location: AppLocation): String

    fun formatLocation(locationName: String, locationAddress: String): String

    fun formatHeight(height: AppRange): String
}