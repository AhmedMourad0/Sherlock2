package inc.ahmedmourad.sherlock.domain.model

import arrow.core.Either
import arrow.core.right
import inc.ahmedmourad.sherlock.domain.model.auth.submodel.Email
import inc.ahmedmourad.sherlock.domain.model.auth.submodel.Password

class UserCredentials private constructor(
        val email: Email,
        val password: Password
) {

    fun component1() = email

    fun component2() = password

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as UserCredentials

        if (email != other.email)
            return false

        if (password != other.password)
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + password.hashCode()
        return result
    }

    override fun toString(): String {
        return "SignUpUser(" +
                "email=$email, " +
                "password=$password" +
                ")"
    }

    companion object {
        fun of(email: Email, password: Password): Either<Exception, UserCredentials> {
            return UserCredentials(email, password).right()
        }
    }

    sealed class Exception
}
