package inc.ahmedmourad.sherlock.auth.authenticator.mapper

import inc.ahmedmourad.sherlock.auth.authenticator.model.AuthenticatorCompletedUser
import inc.ahmedmourad.sherlock.auth.model.AuthCompletedUser

internal fun AuthCompletedUser.toAuthenticatorUserDetails() = AuthenticatorCompletedUser(
        id,
        email,
        name,
        pictureUrl
)
