package inc.ahmedmourad.sherlock.model.auth

internal data class AppSignedInUser(
        val id: String,
        val registrationDate: Long,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val pictureUrl: String
)
