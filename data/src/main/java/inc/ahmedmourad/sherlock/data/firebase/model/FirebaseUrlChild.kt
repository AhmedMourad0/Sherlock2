package inc.ahmedmourad.sherlock.data.firebase.model

import inc.ahmedmourad.sherlock.data.firebase.contract.FirebaseContract

data class FirebaseUrlChild(override val id: String,
                            override val timeMillis: Long,
                            override val name: FirebaseName,
                            override val notes: String,
                            override val location: FirebaseLocation,
                            override val appearance: FirebaseAppearance,
                            val pictureUrl: String) : FirebaseChild {

    constructor(child: FirebasePictureChild, pictureUrl: String) : this(child.id,
            child.timeMillis,
            child.name,
            child.notes,
            child.location,
            child.appearance,
            pictureUrl
    )

    fun toMap(): Map<String, Any> = hashMapOf(
            FirebaseContract.Database.CHILDREN_TIME_MILLIS to timeMillis,
            FirebaseContract.Database.CHILDREN_FIRST_NAME to name.first,
            FirebaseContract.Database.CHILDREN_LAST_NAME to name.last,
            FirebaseContract.Database.CHILDREN_LOCATION to location.store(), //TODO: maybe not just store it as a string
            FirebaseContract.Database.CHILDREN_START_AGE to appearance.age.from,
            FirebaseContract.Database.CHILDREN_END_AGE to appearance.age.to,
            FirebaseContract.Database.CHILDREN_START_HEIGHT to appearance.height.from,
            FirebaseContract.Database.CHILDREN_END_HEIGHT to appearance.height.to,
            FirebaseContract.Database.CHILDREN_GENDER to appearance.gender.value,
            FirebaseContract.Database.CHILDREN_SKIN to appearance.skin.value,
            FirebaseContract.Database.CHILDREN_HAIR to appearance.hair.value,
            FirebaseContract.Database.CHILDREN_NOTES to notes,
            FirebaseContract.Database.CHILDREN_PICTURE_URL to pictureUrl
    )
}
