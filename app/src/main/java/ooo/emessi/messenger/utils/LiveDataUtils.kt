package ooo.emessi.messenger.utils

import androidx.lifecycle.MutableLiveData

fun <T> mutableLiveData(defaultValue: T? = null): MutableLiveData<T> {
    val data = MutableLiveData<T>()
    if (defaultValue != null) data.value = defaultValue
    return data
}