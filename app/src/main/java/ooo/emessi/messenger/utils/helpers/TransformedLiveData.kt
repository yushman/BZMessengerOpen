package ooo.emessi.messenger.utils.helpers

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.*

class TransformedLiveData<Source, Output>(
    private val scope: CoroutineScope,
    private val source: LiveData<Source>,
    private val transformation: (Source?) -> Output?)
    : LiveData<Output>(){
    private val context = Dispatchers.IO
    private var job: Job? = null

    private val observer = Observer<Source> { source ->
        job?.cancel()
        job = scope.launch {
            withContext(Dispatchers.IO){
                transformation(source)?.let { transformed ->
                    // Could have used postValue instead, but using the UI context I can guarantee that
                    // a canceled job will never emit values.
                    withContext(Dispatchers.Main) {
                        value = transformed
                    }
                }
            }
        }
    }

    override fun onActive() {
        source.observeForever(observer)
    }

    override fun onInactive() {
        job?.cancel()
        source.removeObserver(observer)
    }
}