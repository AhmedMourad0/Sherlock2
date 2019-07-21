package inc.ahmedmourad.sherlock.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FilterAbstractFactory
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FindChildrenInteractorAbstractFactory
import inc.ahmedmourad.sherlock.data.utils.toLiveData
import inc.ahmedmourad.sherlock.mapper.AppModelsMapper
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules
import inc.ahmedmourad.sherlock.model.AppUrlChild
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class SearchResultsViewModel(
        rules: AppChildCriteriaRules,
        interactor: FindChildrenInteractorAbstractFactory,
        filterFactory: FilterAbstractFactory) : ViewModel() {

    private val refreshSubject = PublishSubject.create<Unit>()

    val searchResults: LiveData<List<Pair<AppUrlChild, Int>>>

    init {

        val domainRules = AppModelsMapper.toDomainChildCriteriaRules(rules)

        searchResults = interactor.create(domainRules, filterFactory.create(domainRules))
                .execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.map { (child, score) -> AppModelsMapper.toAppChild(child) to score } }
                .retryWhen { refreshSubject.toFlowable(BackpressureStrategy.LATEST) }
                .toLiveData()
    }

    fun refresh() = refreshSubject.onNext(Unit)
}
