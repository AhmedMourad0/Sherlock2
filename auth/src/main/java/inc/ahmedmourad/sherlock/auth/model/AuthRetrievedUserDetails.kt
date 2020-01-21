package inc.ahmedmourad.sherlock.auth.model

data class AuthRetrievedUserDetails(
        val id: String,
        val registrationDate: Long,
        val phoneNumber: String
)
