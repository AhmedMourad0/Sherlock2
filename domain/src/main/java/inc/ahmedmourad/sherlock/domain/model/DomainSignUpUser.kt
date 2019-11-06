package inc.ahmedmourad.sherlock.domain.model

data class DomainSignUpUser(
        val password: String,
        override val email: String,
        override val name: String,
        override val phoneNumber: String,
        val picture: ByteArray
) : DomainUser {

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as DomainSignUpUser

        if (password != other.password)
            return false

        if (email != other.email)
            return false

        if (name != other.name)
            return false

        if (phoneNumber != other.phoneNumber)
            return false

        if (!picture.contentEquals(other.picture))
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = password.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + picture.contentHashCode()
        return result
    }
}
