package inc.ahmedmourad.sherlock.domain.model.auth

data class DomainUser(
        val id: String,
        val registrationDate: Long,
        val lastLoginDate: Long,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val pictureUrl: String
)
