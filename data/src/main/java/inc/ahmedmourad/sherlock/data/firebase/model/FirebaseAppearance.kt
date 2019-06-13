package inc.ahmedmourad.sherlock.data.firebase.model

import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin

data class FirebaseAppearance(
        val gender: Gender,
        val skin: Skin,
        val hair: Hair,
        val age: FirebaseRange,
        val height: FirebaseRange
)
