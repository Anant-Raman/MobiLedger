package com.example.mobiledger.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.usecases.AuthUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(private val authUseCase: AuthUseCase): BaseViewModel() {

    private val _isUserSignedInLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isUserSignedInLiveData: LiveData<Event<Boolean>> = _isUserSignedInLiveData

    private val splashTimeOut: Long = 3000

    fun isUserSignedIn(){
        viewModelScope.launch {
            when(authUseCase.getCurrentUser()){
                is AppResult.Success -> {
                    delay(splashTimeOut)
                    _isUserSignedInLiveData.value = Event(true)
                }
                is AppResult.Failure -> {
                    delay(splashTimeOut)
                    _isUserSignedInLiveData.value = Event(false)
                }
            }
        }
    }
}