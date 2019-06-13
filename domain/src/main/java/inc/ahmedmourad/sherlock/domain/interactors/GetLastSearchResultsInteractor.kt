package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import inc.ahmedmourad.sherlock.domain.repository.Repository
import io.reactivex.Flowable

class GetLastSearchResultsInteractor(private val repository: Lazy<Repository>) : Interactor<Flowable<List<Pair<DomainUrlChild, Int>>>> {
    override fun execute(): Flowable<List<Pair<DomainUrlChild, Int>>> {
        return repository.get().getLastSearchResults()
    }
}
