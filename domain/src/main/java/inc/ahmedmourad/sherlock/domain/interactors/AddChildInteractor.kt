package inc.ahmedmourad.sherlock.domain.interactors

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.model.DomainPublishedChild
import inc.ahmedmourad.sherlock.domain.model.DomainRetrievedChild
import io.reactivex.Single

typealias AddChildInteractor =
        (@JvmSuppressWildcards DomainPublishedChild) -> @JvmSuppressWildcards Single<DomainRetrievedChild>

internal fun addChild(childrenRepository: Lazy<ChildrenRepository>, child: DomainPublishedChild): Single<DomainRetrievedChild> {
    return childrenRepository.get().publish(child)
}
