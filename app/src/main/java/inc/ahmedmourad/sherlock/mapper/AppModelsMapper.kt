package inc.ahmedmourad.sherlock.mapper

import inc.ahmedmourad.sherlock.domain.model.auth.DomainIncompleteUser
import inc.ahmedmourad.sherlock.domain.model.auth.DomainSignedInUser
import inc.ahmedmourad.sherlock.domain.model.auth.DomainUser
import inc.ahmedmourad.sherlock.model.auth.AppIncompleteUser
import inc.ahmedmourad.sherlock.model.auth.AppSignedInUser
import inc.ahmedmourad.sherlock.model.auth.AppUser

internal fun DomainSignedInUser.toAppSignedInUser() = AppSignedInUser(
        id,
        registrationDate,
        email,
        name,
        phoneNumber,
        pictureUrl
)

internal fun DomainUser.toAppUser() = AppUser(
        id,
        registrationDate,
        lastLoginDate,
        email,
        name,
        phoneNumber,
        pictureUrl
)

internal fun DomainIncompleteUser.toAppIncompleteUser() = AppIncompleteUser(
        id,
        email,
        name,
        phoneNumber,
        pictureUrl
)
