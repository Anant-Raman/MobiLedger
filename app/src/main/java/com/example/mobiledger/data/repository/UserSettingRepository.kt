package com.example.mobiledger.data.repository

import com.example.mobiledger.data.sources.cache.CacheSource

interface UserSettingsRepository {
    suspend fun getUID(): String?
    suspend fun saveUID(uid: String)
    suspend fun saveBiometricEnable(isEnable: Boolean)
    suspend fun isBiometricEnable(): Boolean
    suspend fun saveNotificationEnable(isEnable: Boolean)
    suspend fun isNotificationEnable(): Boolean
    suspend fun saveReminderEnable(isEnable: Boolean)
    suspend fun isReminderEnable(): Boolean
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

class UserSettingsRepositoryImpl(private val cacheSource: CacheSource) : UserSettingsRepository {

    override suspend fun getUID(): String? {
        return cacheSource.getUID()
    }

    override suspend fun saveUID(uid: String) {
        cacheSource.saveUid(uid)
    }

    override suspend fun saveBiometricEnable(isEnable: Boolean) {
        cacheSource.saveBiometricEnable(isEnable)
    }

    override suspend fun isBiometricEnable(): Boolean {
        return cacheSource.isBiometricEnable()
    }

    override suspend fun saveNotificationEnable(isEnable: Boolean) {
        cacheSource.saveNotificationEnable(isEnable)
    }

    override suspend fun isNotificationEnable(): Boolean {
        return cacheSource.isNotificationEnable()
    }

    override suspend fun saveReminderEnable(isEnable: Boolean) {
        cacheSource.saveReminderEnable(isEnable)
    }

    override suspend fun isReminderEnable(): Boolean {
        return cacheSource.isReminderEnable()
    }

    override suspend fun clearDataOnLogout() {
        cacheSource.clearSharedPreferenceOnLogout()
    }

    override suspend fun isTermsAndConditionAccepted(): Boolean {
        return cacheSource.isTermsAndConditionAccepted()
    }

    override suspend fun acceptTermsAndCondition(isAccepted: Boolean) {
        cacheSource.acceptTermsAndCondition(isAccepted)
    }

    override suspend fun setIsFirstTimePermissionAsked(permissions: Array<String>) {
        cacheSource.setIsFirstTimePermissionAsked(permissions)
    }

    override suspend fun isFirstTimePermissionAsked(permissions: Array<String>): Boolean {
        return cacheSource.isFirstTimePermissionAsked(permissions)
    }

    override suspend fun setAppUpdateAvailable(isAvailable: Boolean) {
        cacheSource.setAppUpdateAvailable(isAvailable)
    }

    override suspend fun isAppUpdateAvailable(): Boolean {
        return cacheSource.isAppUpdateAvailable()
    }

    override suspend fun setForcedAppUpdateAvailable(isAvailable: Boolean) {
        cacheSource.setForcedAppUpdateAvailable(isAvailable)
    }

    override suspend fun isForcedAppUpdateAvailable(): Boolean {
        return cacheSource.isForcedAppUpdateAvailable()
    }
}
