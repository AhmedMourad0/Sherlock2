package inc.ahmedmourad.sherlock.domain.constants

import inc.ahmedmourad.sherlock.domain.platform.TextManager

interface ValuedEnum<V> {
    val value: V
}

interface PresentableEnum {
    fun getMessage(textManager: TextManager): String
}

enum class Gender(override val value: Int, private val getMessage: (TextManager) -> String) : ValuedEnum<Int>, PresentableEnum {
    MALE(0, { it.male() }),
    FEMALE(1, { it.female() });

    override fun getMessage(textManager: TextManager) = getMessage.invoke(textManager)
}

enum class Hair(override val value: Int, private val getMessage: (TextManager) -> String) : ValuedEnum<Int>, PresentableEnum {
    BLONDE(0, { it.blondeHair() }),
    BROWN(1, { it.brownHair() }),
    DARK(2, { it.darkHair() });

    override fun getMessage(textManager: TextManager) = getMessage.invoke(textManager)
}

enum class Skin(override val value: Int, private val getMessage: (TextManager) -> String) : ValuedEnum<Int>, PresentableEnum {
    WHITE(0, { it.whiteSkin() }),
    WHEAT(1, { it.wheatishSkin() }),
    DARK(2, { it.darkSkin() });

    override fun getMessage(textManager: TextManager) = getMessage.invoke(textManager)
}

inline fun <V, reified T : ValuedEnum<V>> findEnum(value: V, enumValues: Array<T>): T {

    for (item in enumValues)
        if (value == item.value)
            return item

    throw IllegalArgumentException("$value is not a valid error of type ${T::class.java.canonicalName}")
}
