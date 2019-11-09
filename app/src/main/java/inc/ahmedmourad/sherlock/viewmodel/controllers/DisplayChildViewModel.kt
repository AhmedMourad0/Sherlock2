package inc.ahmedmourad.sherlock.viewmodel.controllers

import androidx.lifecycle.ViewModel
import inc.ahmedmourad.sherlock.domain.interactors.FindChildInteractor
import inc.ahmedmourad.sherlock.domain.model.Either
import inc.ahmedmourad.sherlock.mapper.toAppChild
import inc.ahmedmourad.sherlock.model.AppRetrievedChild
import inc.ahmedmourad.sherlock.model.AppSimpleRetrievedChild
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject

internal class DisplayChildViewModel(child: AppSimpleRetrievedChild, interactor: FindChildInteractor) : ViewModel() {

    private val refreshSubject = PublishSubject.create<Unit>()

    val result: Flowable<Either<Pair<AppRetrievedChild, Int>?, Throwable>>

    init {
        result = interactor(child.toDomainSimpleChild())
                .map { either ->
                    either.map {
                        if (it != null)
                            it.first.toAppChild() to it.second
                        else
                            null
                    }
                }.retryWhen { refreshSubject.toFlowable(BackpressureStrategy.LATEST) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun onRefresh() = refreshSubject.onNext(Unit)
}
