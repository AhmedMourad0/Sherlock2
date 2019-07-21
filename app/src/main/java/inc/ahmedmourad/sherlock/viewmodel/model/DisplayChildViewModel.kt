package inc.ahmedmourad.sherlock.viewmodel.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import inc.ahmedmourad.sherlock.dagger.modules.domain.factories.FindChildInteractorAbstractFactory
import inc.ahmedmourad.sherlock.data.utils.toLiveData
import inc.ahmedmourad.sherlock.domain.model.Optional
import inc.ahmedmourad.sherlock.mapper.AppModelsMapper
import inc.ahmedmourad.sherlock.model.AppUrlChild
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class DisplayChildViewModel(childId: String, interactor: FindChildInteractorAbstractFactory) : ViewModel() {

    private val refreshSubject = PublishSubject.create<Unit>()

    val result: LiveData<Optional<Pair<AppUrlChild, Int>>>

    init {
        result = interactor.create(childId).execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.map { (child, score) -> AppModelsMapper.toAppChild(child) to score } }
                .retryWhen { refreshSubject.toFlowable(BackpressureStrategy.LATEST) }
                .toLiveData()
    }

    fun refresh() = refreshSubject.onNext(Unit)
}
