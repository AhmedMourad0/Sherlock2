package inc.ahmedmourad.sherlock.auth.mapper

import inc.ahmedmourad.sherlock.auth.model.AuthCompletedUser
import inc.ahmedmourad.sherlock.auth.model.AuthSignUpUser
import inc.ahmedmourad.sherlock.auth.model.AuthStoredUserDetails
import inc.ahmedmourad.sherlock.domain.model.auth.CompletedUser
import inc.ahmedmourad.sherlock.domain.model.auth.SignUpUser

internal fun SignUpUser.toAuthSignUpUser() = AuthSignUpUser(
        password,
        email,
        name,
        phoneNumber
)

internal fun CompletedUser.toAuthCompletedUser(pictureUrl: String) = AuthCompletedUser(
        id,
        email,
        name,
        pictureUrl
)

internal fun CompletedUser.toAuthStoredUserDetails() = AuthStoredUserDetails(
        id,
        phoneNumber
)
