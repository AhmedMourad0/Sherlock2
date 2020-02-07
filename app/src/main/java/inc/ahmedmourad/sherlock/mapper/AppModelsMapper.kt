package inc.ahmedmourad.sherlock.mapper

import inc.ahmedmourad.sherlock.domain.model.auth.IncompleteUser
import inc.ahmedmourad.sherlock.domain.model.auth.SignedInUser
import inc.ahmedmourad.sherlock.domain.model.auth.User
import inc.ahmedmourad.sherlock.model.auth.AppIncompleteUser
import inc.ahmedmourad.sherlock.model.auth.AppSignedInUser
import inc.ahmedmourad.sherlock.model.auth.AppUser

internal fun SignedInUser.toAppSignedInUser() = AppSignedInUser(
        id,
        registrationDate,
        email,
        name,
        phoneNumber,
        pictureUrl
)

internal fun User.toAppUser() = AppUser(
        id,
        registrationDate,
        lastLoginDate,
        email,
        name,
        phoneNumber,
        pictureUrl
)

internal fun IncompleteUser.toAppIncompleteUser() = AppIncompleteUser(
        id,
        email,
        name,
        phoneNumber,
        pictureUrl
)
