package inc.ahmedmourad.sherlock.domain.model

data class DomainSignedInUser(
        val id: String,
        val registrationDate: Long,
        val lastLoginDate: Long,
        override val email: String,
        override val name: String,
        override val phoneNumber: String,
        val pictureUrl: String
) : DomainUser
