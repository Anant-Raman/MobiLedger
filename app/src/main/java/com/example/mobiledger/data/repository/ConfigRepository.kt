package com.example.mobiledger.data.repository

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.sources.api.CategoryApi
import com.example.mobiledger.data.sources.api.ConfigApi
import com.example.mobiledger.data.sources.cache.CacheSource
import com.example.mobiledger.data.sources.room.categories.CategoriesDb
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.ConfigEntity
import com.example.mobiledger.domain.entities.IncomeCategoryListEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ConfigRepository {
    suspend fun getConfigDetails(): AppResult<ConfigEntity>
}


class ConfigRepositoryImpl(
    private val configApi: ConfigApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    ConfigRepository {
    override suspend fun getConfigDetails(): AppResult<ConfigEntity> {
        return withContext(dispatcher) {
            configApi.getConfigDetails()
        }
    }
}