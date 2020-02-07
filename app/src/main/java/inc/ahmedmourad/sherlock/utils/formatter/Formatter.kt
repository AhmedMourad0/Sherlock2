package inc.ahmedmourad.sherlock.utils.formatter

import arrow.core.Either
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.model.children.submodel.*

internal interface Formatter {

    fun formatSkin(skin: Skin?): String

    fun formatHair(hair: Hair?): String

    fun formatGender(gender: Gender?): String

    fun formatName(name: Either<Name, FullName>?): String

    fun formatNotes(notes: String?): String

    fun formatAge(age: AgeRange?): String

    fun formatLocation(location: Location?): String

    fun formatLocation(locationName: String?, locationAddress: String?): String

    fun formatHeight(height: HeightRange?): String
}
