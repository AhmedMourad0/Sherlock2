package inc.ahmedmourad.sherlock.domain.constants

interface ValuedEnum<V> {
    val value: V
}

enum class Gender(override val value: Int) : ValuedEnum<Int> {
    MALE(0), FEMALE(1);
}

enum class Hair(override val value: Int) : ValuedEnum<Int> {
    BLONDE(0), BROWN(1), DARK(2)
}

enum class Skin(override val value: Int) : ValuedEnum<Int> {
    WHITE(0), WHEAT(1), DARK(2)
}

inline fun <V, reified T : ValuedEnum<V>> findEnum(value: V, enumValues: Array<T>): T {

    for (item in enumValues)
        if (value == item.value)
            return item

    throw IllegalArgumentException("$value is not a valid value of type ${T::class.java.canonicalName}")
}
