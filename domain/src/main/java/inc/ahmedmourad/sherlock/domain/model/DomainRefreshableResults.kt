package inc.ahmedmourad.sherlock.domain.model

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

//TODO: we need to get rid of this
data class DomainRefreshableResults(
        val results: Flowable<List<Pair<DomainUrlChild, Int>>>,
        private val refresh: () -> Completable) {

    private val disposable: Disposable

    init {
        //TODO: error mechanism here
        disposable = refresh().subscribe({ }, { it.printStackTrace() })
    }

    fun refresh(): Completable = refresh.invoke().subscribeOn(Schedulers.io())

    fun clear() {
        disposable.dispose()
    }
}
