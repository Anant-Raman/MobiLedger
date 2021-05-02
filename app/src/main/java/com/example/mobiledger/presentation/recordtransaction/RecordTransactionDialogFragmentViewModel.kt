package com.example.mobiledger.presentation.recordtransaction

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.usecases.TransactionUseCase
import com.example.mobiledger.presentation.Event
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class RecordTransactionDialogFragmentViewModel(private val transactionUseCase: TransactionUseCase) : BaseViewModel() {

    private var categoryList = arrayListOf<String>()

    val dataUpdatedResult: LiveData<Event<Unit>> get() = _dataUpdatedResult
    private val _dataUpdatedResult: MutableLiveData<Event<Unit>> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _loadingState = MutableLiveData<Boolean>(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    //todo : Fetch it from Firebase later
    fun provideCategoryList(): ArrayList<String> {
        categoryList.add("Rent")
        categoryList.add("Food")
        categoryList.add("Grocery")
        categoryList.add("Investment")
        categoryList.add("MISC")
        categoryList.add("Salary")
        categoryList.add("Bills")
        categoryList.add("Domestic Help")
        categoryList.add("Water")
        categoryList.add("Travel")

        return categoryList
    }

    fun addTransaction(
        monthYear: String, amount: Long, category: String, description: String,
        transactionTime: Timestamp, transactionType: String
    ) {
        viewModelScope.launch {
            _loadingState.value = true
            getMonthlyTransactionDetail(monthYear, amount, category, description, transactionTime, transactionType)
        }
    }

    private suspend fun getMonthlyTransactionDetail(
        monthYear: String, amount: Long, category: String, description: String,
        transactionTime: Timestamp, transactionType: String
    ) {
        when (val result = transactionUseCase.getMonthlyTransactionSummaryFromDb(monthYear)) {
            is AppResult.Success -> {
                val monthlySummaryEntity = result.data
                val transactionEntity = TransactionEntity(amount, category, description, transactionType, transactionTime)
                addTransactionToFireBase(monthYear, transactionEntity, monthlySummaryEntity)
            }
            is AppResult.Failure -> {
                _errorLiveData.value = Event(
                    ViewError(
                        viewErrorType = ViewErrorType.NON_BLOCKING,
                        message = result.error.message
                    )
                )
                _loadingState.value = false
            }
        }
    }

    private suspend fun addTransactionToFireBase(
        monthYear: String,
        transactionEntity: TransactionEntity,
        monthlySummaryEntity: MonthlyTransactionSummaryEntity?
    ) {
        when (val result = transactionUseCase.addUserTransactionToFirebase(monthYear, transactionEntity, monthlySummaryEntity)) {
            is AppResult.Success -> {
                _dataUpdatedResult.value = Event(result.data)
            }
            is AppResult.Failure -> {
                _errorLiveData.value = Event(
                    ViewError(
                        viewErrorType = ViewErrorType.NON_BLOCKING,
                        message = result.error.message
                    )
                )
            }
        }
        _loadingState.value = false
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )
}