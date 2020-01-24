package inc.ahmedmourad.sherlock.domain.model.auth

data class DomainIncompleteUser(
        val id: String,
        val email: String?,
        val name: String?,
        val phoneNumber: String?,
        val pictureUrl: String?
)
