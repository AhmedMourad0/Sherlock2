package inc.ahmedmourad.sherlock.model

import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.domain.model.auth.submodel.*
import inc.ahmedmourad.sherlock.domain.model.children.ChildQuery
import inc.ahmedmourad.sherlock.domain.model.children.submodel.*
import inc.ahmedmourad.sherlock.domain.model.common.Name
import inc.ahmedmourad.sherlock.model.auth.AppCompletedUser
import inc.ahmedmourad.sherlock.model.children.AppPublishedChild
import splitties.init.appCtx

internal fun Name.Exception.localizedMessage(): String {
    return when (this) {
        Name.Exception.BlankNameException ->
            appCtx.getString(R.string.name_empty_or_blank)
        Name.Exception.NameContainsWhiteSpacesException ->
            appCtx.getString(R.string.name_contains_white_spaces)
        is Name.Exception.NameTooShortException ->
            appCtx.getString(R.string.name_too_short, this.minLength)
        is Name.Exception.NameTooLongException ->
            appCtx.getString(R.string.name_too_long, this.maxLength)
        Name.Exception.NameContainsNumbersOrSymbolsException ->
            appCtx.getString(R.string.name_contains_numbers_or_symbols)
    }
}

@Suppress("unused")
internal fun FullName.Exception.localizedMessage(): String {
    return appCtx.getString(R.string.invalid_first_and_last_name)
}

internal fun Age.Exception.localizedMessage(): String {
    return when (this) {
        is Age.Exception.AgeOutOfRangeException ->
            appCtx.getString(R.string.age_out_of_range, this.min, this.max)
    }
}

internal fun AgeRange.Exception.localizedMessage(): String {
    return when (this) {
        is AgeRange.Exception.MinExceedsMaxException -> appCtx.getString(R.string.age_min_exceeds_max)
    }
}

internal fun Height.Exception.localizedMessage(): String {
    return when (this) {
        is Height.Exception.HeightOutOfRangeException ->
            appCtx.getString(R.string.height_out_of_range, this.min, this.max)
    }
}

internal fun HeightRange.Exception.localizedMessage(): String {
    return when (this) {
        is HeightRange.Exception.MinExceedsMaxException -> appCtx.getString(R.string.height_min_exceeds_max)
    }
}

internal fun Coordinates.Exception.localizedMessage(): String {
    return when (this) {
        Coordinates.Exception.InvalidLatitudeException ->
            appCtx.getString(R.string.invalid_latitude)
        Coordinates.Exception.InvalidLongitudeException ->
            appCtx.getString(R.string.invalid_longitude)
        Coordinates.Exception.InvalidCoordinatesException ->
            appCtx.getString(R.string.invalid_coordinates)
    }
}

@Suppress("unused")
internal fun Location.Exception.localizedMessage(): String {
    return appCtx.getString(R.string.invalid_location)
}

internal fun ApproximateAppearance.Exception.localizedMessage(): String {
    return when (this) {
        ApproximateAppearance.Exception.NotEnoughDetailsException ->
            appCtx.getString(R.string.few_appearance_details)
    }
}

@Suppress("unused")
internal fun ExactAppearance.Exception.localizedMessage(): String {
    return appCtx.getString(R.string.incomplete_child_appearance)
}

internal fun AppPublishedChild.Exception.localizedMessage(): String {
    return when (this) {
        AppPublishedChild.Exception.NotEnoughDetailsException ->
            appCtx.getString(R.string.child_not_enough_details)
    }
}

@Suppress("unused")
internal fun ChildQuery.Exception.localizedMessage(): String {
    return appCtx.getString(R.string.incomplete_child_query)
}

internal fun Email.Exception.localizedMessage(): String {
    return when (this) {
        Email.Exception.BlankEmailException -> appCtx.getString(R.string.email_empty_or_blank)
        Email.Exception.EmailContainsWhiteSpacesException -> appCtx.getString(R.string.email_contains_whitespaces)
        Email.Exception.MalformedEmailException -> appCtx.getString(R.string.malformed_email)
    }
}

internal fun Password.Exception.localizedMessage(): String {
    return when (this) {
        Password.Exception.BlankPasswordException -> TODO()
        is Password.Exception.PasswordTooShortException -> TODO()
        Password.Exception.NoLettersException -> TODO()
        Password.Exception.NoDigitsException -> TODO()
        Password.Exception.NoSymbolsException -> TODO()
        is Password.Exception.FewDistinctCharactersException -> TODO()
    }
}

internal fun DisplayName.Exception.localizedMessage(): String {
    return when (this) {
        DisplayName.Exception.BlankDisplayNameException ->
            appCtx.getString(R.string.display_name_empty_or_blank)
        is DisplayName.Exception.DisplayNameTooShortException ->
            appCtx.getString(R.string.display_name_too_short, this.minLength)
        is DisplayName.Exception.DisplayNameTooLongException ->
            appCtx.getString(R.string.display_name_too_long, this.maxLength)
        DisplayName.Exception.DisplayNameContainsNumbersOrSymbolsException ->
            appCtx.getString(R.string.display_name_contains_numbers_or_symbols)
        is DisplayName.Exception.SingleNameException ->
            this.exception.localizedMessage()
    }
}

internal fun PhoneNumber.Exception.localizedMessage(): String {
    return when (this) {
        PhoneNumber.Exception.BlankPhoneNumberException ->
            appCtx.getString(R.string.invalid_phone_number)
        PhoneNumber.Exception.PhoneNumberContainsWhiteSpacesException ->
            appCtx.getString(R.string.phone_number_contains_whitespaces)
        PhoneNumber.Exception.CountryCodeContainsWhiteSpacesException ->
            appCtx.getString(R.string.country_code_contains_whitespaces)
        PhoneNumber.Exception.PhoneNumberTooShortAfterIddException ->
            appCtx.getString(R.string.international_phone_number_too_short)
        PhoneNumber.Exception.PhoneNumberTooShortException ->
            appCtx.getString(R.string.phone_number_too_short)
        PhoneNumber.Exception.PhoneNumberTooLongException ->
            appCtx.getString(R.string.phone_number_too_long)
        PhoneNumber.Exception.InvalidCountryCodeException ->
            appCtx.getString(R.string.invalid_country_code)
        PhoneNumber.Exception.InvalidPhoneNumberException ->
            appCtx.getString(R.string.invalid_phone_number)
    }
}

@Suppress("unused")
internal fun UserCredentials.Exception.localizedMessage(): String {
    return when (this) {
        UserCredentials.Exception.EmailIsUsedAsPasswordException -> TODO()
    }
}

@Suppress("unused")
internal fun AppCompletedUser.Exception.localizedMessage(): String {
    return appCtx.getString(R.string.invalid_user)
}
