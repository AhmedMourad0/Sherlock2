package inc.ahmedmourad.sherlock.auth.remote.mapper

import inc.ahmedmourad.sherlock.auth.model.AuthStoredUserDetails
import inc.ahmedmourad.sherlock.auth.remote.model.RemoteStoredUserDetails

internal fun AuthStoredUserDetails.toRemoteUserDetails() = RemoteStoredUserDetails(
        id,
        phoneNumber
)
