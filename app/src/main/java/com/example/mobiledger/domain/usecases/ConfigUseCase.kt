package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.ConfigRepository
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.ConfigEntity

interface ConfigUseCase {
    suspend fun getConfigDetails(): AppResult<ConfigEntity?>

}

class ConfigUseCaseImpl(private val configRepository: ConfigRepository) : ConfigUseCase {
    override suspend fun getConfigDetails(): AppResult<ConfigEntity?> =
        configRepository.getConfigDetails()
}