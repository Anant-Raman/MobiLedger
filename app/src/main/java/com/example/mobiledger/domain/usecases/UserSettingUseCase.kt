package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.UserSettingsRepository

interface UserSettingsUseCase {
    suspend fun getUID(): String?
    suspend fun saveUID(uid: String)
    suspend fun saveBiometricEnabled(isEnabled: Boolean)
    suspend fun isBiometricEnabled(): Boolean
    suspend fun saveNotificationEnabled(isEnabled: Boolean)
    suspend fun isNotificationEnabled(): Boolean
    suspend fun saveReminderEnabled(isEnabled: Boolean)
    suspend fun isReminderEnabled(): Boolean
    suspend fun clearDataOnLogout()
    suspend fun isTermsAndConditionAccepted(): Boolean
    suspend fun acceptTermsAndCondition(isAccepted: Boolean)
    suspend fun setIsFirstTimePermissionAsked(permissions: Array<String>)
    suspend fun isFirstTimePermissionAsked(permissions: Array<String>): Boolean
    suspend fun setAppUpdateAvailable(isAvailable: Boolean)
    suspend fun isAppUpdateAvailable(): Boolean
    suspend fun setForcedAppUpdateAvailable(isAvailable: Boolean)
    suspend fun isForcedAppUpdateAvailable(): Boolean
}

class UserSettingsUseCaseImpl(
    private val userSettingsRepository: UserSettingsRepository
) : UserSettingsUseCase {
    override suspend fun getUID(): String? {
        return userSettingsRepository.getUID()
    }

    override suspend fun saveUID(uid: String) {
        userSettingsRepository.saveUID(uid)
    }

    override suspend fun saveBiometricEnabled(isEnabled: Boolean) = userSettingsRepository.saveBiometricEnable(isEnabled)

    override suspend fun isBiometricEnabled(): Boolean = userSettingsRepository.isBiometricEnable()

    override suspend fun saveNotificationEnabled(isEnabled: Boolean) = userSettingsRepository.saveNotificationEnable(isEnabled)

    override suspend fun isNotificationEnabled(): Boolean = userSettingsRepository.isNotificationEnable()

    override suspend fun saveReminderEnabled(isEnabled: Boolean) = userSettingsRepository.saveReminderEnable(isEnabled)

    override suspend fun isReminderEnabled(): Boolean = userSettingsRepository.isReminderEnable()

    override suspend fun clearDataOnLogout() = userSettingsRepository.clearDataOnLogout()

    override suspend fun isTermsAndConditionAccepted(): Boolean = userSettingsRepository.isTermsAndConditionAccepted()

    override suspend fun acceptTermsAndCondition(isAccepted: Boolean) = userSettingsRepository.acceptTermsAndCondition(isAccepted)

    override suspend fun setIsFirstTimePermissionAsked(permissions: Array<String>) {
        userSettingsRepository.setIsFirstTimePermissionAsked(permissions)
    }

    override suspend fun isFirstTimePermissionAsked(permissions: Array<String>): Boolean {
        return userSettingsRepository.isFirstTimePermissionAsked(permissions)
    }

    override suspend fun setAppUpdateAvailable(isAvailable: Boolean) =  userSettingsRepository.setAppUpdateAvailable(isAvailable)

    override suspend fun isAppUpdateAvailable(): Boolean = userSettingsRepository.isAppUpdateAvailable()

    override suspend fun setForcedAppUpdateAvailable(isAvailable: Boolean) =  userSettingsRepository.setForcedAppUpdateAvailable(isAvailable)

    override suspend fun isForcedAppUpdateAvailable(): Boolean = userSettingsRepository.isForcedAppUpdateAvailable()
}