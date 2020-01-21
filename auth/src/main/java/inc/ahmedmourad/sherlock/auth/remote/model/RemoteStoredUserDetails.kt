package inc.ahmedmourad.sherlock.auth.remote.model

import com.google.firebase.firestore.FieldValue
import inc.ahmedmourad.sherlock.auth.remote.contract.RemoteContract

internal data class RemoteStoredUserDetails(
        val id: String,
        val phoneNumber: String
) {

    fun toRemoteRetrievedUserDetails(registrationDate: Long) = RemoteRetrievedUserDetails(
            id,
            registrationDate,
            phoneNumber
    )

    fun toMap(): Map<String, Any> = hashMapOf(
            RemoteContract.Database.Users.REGISTRATION_DATE to FieldValue.serverTimestamp(),
            RemoteContract.Database.Users.LAST_LOGIN_DATE to FieldValue.serverTimestamp(),
            RemoteContract.Database.Users.PHONE_NUMBER to phoneNumber
    )
}
