package com.example.mobiledger.presentation.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.BuildConfig
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.usecases.ConfigUseCase
import com.example.mobiledger.domain.usecases.UserSettingsUseCase
import com.example.mobiledger.presentation.Event
import com.example.mobiledger.presentation.home.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val userSettingsUseCase: UserSettingsUseCase,
    private val configUseCase: ConfigUseCase
) : BaseViewModel() {

    private val _isUserSignedInLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isUserSignedInLiveData: LiveData<Event<Boolean>> = _isUserSignedInLiveData

    private val _isBiometricEnabled = MutableLiveData<Event<Boolean>>()
    val isBiometricIdEnabled: LiveData<Event<Boolean>> get() = _isBiometricEnabled

    private val _isTermsAndConditionAcceptedLiveData = MutableLiveData<Event<Boolean>>()
    val isTermsAndConditionAcceptedLiveData: LiveData<Event<Boolean>> get() = _isTermsAndConditionAcceptedLiveData

    private val _errorLiveData: MutableLiveData<Event<HomeViewModel.ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<HomeViewModel.ViewError>> = _errorLiveData

    private val splashTimeOut: Long = 3000

    init {
        isTermsAncConditionAccepted()
        getConfigDetails()
    }

    private fun getConfigDetails() {
        viewModelScope.launch {
            when (val result = configUseCase.getConfigDetails()) {
                is AppResult.Success -> {
                    val latestVersion = result.data?.latestVersion ?: ""
                    Log.i("Anant splash vm", result.data?.isForceUpdate.toString() )
                    if (latestVersion > BuildConfig.VERSION_NAME) {
                        if (result.data?.isForceUpdate == true) {
                            Log.i("Anant splash vm", "force upd" )
                            userSettingsUseCase.setForcedAppUpdateAvailable(true)
                            userSettingsUseCase.setAppUpdateAvailable(false)
                        } else {
                            userSettingsUseCase.setAppUpdateAvailable(true)
                            userSettingsUseCase.setForcedAppUpdateAvailable(false)
                        }
                    }else{
                        userSettingsUseCase.setAppUpdateAvailable(false)
                        userSettingsUseCase.setForcedAppUpdateAvailable(false)
                    }
                }

                is AppResult.Failure -> {
                    if (needToHandleAppError(result.error)) {
                        _errorLiveData.value = Event(
                            HomeViewModel.ViewError(
                                viewErrorType = HomeViewModel.ViewErrorType.NON_BLOCKING,
                                message = result.error.message
                            )
                        )
                    }
                }
            }
        }
    }

    private fun isTermsAncConditionAccepted() {
        viewModelScope.launch {
            val tAndCStatus = userSettingsUseCase.isTermsAndConditionAccepted()
            delay(splashTimeOut)
            _isTermsAndConditionAcceptedLiveData.value = Event(tAndCStatus)
        }
    }

    fun isUserSignedIn() {
        viewModelScope.launch {
            val uID = userSettingsUseCase.getUID()
//            delay(splashTimeOut)
            if (uID != null) _isUserSignedInLiveData.value = Event(true)
            else _isUserSignedInLiveData.value = Event(false)
        }
    }

    fun isBiometricEnabled() {
        viewModelScope.launch {
            val isBiometricActive = userSettingsUseCase.isBiometricEnabled()
            _isBiometricEnabled.value = Event(isBiometricActive)
        }
    }
}