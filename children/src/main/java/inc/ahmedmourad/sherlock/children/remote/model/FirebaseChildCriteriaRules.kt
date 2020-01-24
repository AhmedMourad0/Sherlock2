package inc.ahmedmourad.sherlock.children.remote.model

internal data class FirebaseChildCriteriaRules(
        val name: FirebaseName,
        val location: FirebaseLocation?,
        val appearance: FirebaseExactAppearance
)
