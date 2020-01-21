package inc.ahmedmourad.sherlock.auth.remote.contract

internal object RemoteContract {

    object Database {

        object Users {

            const val PATH = "p"

            const val REGISTRATION_DATE = "r"
            const val LAST_LOGIN_DATE = "l"
            const val EMAIL = "e"
            const val NAME = "n"
            const val PHONE_NUMBER = "p"
            const val PICTURE_URL = "u"
        }
    }
}
