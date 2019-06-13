package inc.ahmedmourad.sherlock.viewmodel.model

import androidx.lifecycle.MutableLiveData

class DefaultLiveData<T>(initialValue: T) : MutableLiveData<T>() {

    init {
        value = initialValue
    }

    override fun getValue(): T {
        return super.getValue() ?: throw IllegalStateException("Value cannot be null!")
    }
}
