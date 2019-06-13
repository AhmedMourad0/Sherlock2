package inc.ahmedmourad.sherlock.domain.model

data class DomainPictureChild(
        override val id: String,
        override val timeMillis: Long,
        override val name: DomainName,
        override val notes: String,
        override val location: DomainLocation,
        override val appearance: DomainAppearance,
        val picture: ByteArray
) : DomainChild {

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as DomainPictureChild

        if (id != other.id)
            return false

        if (timeMillis != other.timeMillis)
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
        result = 31 * result + timeMillis.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + appearance.hashCode()
        result = 31 * result + picture.contentHashCode()
        return result
    }
}
