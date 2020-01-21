package inc.ahmedmourad.sherlock.auth.remote.model

import inc.ahmedmourad.sherlock.auth.model.AuthRetrievedUserDetails

data class RemoteRetrievedUserDetails(
        val id: String,
        val registrationDate: Long,
        val phoneNumber: String
) {
    fun toAuthUserDetails() = AuthRetrievedUserDetails(
            id,
            registrationDate,
            phoneNumber
    )
}
