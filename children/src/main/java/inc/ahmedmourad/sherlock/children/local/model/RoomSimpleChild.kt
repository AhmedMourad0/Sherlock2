package inc.ahmedmourad.sherlock.children.local.model

internal data class RoomSimpleChild(
        val id: String,
        val publicationDate: Long,
        val name: RoomName,
        val notes: String,
        val locationName: String,
        val locationAddress: String,
        val pictureUrl: String
)
