package inc.ahmedmourad.sherlock.viewmodel.controllers

import androidx.lifecycle.ViewModel
import arrow.core.Either
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.tuple2.bifunctor.mapLeft
import inc.ahmedmourad.sherlock.domain.interactors.FindChildInteractor
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import inc.ahmedmourad.sherlock.mapper.toAppChild
import inc.ahmedmourad.sherlock.model.AppRetrievedChild
import inc.ahmedmourad.sherlock.model.AppSimpleRetrievedChild
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject

internal class DisplayChildViewModel(child: AppSimpleRetrievedChild, interactor: FindChildInteractor) : ViewModel() {

    private val refreshSubject = PublishSubject.create<Unit>()

    val result: Flowable<Either<Throwable, Option<Tuple2<AppRetrievedChild, Int>>>>

    init {
        result = interactor(child.toDomainSimpleChild())
                .map { resultEither ->
                    resultEither.map { resultOption ->
                        resultOption.map { resultTuple ->
                            resultTuple.mapLeft(DomainRetrievedChild::toAppChild)
                        }
                    }
                }.retryWhen { refreshSubject.toFlowable(BackpressureStrategy.LATEST) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun onRefresh() = refreshSubject.onNext(Unit)
}
