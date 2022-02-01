package com.example.mobiledger.data.sources.api

import android.util.Log
import com.example.mobiledger.common.utils.ConstantUtils
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.ErrorMapper
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.FireBaseResult
import com.example.mobiledger.domain.entities.ConfigEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface ConfigApi {
    suspend fun getConfigDetails(): AppResult<ConfigEntity>
}

class ConfigApiImpl(private val firebaseDb: FirebaseFirestore) :
    ConfigApi {
    override suspend fun getConfigDetails(): AppResult<ConfigEntity> {
        var response: Task<DocumentSnapshot>? = null
        var exception: Exception? = null

        try {
            val docRef =
                firebaseDb.collection(ConstantUtils.CONFIG).document(ConstantUtils.APP_UPDATE)

            response = docRef.get()
            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                val configDetail = configEntityMapper(result.data?.result as DocumentSnapshot)
                Log.i("Anant from api", configDetail?.isForceUpdate.toString())
                Log.i("Anant from api", configDetail?.latestVersion.toString())
                Log.i("Anant from api", configDetail?.updateMessage.toString())
                if (configDetail != null) AppResult.Success(configDetail)
                else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }
}


private fun configEntityMapper(configDetails: DocumentSnapshot): ConfigEntity? {
    if (!configDetails.exists()) return null
    Log.i("Anant --- ",configDetails.data?.get("isForceUpdate").toString())
    return configDetails.toObject(ConfigEntity::class.java)
}
