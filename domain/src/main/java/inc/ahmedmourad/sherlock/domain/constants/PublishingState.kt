package inc.ahmedmourad.sherlock.domain.constants

import inc.ahmedmourad.sherlock.domain.model.children.DomainPublishedChild
import inc.ahmedmourad.sherlock.domain.model.children.DomainRetrievedChild

sealed class PublishingState {
    data class Success(val child: DomainRetrievedChild) : PublishingState()
    data class Ongoing(val child: DomainPublishedChild) : PublishingState()
    data class Failure(val child: DomainPublishedChild) : PublishingState()
}
