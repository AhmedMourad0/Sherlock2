package inc.ahmedmourad.sherlock.domain.model.auth.submodel

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

class PhoneNumber private constructor(val number: String, val countryCode: String) {

    fun component1() = number

    fun component2() = countryCode

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as PhoneNumber

        if (number != other.number)
            return false

        if (countryCode != other.countryCode)
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = number.hashCode()
        result = 31 * result + countryCode.hashCode()
        return result
    }

    override fun toString(): String {
        return "PhoneNumber(number='$number', countryCode='$countryCode')"
    }

    companion object {

        fun of(number: String, countryCode: String = ""): Either<Exception, PhoneNumber> {
            return when {

                number.isBlank() -> Exception.BlankPhoneNumberException.left()

                number.trim().contains(" ") -> Exception.PhoneNumberContainsWhiteSpacesException.left()

                countryCode.trim().contains(" ") -> Exception.CountryCodeContainsWhiteSpacesException.left()

                else -> validatePhoneNumber(number, countryCode)
            }
        }

        private fun validatePhoneNumber(phoneNumber: String, countryCode: String): Either<Exception, PhoneNumber> {
            return try {
                val phoneUtil = PhoneNumberUtil.getInstance()
                val numberProto = phoneUtil.parse(phoneNumber, countryCode)
                if (numberProto.hasCountryCode() && phoneUtil.isValidNumber(numberProto)) {
                    PhoneNumber(numberProto.nationalNumber.toString(), numberProto.countryCode.toString()).right()
                } else {
                    Exception.InvalidPhoneNumberException.left()
                }
            } catch (e: NumberParseException) {
                when (e.errorType) {
                    NumberParseException.ErrorType.INVALID_COUNTRY_CODE -> Exception.InvalidCountryCodeException.left()
                    NumberParseException.ErrorType.NOT_A_NUMBER -> Exception.InvalidPhoneNumberException.left()
                    NumberParseException.ErrorType.TOO_SHORT_AFTER_IDD -> Exception.PhoneNumberTooShortAfterIddException.left()
                    NumberParseException.ErrorType.TOO_SHORT_NSN -> Exception.PhoneNumberTooShortException.left()
                    NumberParseException.ErrorType.TOO_LONG -> Exception.PhoneNumberTooLongException.left()
                    null -> Exception.InvalidPhoneNumberException.left()
                }
            }
        }
    }

    sealed class Exception {
        object BlankPhoneNumberException : Exception()
        object PhoneNumberContainsWhiteSpacesException : Exception()
        object CountryCodeContainsWhiteSpacesException : Exception()
        object PhoneNumberTooShortAfterIddException : Exception()
        object PhoneNumberTooShortException : Exception()
        object PhoneNumberTooLongException : Exception()
        object InvalidCountryCodeException : Exception()
        object InvalidPhoneNumberException : Exception()
    }
}
