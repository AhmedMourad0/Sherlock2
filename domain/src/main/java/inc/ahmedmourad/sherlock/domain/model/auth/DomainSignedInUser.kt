package inc.ahmedmourad.sherlock.domain.model.auth

//TODO: picture may be null (not just here)
data class DomainSignedInUser(
        val id: String,
        val registrationDate: Long,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val pictureUrl: String
)
