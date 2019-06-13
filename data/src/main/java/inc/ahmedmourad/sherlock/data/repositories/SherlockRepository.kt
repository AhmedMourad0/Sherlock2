package inc.ahmedmourad.sherlock.data.repositories

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.DomainPictureChild
import inc.ahmedmourad.sherlock.domain.model.DomainRefreshableResults
import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import inc.ahmedmourad.sherlock.domain.repository.Repository
import io.reactivex.Flowable
import io.reactivex.Single

class SherlockRepository(private val localRepository: Lazy<LocalRepository>, private val cloudRepository: Lazy<CloudRepository>) : Repository {

    override fun publish(domainChild: DomainPictureChild): Single<DomainUrlChild> {
        return cloudRepository.get().publish(domainChild)
    }

    override fun find(rules: DomainChildCriteriaRules, filter: Filter<DomainUrlChild>) = DomainRefreshableResults(localRepository.get().getResults()) {
        cloudRepository.get().find(rules, filter).flatMapCompletable { localRepository.get().replaceResults(it) }
    }

    override fun getLastSearchResults(): Flowable<List<Pair<DomainUrlChild, Int>>> {
        return localRepository.get().getResults()
    }
}
