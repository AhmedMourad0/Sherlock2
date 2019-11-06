package inc.ahmedmourad.sherlock.viewmodel.model

import androidx.lifecycle.MutableLiveData

internal class DefaultLiveData<T : Any>(initialValue: T) : MutableLiveData<T>() {

    init {
        value = initialValue
    }

    override fun getValue(): T {
        return checkNotNull(super.getValue())
    }
}
