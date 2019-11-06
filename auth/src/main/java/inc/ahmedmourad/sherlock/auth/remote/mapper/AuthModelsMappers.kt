package inc.ahmedmourad.sherlock.auth.remote.mapper

import inc.ahmedmourad.sherlock.auth.authenticator.model.AuthSignUpUser
import inc.ahmedmourad.sherlock.auth.remote.model.AuthUserData
import inc.ahmedmourad.sherlock.domain.model.DomainSignUpUser
import inc.ahmedmourad.sherlock.domain.model.DomainUserData

internal fun DomainSignUpUser.toAuthSignUpUser() = AuthSignUpUser(
        password,
        email,
        name,
        phoneNumber,
        picture
)

internal fun DomainUserData.toAuthUserData() = AuthUserData(
        id,
        email,
        name,
        phoneNumber,
        picture
)
