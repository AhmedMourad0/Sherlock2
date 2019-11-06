package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import io.reactivex.Flowable

internal class FindLastSearchResultsInteractor(private val childrenRepository: Lazy<ChildrenRepository>) : Interactor<Flowable<List<Pair<DomainSimpleRetrievedChild, Int>>>> {
    override fun execute(): Flowable<List<Pair<DomainSimpleRetrievedChild, Int>>> {
        return childrenRepository.get().findLastSearchResults()
    }
}
