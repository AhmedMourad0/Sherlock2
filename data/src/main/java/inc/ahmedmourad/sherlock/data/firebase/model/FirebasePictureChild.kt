package inc.ahmedmourad.sherlock.data.firebase.model

data class FirebasePictureChild(override val id: String,
                                override val publicationDate: Long,
                                override val name: FirebaseName,
                                override val notes: String,
                                override val location: FirebaseLocation,
                                override val appearance: FirebaseAppearance,
                                val picture: ByteArray) : FirebaseChild {

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as FirebasePictureChild

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

        if (!picture.contentEquals(other.picture))
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
        result = 31 * result + picture.contentHashCode()
        return result
    }
}
