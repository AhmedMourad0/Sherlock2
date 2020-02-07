package inc.ahmedmourad.sherlock.children.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import arrow.core.*
import arrow.core.extensions.fx
import inc.ahmedmourad.sherlock.children.local.contract.Contract.ChildrenEntry
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.domain.constants.findEnum
import inc.ahmedmourad.sherlock.domain.exceptions.ModelConversionException
import inc.ahmedmourad.sherlock.domain.model.children.*
import timber.log.Timber

//TODO: maybe move weight to a different table
@Entity(tableName = ChildrenEntry.TABLE_NAME)
internal data class RoomChildEntity(

        @PrimaryKey
        @ColumnInfo(name = ChildrenEntry.COLUMN_ID)
        val id: String,

        @ColumnInfo(name = ChildrenEntry.COLUMN_PUBLICATION_DATE)
        val publicationDate: Long,

        @ColumnInfo(name = ChildrenEntry.COLUMN_FIRST_NAME)
        val firstName: String?,

        @ColumnInfo(name = ChildrenEntry.COLUMN_LAST_NAME)
        val lastName: String?,

        @ColumnInfo(name = ChildrenEntry.COLUMN_LOCATION_ID)
        val locationId: String?,

        @ColumnInfo(name = ChildrenEntry.COLUMN_LOCATION_NAME)
        val locationName: String?,

        @ColumnInfo(name = ChildrenEntry.COLUMN_LOCATION_ADDRESS)
        val locationAddress: String?,

        @ColumnInfo(name = ChildrenEntry.COLUMN_LOCATION_LATITUDE)
        val locationLatitude: Double?,

        @ColumnInfo(name = ChildrenEntry.COLUMN_LOCATION_LONGITUDE)
        val locationLongitude: Double?,

        @ColumnInfo(name = ChildrenEntry.COLUMN_NOTES)
        val notes: String?,

        @ColumnInfo(name = ChildrenEntry.COLUMN_GENDER)
        val gender: Int?,

        @ColumnInfo(name = ChildrenEntry.COLUMN_SKIN)
        val skin: Int?,

        @ColumnInfo(name = ChildrenEntry.COLUMN_HAIR)
        val hair: Int?,

        @ColumnInfo(name = ChildrenEntry.COLUMN_MIN_AGE)
        val minAge: Int?,

        @ColumnInfo(name = ChildrenEntry.COLUMN_MAX_AGE)
        val maxAge: Int?,

        @ColumnInfo(name = ChildrenEntry.COLUMN_MIN_HEIGHT)
        val minHeight: Int?,

        @ColumnInfo(name = ChildrenEntry.COLUMN_MAX_HEIGHT)
        val maxHeight: Int?,

        @ColumnInfo(name = ChildrenEntry.COLUMN_PICTURE_URL)
        val pictureUrl: String?,

        @ColumnInfo(name = ChildrenEntry.COLUMN_WEIGHT)
        val weight: Double?
) {

    fun toRetrievedChild(): Either<Throwable, Tuple2<RetrievedChild, Weight?>> {

        val name = extractName().getOrHandle {
            Timber.e(it)
            null
        }

        val location = extractLocation().getOrHandle {
            Timber.e(it)
            null
        }

        val url = pictureUrl?.let(Url.Companion::of)
                ?.mapLeft { ModelConversionException(it.toString()) }
                ?.getOrHandle {
                    Timber.e(it)
                    null
                }

        val weight = this@RoomChildEntity.weight?.let(Weight.Companion::of)
                ?.mapLeft { ModelConversionException(it.toString()) }
                ?.getOrHandle {
                    Timber.e(it)
                    null
                }

        return Either.fx {

            val (appearance) = extractApproximateAppearance()

            val (child) = RetrievedChild.of(
                    ChildId(id),
                    publicationDate,
                    name,
                    notes,
                    location,
                    appearance,
                    url
            ).bimap(
                    leftOperation = { ModelConversionException(it.toString()) },
                    rightOperation = { it toT weight }
            )

            return@fx child
        }
    }

    fun simplify(): Either<Throwable, Tuple2<SimpleRetrievedChild, Weight?>> {

        val name = extractName().getOrHandle {
            Timber.e(it)
            null
        }

        val url = pictureUrl?.let(Url.Companion::of)
                ?.mapLeft { ModelConversionException(it.toString()) }
                ?.getOrHandle {
                    Timber.e(it)
                    null
                }

        val weight = this@RoomChildEntity.weight?.let(Weight.Companion::of)
                ?.mapLeft { ModelConversionException(it.toString()) }
                ?.getOrHandle {
                    Timber.e(it)
                    null
                }

        return SimpleRetrievedChild.of(
                ChildId(id),
                publicationDate,
                name,
                notes,
                locationName,
                locationAddress,
                url
        ).bimap(
                leftOperation = { ModelConversionException(it.toString()) },
                rightOperation = { it toT weight }
        )
    }

    private fun extractName(): Either<Throwable, Either<Name, FullName>?> {
        return Either.fx {

            firstName ?: return@fx null

            val (first) = Name.of(firstName).mapLeft { ModelConversionException(it.toString()) }

            lastName ?: return@fx first.left()

            val (last) = Name.of(lastName).mapLeft { ModelConversionException(it.toString()) }

            val (fullName) = FullName.of(first, last).mapLeft { ModelConversionException(it.toString()) }

            return@fx fullName.right()
        }
    }

    private fun extractApproximateAppearance(): Either<Throwable, ApproximateAppearance> {

        val gender = gender?.let { findEnum(it, Gender.values()) }
        val skin = skin?.let { findEnum(it, Skin.values()) }
        val hair = hair?.let { findEnum(it, Hair.values()) }

        val ageRange = extractAgeRange().getOrHandle {
            Timber.e(it)
            null
        }

        val heightRange = extractHeightRange().getOrHandle {
            Timber.e(it)
            null
        }

        return ApproximateAppearance.of(
                gender,
                skin,
                hair,
                ageRange,
                heightRange
        ).mapLeft { ModelConversionException(it.toString()) }
    }

    private fun extractAgeRange(): Either<Throwable, AgeRange?> {
        return Either.fx {

            minAge ?: return@fx null
            maxAge ?: return@fx null

            val (min) = Age.of(minAge).mapLeft { ModelConversionException(it.toString()) }
            val (max) = Age.of(maxAge).mapLeft { ModelConversionException(it.toString()) }

            val (ageRange) = AgeRange.of(min, max).mapLeft { ModelConversionException(it.toString()) }

            return@fx ageRange
        }
    }

    private fun extractHeightRange(): Either<Throwable, HeightRange?> {
        return Either.fx {

            minHeight ?: return@fx null
            maxHeight ?: return@fx null

            val (min) = Height.of(minHeight).mapLeft { ModelConversionException(it.toString()) }
            val (max) = Height.of(maxHeight).mapLeft { ModelConversionException(it.toString()) }

            val (heightRange) = HeightRange.of(min, max).mapLeft { ModelConversionException(it.toString()) }

            return@fx heightRange
        }
    }

    private fun extractLocation(): Either<Throwable, Location?> {
        return Either.fx {

            val id = locationId ?: return@fx null
            val name = locationName ?: return@fx null
            val address = locationAddress ?: return@fx null

            val (coordinates) = extractCoordinates()

            coordinates ?: return@fx null

            val (location) = Location.of(
                    id,
                    name,
                    address,
                    coordinates
            ).mapLeft { ModelConversionException(it.toString()) }

            return@fx location
        }
    }

    private fun extractCoordinates(): Either<Throwable, Coordinates?> {

        val latitude = this.locationLatitude ?: return null.right()
        val longitude = this.locationLongitude ?: return null.right()

        return Coordinates.of(latitude, longitude).mapLeft { ModelConversionException(it.toString()) }
    }
}
