package inc.ahmedmourad.sherlock.viewmodel.controllers.children

import androidx.lifecycle.ViewModel
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.extensions.tuple2.bifunctor.mapLeft
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.ChildrenFilterFactory
import inc.ahmedmourad.sherlock.domain.interactors.children.FindChildrenInteractor
import inc.ahmedmourad.sherlock.domain.model.children.DomainSimpleRetrievedChild
import inc.ahmedmourad.sherlock.mapper.toAppSimpleChild
import inc.ahmedmourad.sherlock.model.children.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.model.children.AppSimpleRetrievedChild
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject

internal class ChildrenSearchResultsViewModel(
        interactor: FindChildrenInteractor,
        filterFactory: ChildrenFilterFactory,
        rules: AppChildCriteriaRules
) : ViewModel() {

    private val refreshSubject = PublishSubject.create<Unit>()

    val searchResultsFlowable: Flowable<Either<Throwable, List<Tuple2<AppSimpleRetrievedChild, Int>>>>

    init {

        val domainRules = rules.toDomainChildCriteriaRules()

        searchResultsFlowable = interactor(domainRules, filterFactory(domainRules))
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
