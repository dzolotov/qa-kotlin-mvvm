package tech.dzolotov.counterappmvvm

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DescriptionResult {
    data class Success(val text: String) : DescriptionResult()
    class Error() : DescriptionResult()
    class Loading() : DescriptionResult()
}

@HiltViewModel
class CounterViewModel @Inject constructor(private val descriptionRepository: IDescriptionRepository): ViewModel() {
    private val _counter = MutableLiveData<Int?>(null)      //счетчик
    fun getCounter(): LiveData<Int?> = _counter                   //доступ только на чтение

    //используем ленивую инициализацию для загрузки данных однократно (при первом обращении)
    private val _description by lazy {
        val liveData = MutableLiveData<DescriptionResult?>(null)
        loadData(liveData)
        return@lazy liveData
    }
    fun getDescription(): LiveData<DescriptionResult?> = _description

    fun overrideScope(scope: CoroutineScope) {
        val tags = ViewModel::class.java.getDeclaredField("mBagOfTags")
        tags.isAccessible = true
        val tagsValue = tags.get(this) as HashMap<String, Any>
        tagsValue["androidx.lifecycle.ViewModelCoroutineScope.JOB_KEY"] = scope as Any
    }

    fun increment() {
        _counter.value = (_counter.value?.inc() ?: 1)
    }

    //здесь дополнительно принимаем объект MutableLiveData, поскольку _description еще не инициализирован
    fun loadData(liveData: MutableLiveData<DescriptionResult?>) {
        viewModelScope.launch {
            liveData.postValue(DescriptionResult.Loading())
            delay(2000)
            liveData.postValue(DescriptionResult.Success(descriptionRepository.getDescription()))
        }
    }
}