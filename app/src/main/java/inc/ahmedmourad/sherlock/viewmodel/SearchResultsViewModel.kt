package inc.ahmedmourad.sherlock.viewmodel

import androidx.lifecycle.ViewModel
import inc.ahmedmourad.sherlock.dagger.SherlockComponent
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FilterFactory
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FindChildrenInteractorAbstractFactory
import inc.ahmedmourad.sherlock.data.utils.toLiveData
import inc.ahmedmourad.sherlock.domain.model.DomainRefreshableResults
import inc.ahmedmourad.sherlock.mapper.AppModelsMapper
import inc.ahmedmourad.sherlock.model.AppChildCriteriaRules
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SearchResultsViewModel(rules: AppChildCriteriaRules, interactor: FindChildrenInteractorAbstractFactory) : ViewModel() {

    @Inject
    lateinit var filterFactory: FilterFactory

    private val refreshableResults: DomainRefreshableResults

    init {
        SherlockComponent.ViewModels.searchResultsComponent.get().inject(this)
        with(AppModelsMapper.toDomainChildCriteriaRules(rules)) {
            refreshableResults = interactor.create(this, filterFactory.create(this)).execute()
        }
    }

    //TODO: might need to pass the whole pair one day
    val searchResults = refreshableResults.results
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.map { (first) -> first }.map(AppModelsMapper::toAppChild) }
            .toLiveData()

    private fun refresh(): Completable {
        return refreshableResults.refresh()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun onCleared() {
        refreshableResults.clear()
        SherlockComponent.ViewModels.searchResultsComponent.release()
        super.onCleared()
    }
}
