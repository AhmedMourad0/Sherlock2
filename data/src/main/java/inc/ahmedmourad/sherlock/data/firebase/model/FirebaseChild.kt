package inc.ahmedmourad.sherlock.data.firebase.model

interface FirebaseChild {
    val id: String
    val timeMillis: Long
    val name: FirebaseName
    val notes: String
    val location: FirebaseLocation
    val appearance: FirebaseAppearance
}
