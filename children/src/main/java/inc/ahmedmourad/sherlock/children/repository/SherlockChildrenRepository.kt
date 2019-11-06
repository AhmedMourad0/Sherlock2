package inc.ahmedmourad.sherlock.children.repository

import dagger.Lazy
import inc.ahmedmourad.sherlock.children.repository.dependencies.ChildrenLocalRepository
import inc.ahmedmourad.sherlock.children.repository.dependencies.ChildrenRemoteRepository
import inc.ahmedmourad.sherlock.domain.bus.Bus
import inc.ahmedmourad.sherlock.domain.data.ChildrenRepository
import inc.ahmedmourad.sherlock.domain.filter.Filter
import inc.ahmedmourad.sherlock.domain.filter.criteria.DomainChildCriteriaRules
import inc.ahmedmourad.sherlock.domain.model.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

//TODO: if requireUserSignedIn doesn't fail and user is not signed in with FirebaseAuth
// implement a fallback mechanism and sign the user in anonymously
internal class SherlockChildrenRepository(
        private val childrenLocalRepository: Lazy<ChildrenLocalRepository>,
        private val childrenRemoteRepository: Lazy<ChildrenRemoteRepository>,
        private val bus: Lazy<Bus>
) : ChildrenRepository {

    private val tester by lazy { SherlockTester(childrenRemoteRepository, childrenLocalRepository) }

    override fun publish(child: DomainPublishedChild): Single<DomainRetrievedChild> {
        return childrenRemoteRepository.get()
                .publish(child)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSuccess { bus.get().childPublishingState.accept(Bus.PublishingState.Success(it)) }
                .doOnSubscribe { bus.get().childPublishingState.accept(Bus.PublishingState.Ongoing(child)) }
                .doOnError { bus.get().childPublishingState.accept(Bus.PublishingState.Failure(child)) }
    }

    override fun find(child: DomainSimpleRetrievedChild): Flowable<Either<Pair<DomainRetrievedChild, Int>?, Throwable>> {
        return childrenRemoteRepository.get()
                .find(child)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapEither { retrievedChild ->

                    if (retrievedChild == null)
                        Flowable.just(Either.NULL)
                    else
                        childrenLocalRepository.get()
                                .updateIfExists(retrievedChild)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .map { Either.Value(it) }
                                .toSingle(Either.Value(retrievedChild to -1))
                                .toFlowable()

                }.doOnSubscribe { bus.get().childFindingState.accept(Bus.BackgroundState.ONGOING) }
                .doOnNext { bus.get().childFindingState.accept(Bus.BackgroundState.SUCCESS) }
                .doOnError { bus.get().childFindingState.accept(Bus.BackgroundState.FAILURE) }
    }

    override fun findAll(rules: DomainChildCriteriaRules, filter: Filter<DomainRetrievedChild>): Flowable<Either<List<Pair<DomainSimpleRetrievedChild, Int>>, Throwable>> {
        return childrenRemoteRepository.get()
                .findAll(rules, filter)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapEither { results ->

                    childrenLocalRepository.get()
                            .replaceAll(results)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .map { Either.Value(it) }
                            .toFlowable()

                }.doOnSubscribe { bus.get().childrenFindingState.accept(Bus.BackgroundState.ONGOING) }
                .doOnNext { bus.get().childrenFindingState.accept(Bus.BackgroundState.SUCCESS) }
                .doOnError { bus.get().childrenFindingState.accept(Bus.BackgroundState.FAILURE) }
    }

    override fun findLastSearchResults(): Flowable<List<Pair<DomainSimpleRetrievedChild, Int>>> {
        return childrenLocalRepository.get()
                .findAll()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
    }

    override fun test(): ChildrenRepository.Tester {
        return tester
    }

    class SherlockTester(
            private val childrenRemoteRepository: Lazy<ChildrenRemoteRepository>,
            private val childrenLocalRepository: Lazy<ChildrenLocalRepository>
    ) : ChildrenRepository.Tester {
        override fun clear(): Completable {
            return childrenRemoteRepository.get()
                    .clear()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .andThen(childrenLocalRepository.get().clear())
        }
    }
}
