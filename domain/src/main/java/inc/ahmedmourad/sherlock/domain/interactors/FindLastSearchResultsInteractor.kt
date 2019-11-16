package inc.ahmedmourad.sherlock.domain.interactors

import arrow.core.Tuple2
import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.model.DomainSimpleRetrievedChild
import io.reactivex.Flowable

typealias FindLastSearchResultsInteractor = () -> @JvmSuppressWildcards Flowable<List<Tuple2<DomainSimpleRetrievedChild, Int>>>

internal fun findLastSearchResults(
        childrenRepository: Lazy<ChildrenRepository>
): Flowable<List<Tuple2<DomainSimpleRetrievedChild, Int>>> {
    return childrenRepository.get().findLastSearchResults()
}
