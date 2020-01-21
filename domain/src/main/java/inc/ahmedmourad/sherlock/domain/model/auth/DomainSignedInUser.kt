package inc.ahmedmourad.sherlock.domain.model.auth

data class DomainSignedInUser(
        val id: String,
        val registrationDate: Long,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val pictureUrl: String
)
