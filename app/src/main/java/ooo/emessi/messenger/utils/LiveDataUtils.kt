package ooo.emessi.messenger.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import ooo.emessi.messenger.utils.helpers.TransformedLiveData

fun <T> mutableLiveData(defaultValue: T? = null): MutableLiveData<T> {
    val data = MutableLiveData<T>()
    if (defaultValue != null) data.value = defaultValue
    return data
}

fun <Source, Output> LiveData<Source>.transform(scope: CoroutineScope,
    transformation: (Source?) -> Output?) = TransformedLiveData(scope, this, transformation)