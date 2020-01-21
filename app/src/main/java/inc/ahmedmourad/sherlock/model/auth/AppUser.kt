package inc.ahmedmourad.sherlock.model.auth

internal data class AppUser(
        val id: String,
        val registrationDate: Long,
        val lastLoginDate: Long,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val pictureUrl: String
)
