package inc.ahmedmourad.sherlock.viewmodel.controllers

import androidx.lifecycle.ViewModel
import inc.ahmedmourad.sherlock.domain.dagger.modules.factories.ChildrenFilterFactory
import inc.ahmedmourad.sherlock.domain.interactors.FindChildrenInteractor
import inc.ahmedmourad.sherlock.domain.model.Either
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

    val searchResults: Flowable<Either<List<Pair<AppSimpleRetrievedChild, Int>>, Throwable>>

    init {

        val domainRules = rules.toDomainChildCriteriaRules()

        searchResults = interactor(domainRules, filterFactory(domainRules))
                .map { either ->
                    either.map {
                        it.map { (child, score) ->
                            child.toAppSimpleChild() to score
                        }
                    }
                }.retryWhen { refreshSubject.toFlowable(BackpressureStrategy.LATEST) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun onRefresh() = refreshSubject.onNext(Unit)
}
