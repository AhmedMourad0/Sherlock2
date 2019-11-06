package inc.ahmedmourad.sherlock.domain.model

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.exceptions.Exceptions
import org.reactivestreams.Publisher

sealed class Either<out V, out E : Throwable> {

    data class Value<V>(val value: V) : Either<V, Nothing>()
    data class Error<E : Throwable>(val error: E) : Either<Nothing, E>()

    inline fun <R> map(mapper: (V) -> R): Either<R, E> {
        return when (this) {
            is Error -> this
            is Value -> Value(mapper.invoke(this.value))
        }
    }

    companion object {
        val NULL = Value(null)
    }
}

fun <E : Throwable> E.asEither(): Either<Nothing, E> = Either.Error(this)

fun <V, E : Throwable, R> Flowable<Either<V, E>>.flatMapEither(mapper: (V) -> Publisher<out Either<R, E>>): Flowable<Either<R, E>> {
    return this.flatMap {
        when (it) {
            is Either.Value -> mapper.invoke(it.value)
            is Either.Error -> Flowable.just(it)
        }
    }
}

fun <V, E : Throwable, R> Single<Either<V, E>>.flatMapEither(mapper: (V) -> SingleSource<out Either<R, E>>): Single<Either<R, E>> {
    return this.flatMap {
        when (it) {
            is Either.Value -> mapper.invoke(it.value)
            is Either.Error -> Single.just(it)
        }
    }
}

fun <V, E : Throwable> Observable<Either<V, E>>.unwrapEither(): Observable<V> {
    return this.map {
        when (it) {
            is Either.Value -> it.value
            is Either.Error -> throw Exceptions.propagate(it.error)
        }
    }
}
