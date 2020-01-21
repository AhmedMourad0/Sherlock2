package inc.ahmedmourad.sherlock.auth.model

data class AuthUser(
        val id: String,
        val registrationDate: Long,
        val lastLoginDate: Long,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val pictureUrl: String
)
