package com.example.mobiledger.domain.entities

import com.example.mobiledger.domain.enums.SignInType

data class ConfigEntity(
    val isForceUpdate: Boolean = false ,
    val latestVersion: String = "",
    val updateMessage: String = "",
){
//    constructor() : this(isForceUpdate = false)
}