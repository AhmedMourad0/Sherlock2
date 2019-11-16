package inc.ahmedmourad.sherlock.viewmodel.controllers

import androidx.lifecycle.ViewModel
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.extensions.tuple2.bifunctor.mapLeft
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.ChildrenFilterFactory
import inc.ahmedmourad.sherlock.domain.interactors.FindChildrenInteractor
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import inc.ahmedmourad.sherlock.mapper.toAppSimpleChild
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.model.AppSimpleRetrievedChild
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject

internal class SearchResultsViewModel(
        interactor: FindChildrenInteractor,
        filterFactory: ChildrenFilterFactory,
        rules: AppChildCriteriaRules
) : ViewModel() {

    private val refreshSubject = PublishSubject.create<Unit>()

    val searchResults: Flowable<Either<Throwable, List<Tuple2<AppSimpleRetrievedChild, Int>>>>

    init {

        val domainRules = rules.toDomainChildCriteriaRules()

        searchResults = interactor(domainRules, filterFactory(domainRules))
                .map { either ->
                    either.map {
                        it.map { resultTuple ->
                            resultTuple.mapLeft(DomainSimpleRetrievedChild::toAppSimpleChild)
                        }
                    }
                }.retryWhen { refreshSubject.toFlowable(BackpressureStrategy.LATEST) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun onRefresh() = refreshSubject.onNext(Unit)
}
