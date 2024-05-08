package otus.homework.flowcats

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CatsViewModel(
    private val catsRepository: CatsRepository
) : ViewModel() {

    private val _catsStateFlow: MutableStateFlow<Result> = MutableStateFlow(Success(Fact.NONE))
    val catsStateFlow: StateFlow<Result> = _catsStateFlow.asStateFlow()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _catsStateFlow.value = Error(throwable.message ?: throwable.toString())
    }
    init {
        viewModelScope.launch ( exceptionHandler){
            withContext(Dispatchers.IO ) {
                catsRepository.listenForCatFacts().collect {
                    _catsStateFlow.value = Success(it)
                }
            }
        }
    }
}

class CatsViewModelFactory(private val catsRepository: CatsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CatsViewModel(catsRepository) as T
}

sealed class Result
data class Success(val fact: Fact) : Result()
data class Error(val message: String) : Result()