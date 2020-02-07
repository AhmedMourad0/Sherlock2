package inc.ahmedmourad.sherlock.domain.model.children

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import inc.ahmedmourad.sherlock.domain.model.children.submodel.*

//TODO: follow PublishedChild's rules
class RetrievedChild private constructor(
        val id: ChildId,
        val publicationDate: Long,
        val name: Either<Name, FullName>?,
        val notes: String?,
        val location: Location?,
        val appearance: ApproximateAppearance,
        val pictureUrl: Url?
) {

    fun simplify(): Either<SimpleRetrievedChild.Exception, SimpleRetrievedChild> {
        return SimpleRetrievedChild.of(
                id,
                publicationDate,
                name,
                notes,
                location?.name,
                location?.address,
                pictureUrl
        )
    }

    fun component1() = id

    fun component2() = publicationDate

    fun component3() = name

    fun component4() = notes

    fun component5() = location

    fun component6() = appearance

    fun component7() = pictureUrl

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as RetrievedChild

        if (id != other.id)
            return false

        if (publicationDate != other.publicationDate)
            return false

        if (name != other.name)
            return false

        if (notes != other.notes)
            return false

        if (location != other.location)
            return false

        if (appearance != other.appearance)
            return false

        if (pictureUrl != other.pictureUrl)
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + publicationDate.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + appearance.hashCode()
        result = 31 * result + pictureUrl.hashCode()
        return result
    }

    override fun toString(): String {
        return "RetrievedChild(" +
                "id='$id', " +
                "publicationDate=$publicationDate, " +
                "name=$name, " +
                "notes=$notes, " +
                "location=$location, " +
                "appearance=$appearance, " +
                "pictureUrl=$pictureUrl" +
                ")"
    }

    companion object {
        fun of(id: ChildId,
               publicationDate: Long,
               name: Either<Name, FullName>?,
               notes: String?,
               location: Location?,
               appearance: ApproximateAppearance,
               pictureUrl: Url?
        ): Either<Exception, RetrievedChild> {
            return if (name != null || notes != null || location != null || pictureUrl != null) {
                RetrievedChild(
                        id,
                        publicationDate,
                        name,
                        notes,
                        location,
                        appearance,
                        pictureUrl
                ).right()
            } else {
                Exception.NotEnoughDetailsException.left()
            }
        }
    }

    sealed class Exception {
        object NotEnoughDetailsException : Exception()
    }
}
