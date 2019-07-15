package inc.ahmedmourad.sherlock.utils

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import inc.ahmedmourad.sherlock.domain.repository.Repository
import io.reactivex.disposables.Disposable

class SearchResultsIdlingResource(repository: Repository) : IdlingResource {

    @Volatile
    private var resourceCallback: ResourceCallback? = null

    private var disposable: Disposable? = null
        set(value) {
            field?.dispose()
            field = value
        }

    private var isIdle = true

    init {
        disposable = repository.getLastSearchResults().subscribe({
            isIdle = true
        }, {
            it.printStackTrace()
            isIdle = true
        })
    }

    fun onStart() {
        isIdle = false
    }

    override fun getName(): String {
        return SearchResultsIdlingResource::class.java.name
    }

    override fun isIdleNow(): Boolean {
        return isIdle.also {
            if (it)
                resourceCallback?.onTransitionToIdle()
        }
    }

    override fun registerIdleTransitionCallback(resourceCallback: ResourceCallback) {
        this.resourceCallback = resourceCallback
    }

    fun dispose() {
        isIdle = true
        disposable?.dispose()
    }
}
