package com.example.mobiledger.presentation.budget

import androidx.annotation.StringRes
import com.example.mobiledger.common.utils.ConstantUtils.MAX_BUDGET
import com.example.mobiledger.common.utils.ConstantUtils.TOTAL_BUDGET

sealed class BudgetViewItem(val viewType: BudgetViewType) {
    data class BudgetHeaderData(@StringRes val data: Int, val type: BudgetViewType = BudgetViewType.Header) : BudgetViewItem(type)
    data class BudgetOverviewData(val data: MonthlyBudgetData, val type: BudgetViewType = BudgetViewType.MonthlyBudgetOverview) :
        BudgetViewItem(type)

    data class BudgetCategory(val data: BudgetCategoryData, val type: BudgetViewType = BudgetViewType.BudgetData) : BudgetViewItem(type)
}

data class MonthlyBudgetData(val maxBudget: Long = 0, val totalBudget: Long = 0)

data class BudgetCategoryData(val categoryName: String, val categoryExpense: String, val categoryBudget: String)

enum class BudgetViewType { Header, MonthlyBudgetOverview, BudgetData }

fun MonthlyBudgetData?.isEmpty(): Boolean = this == MonthlyBudgetData()

fun MonthlyBudgetData.toMutableMap(): MutableMap<String, Any> = mutableMapOf(
    MAX_BUDGET to maxBudget,
    TOTAL_BUDGET to totalBudget
)