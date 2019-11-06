package inc.ahmedmourad.sherlock.children.local.contract

internal object Contract {

    const val DATABASE_NAME = "SherlockDatabase"

    object ChildrenEntry {

        const val TABLE_NAME = "children"

        const val COLUMN_ID = "id"
        const val COLUMN_PUBLICATION_DATE = "publicationDate"
        const val COLUMN_FIRST_NAME = "first_name"
        const val COLUMN_LAST_NAME = "last_name"
        const val COLUMN_PICTURE_URL = "picture_url"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_NOTES = "notes"
        const val COLUMN_GENDER = "isSameGender"
        const val COLUMN_SKIN = "isSameSkin"
        const val COLUMN_HAIR = "isSameHair"
        const val COLUMN_START_AGE = "start_age"
        const val COLUMN_END_AGE = "end_age"
        const val COLUMN_START_HEIGHT = "start_height"
        const val COLUMN_END_HEIGHT = "end_height"
        const val COLUMN_SCORE = "score"
    }
}
