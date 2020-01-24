package inc.ahmedmourad.sherlock.viewmodel.controllers.children

import androidx.lifecycle.ViewModel
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.extensions.tuple2.bifunctor.mapLeft
import inc.ahmedmourad.sherlock.domain.interactors.children.FindChildInteractor
import inc.ahmedmourad.sherlock.domain.model.children.DomainRetrievedChild
import inc.ahmedmourad.sherlock.mapper.toAppChild
import inc.ahmedmourad.sherlock.model.children.AppRetrievedChild
import inc.ahmedmourad.sherlock.model.children.AppSimpleRetrievedChild
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject

internal class ChildDetailsViewModel(child: AppSimpleRetrievedChild, interactor: FindChildInteractor) : ViewModel() {

    private val refreshSubject = PublishSubject.create<Unit>()

    val result: Flowable<Either<Throwable, Tuple2<AppRetrievedChild, Int?>?>>

    init {
        result = interactor(child.toDomainSimpleChild())
                .map { resultEither ->
                    resultEither.map { result ->
                        result?.mapLeft(DomainRetrievedChild::toAppChild)
                    }
                }.retryWhen { refreshSubject.toFlowable(BackpressureStrategy.LATEST) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun onRefresh() = refreshSubject.onNext(Unit)
}
