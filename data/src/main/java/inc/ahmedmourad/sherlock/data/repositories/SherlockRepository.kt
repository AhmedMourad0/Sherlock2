package inc.ahmedmourad.sherlock.data.repositories

import dagger.Lazy
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.DomainPictureChild
import inc.ahmedmourad.sherlock.domain.model.DomainUrlChild
import inc.ahmedmourad.sherlock.domain.model.Optional
import inc.ahmedmourad.sherlock.domain.model.asOptional
import inc.ahmedmourad.sherlock.domain.repository.Repository
import io.reactivex.Flowable
import io.reactivex.Single

class SherlockRepository(private val localRepository: Lazy<LocalRepository>, private val cloudRepository: Lazy<CloudRepository>) : Repository {

    override fun publish(domainChild: DomainPictureChild): Single<DomainUrlChild> {
        return cloudRepository.get().publish(domainChild)
    }

    override fun find(childId: String): Flowable<Optional<Pair<DomainUrlChild, Int>>> {
        return cloudRepository.get()
                .find(childId)
                .flatMap {

                    val (value) = it

                    if (value != null)
                        localRepository.get().updateIfExists(value).toFlowable()
                    else
                        Flowable.just(null.asOptional())
                }
    }

    override fun findAll(rules: DomainChildCriteriaRules, filter: Filter<DomainUrlChild>): Flowable<List<Pair<DomainUrlChild, Int>>> {
        return cloudRepository.get()
                .findAll(rules, filter)
                .flatMap { localRepository.get().replaceAll(it).andThen(Flowable.just(it)) }
    }

    override fun getLastSearchResults(): Flowable<List<Pair<DomainUrlChild, Int>>> {
        return localRepository.get().findAll()
    }
}
