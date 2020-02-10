package inc.ahmedmourad.sherlock.model.children

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.domain.model.children.PublishedChild
import inc.ahmedmourad.sherlock.domain.model.children.submodel.*
import inc.ahmedmourad.sherlock.utils.getImageBytes

//TODO: follow PublishedChild's rules
internal class AppPublishedChild private constructor(
        val name: Either<Name, FullName>?,
        val notes: String?,
        val location: Location?,
        val appearance: ApproximateAppearance,
        val picturePath: PicturePath?
) {

    fun toPublishedChild() = PublishedChild.of(
            name,
            notes,
            location,
            appearance,
            getImageBytes(picturePath, R.drawable.placeholder)
    )

    fun component1() = name

    fun component2() = notes

    fun component3() = location

    fun component4() = appearance

    fun component5() = picturePath

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as AppPublishedChild

        if (name != other.name)
            return false

        if (notes != other.notes)
            return false

        if (location != other.location)
            return false

        if (appearance != other.appearance)
            return false

        if (picturePath != other.picturePath)
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + appearance.hashCode()
        result = 31 * result + picturePath.hashCode()
        return result
    }

    override fun toString(): String {
        return "AppPublishedChild(" +
                "name=$name, " +
                "notes=$notes, " +
                "location=$location, " +
                "appearance=$appearance, " +
                "picturePath=$picturePath" +
                ")"
    }

    companion object {
        fun of(name: Either<Name, FullName>?,
               notes: String?,
               location: Location?,
               appearance: ApproximateAppearance,
               picturePath: PicturePath?
        ): Either<Exception, AppPublishedChild> {
            return if (name != null || notes != null || location != null || picturePath != null) {
                AppPublishedChild(name, notes, location, appearance, picturePath).right()
            } else {
                Exception.NotEnoughDetailsException.left()
            }
        }
    }

    sealed class Exception {
        object NotEnoughDetailsException : Exception()
    }
}
