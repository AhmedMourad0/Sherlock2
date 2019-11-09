package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import io.reactivex.Flowable

typealias FindLastSearchResultsInteractor = () -> @JvmSuppressWildcards Flowable<List<Pair<DomainSimpleRetrievedChild, Int>>>

internal fun findLastSearchResults(childrenRepository: Lazy<ChildrenRepository>): Flowable<List<Pair<DomainSimpleRetrievedChild, Int>>> {
    return childrenRepository.get().findLastSearchResults()
}
