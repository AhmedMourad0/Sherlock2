package inc.ahmedmourad.sherlock.viewmodel.controllers.validators.auth

import arrow.core.Either
import arrow.core.left
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.domain.model.auth.submodel.*
import inc.ahmedmourad.sherlock.domain.model.common.PicturePath
import inc.ahmedmourad.sherlock.domain.model.ids.UserId
import inc.ahmedmourad.sherlock.model.auth.AppCompletedUser
import inc.ahmedmourad.sherlock.model.localizedMessage
import splitties.init.appCtx

//TODO: provide better messages for empty or blank
internal fun validateEmail(value: String?): Either<String, Email> {

    if (value == null) {
        return appCtx.getString(R.string.malformed_email).left()
    }

    return Email.of(value).mapLeft(Email.Exception::localizedMessage)
}

internal fun validatePassword(value: String?): Either<String, Password> {

    if (value == null) {
        return appCtx.getString(R.string.malformed_email).left()
    }

    return Password.of(value).mapLeft(Password.Exception::localizedMessage)
}

internal fun validateDisplayName(value: String?): Either<String, DisplayName> {

    if (value == null) {
        return appCtx.getString(R.string.invalid_display_name).left()
    }

    return DisplayName.of(value).mapLeft(DisplayName.Exception::localizedMessage)
}

internal fun validatePhoneNumber(countryCode: String?, number: String?): Either<String, PhoneNumber> {

    if (number == null) {
        return appCtx.getString(R.string.invalid_phone_number).left()
    }

    if (countryCode == null) {
        return appCtx.getString(R.string.invalid_country_code).left()
    }

    return PhoneNumber.of(number, countryCode).mapLeft(PhoneNumber.Exception::localizedMessage)
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

internal fun validateUserCredentials(
        email: Email,
        password: Password
): Either<String, UserCredentials> {
    return UserCredentials.of(
            email,
            password
    ).mapLeft(UserCredentials.Exception::localizedMessage)
}
