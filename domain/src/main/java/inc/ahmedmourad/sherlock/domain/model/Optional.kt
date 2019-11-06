package inc.ahmedmourad.sherlock.domain.model

private val NULL_OPTIONAL = Optional(null)

data class Optional<out T>(val value: T?) {
    inline fun <R> map(mapper: (T) -> R): Optional<R> {
        return if (value != null)
            mapper.invoke(this.value).asOptional()
        else
            null.asOptional()
    }
}

fun <T> T?.asOptional(): Optional<T> = if (this == null) NULL_OPTIONAL else Optional(this)
