package inc.ahmedmourad.sherlock.viewmodel.controllers.validators.auth

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.domain.model.auth.submodel.DisplayName
import inc.ahmedmourad.sherlock.domain.model.auth.submodel.Email
import inc.ahmedmourad.sherlock.domain.model.auth.submodel.PhoneNumber
import inc.ahmedmourad.sherlock.domain.model.common.PicturePath
import inc.ahmedmourad.sherlock.domain.model.ids.UserId
import inc.ahmedmourad.sherlock.model.auth.AppCompletedUser
import inc.ahmedmourad.sherlock.model.localizedMessage
import splitties.init.appCtx

internal fun validateEmail(value: String?): Either<String, Email> {

    if (value == null) {
        return appCtx.getString(R.string.malformed_email).left()
    }

    return Email.of(value).mapLeft(Email.Exception::localizedMessage)
}

internal fun validateDisplayName(value: String?): Either<String, DisplayName> {

    if (value == null) {
        return appCtx.getString(R.string.invalid_display_name).left()
    }

    return DisplayName.of(value).mapLeft(DisplayName.Exception::localizedMessage)
}

internal fun validatePhoneNumber(location: PhoneNumber?): Either<String, PhoneNumber> {
    return location?.right() ?: appCtx.getString(R.string.invalid_phone_number).left()
}

internal fun validateAppCompletedUser(
        id: UserId,
        email: Email,
        displayName: DisplayName,
        phoneNumber: PhoneNumber,
        picturePath: PicturePath?
): Either<String, AppCompletedUser> {
    return AppCompletedUser.of(
            id,
            email,
            displayName,
            phoneNumber,
            picturePath
    ).mapLeft(AppCompletedUser.Exception::localizedMessage)
}
