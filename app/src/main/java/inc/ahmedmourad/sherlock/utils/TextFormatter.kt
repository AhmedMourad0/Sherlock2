package inc.ahmedmourad.sherlock.utils

import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.model.AppLocation
import inc.ahmedmourad.sherlock.model.AppName
import inc.ahmedmourad.sherlock.model.AppRange
import splitties.init.appCtx

class TextFormatter : Formatter<String> {

    override fun formatSkin(skin: Skin): String = when (skin) {

        Skin.WHITE -> appCtx.getString(R.string.white_skin)

        Skin.WHEAT -> appCtx.getString(R.string.wheatish_skin)

        Skin.DARK -> appCtx.getString(R.string.dark_skin)

        else -> appCtx.getString(R.string.not_available)
    }

    override fun formatHair(hair: Hair): String = when (hair) {

        Hair.BLONDE -> appCtx.getString(R.string.blonde_hair)

        Hair.BROWN -> appCtx.getString(R.string.brown_hair)

        Hair.DARK -> appCtx.getString(R.string.dark_hair)

        else -> appCtx.getString(R.string.not_available)
    }

    override fun formatGender(gender: Gender): String = when (gender) {

        Gender.MALE -> appCtx.getString(R.string.male)

        Gender.FEMALE -> appCtx.getString(R.string.female)

        else -> appCtx.getString(R.string.not_available)
    }

    override fun formatName(name: AppName): String {

        val isFirstNameEmpty = name.first.isBlank()
        val isLastNameEmpty = name.last.isBlank()

        return if (!isFirstNameEmpty && !isLastNameEmpty)
            "${name.first} ${name.last}"
        else if (!isFirstNameEmpty)
            name.first
        else if (!isLastNameEmpty)
            name.last
        else
            appCtx.getString(R.string.not_available)
    }

    override fun formatNotes(notes: String): String = if (notes.isNotBlank()) notes else appCtx.getString(R.string.not_available)

    override fun formatAge(age: AppRange): String = appCtx.getString(R.string.years_range, "${age.from} - ${age.to}")

    override fun formatLocation(location: AppLocation): String {

        if (!location.isValid())
            return appCtx.getString(R.string.not_available)

        val isNameEmpty = location.name.isBlank()
        val isAddressEmpty = location.address.isBlank()

        return if (!isNameEmpty && !isAddressEmpty)
            appCtx.getString(R.string.location, location.name, location.address)
        else if (!isNameEmpty)
            location.name
        else if (!isAddressEmpty)
            location.address
        else
            return appCtx.getString(R.string.not_available)
    }

    override fun formatHeight(height: AppRange): String = appCtx.getString(R.string.height_range,
            formatHeight(height.from),
            formatHeight(height.to)
    )

    private fun formatHeight(height: Int): String {

        var result = ""

        val meters = height / 100
        val centimeters = height % 100

        if (meters > 0)
            result += appCtx.resources.getQuantityString(R.plurals.height_meters, meters, meters)

        if (meters > 0 && centimeters > 0)
            result += " "

        if (centimeters > 0)
            result += appCtx.resources.getQuantityString(R.plurals.height_centimeters, centimeters, centimeters)

        return if (result.isBlank()) appCtx.getString(R.string.not_available) else result
    }
}
