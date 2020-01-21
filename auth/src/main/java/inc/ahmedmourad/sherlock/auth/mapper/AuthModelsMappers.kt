package inc.ahmedmourad.sherlock.auth.mapper

import inc.ahmedmourad.sherlock.auth.model.AuthCompletedUser
import inc.ahmedmourad.sherlock.auth.model.AuthSignUpUser
import inc.ahmedmourad.sherlock.auth.model.AuthStoredUserDetails
import inc.ahmedmourad.sherlock.domain.model.auth.DomainCompletedUser
import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignUpUser

internal fun DomainSignUpUser.toAuthSignUpUser() = AuthSignUpUser(
        password,
        email,
        name,
        phoneNumber
)

internal fun DomainCompletedUser.toAuthCompletedUser(pictureUrl: String) = AuthCompletedUser(
        id,
        email,
        name,
        pictureUrl
)

internal fun DomainCompletedUser.toAuthStoredUserDetails() = AuthStoredUserDetails(
        id,
        phoneNumber
)
