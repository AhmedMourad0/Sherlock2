package inc.ahmedmourad.sherlock.utils.formatter

import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.platform.TextManager
import inc.ahmedmourad.sherlock.model.children.AppLocation
import inc.ahmedmourad.sherlock.model.children.AppName
import inc.ahmedmourad.sherlock.model.children.AppRange
import splitties.init.appCtx

internal class TextFormatter(private val textManager: TextManager) : Formatter {

    override fun formatSkin(skin: Skin): String = skin.getMessage(textManager)

    override fun formatHair(hair: Hair): String = hair.getMessage(textManager)

    override fun formatGender(gender: Gender): String = gender.getMessage(textManager)

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

        return formatLocation(location.name, location.address)
    }

    override fun formatLocation(locationName: String, locationAddress: String): String {

        val isNameEmpty = locationName.isBlank()
        val isAddressEmpty = locationAddress.isBlank()

        return if (!isNameEmpty && !isAddressEmpty)
            appCtx.getString(R.string.location, locationName, locationAddress)
        else if (!isNameEmpty)
            locationName
        else if (!isAddressEmpty)
            locationAddress
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
