package com.example.mobiledger.domain.entities

import com.example.mobiledger.common.utils.ConstantUtils

/*---------------------------------Category List---------------------------- */

data class CategoryListEntity(
    val uid: String,
    val incomeCategoryList: IncomeCategoryListEntity,
    val expenseCategoryList: ExpenseCategoryListEntity
)

data class IncomeCategoryListEntity(
    val incomeCategoryList: List<String>
) {
    constructor() : this(incomeCategoryList = emptyList())
}

data class ExpenseCategoryListEntity(
    val expenseCategoryList: List<String>
) {
    constructor() : this(expenseCategoryList = emptyList())
}

fun IncomeCategoryListEntity.toMutableMap(): MutableMap<String, Any> = mutableMapOf(
    ConstantUtils.INCOME_CATEGORY_LIST to incomeCategoryList,
)

fun ExpenseCategoryListEntity.toMutableMap(): MutableMap<String, Any> = mutableMapOf(
    ConstantUtils.EXPENSE_CATEGORY_LIST to expenseCategoryList,
)

fun IncomeCategoryListEntity.toMutableList(): MutableList<String> = incomeCategoryList.toMutableList()

fun ExpenseCategoryListEntity.toMutableList(): MutableList<String> = expenseCategoryList.toMutableList()


