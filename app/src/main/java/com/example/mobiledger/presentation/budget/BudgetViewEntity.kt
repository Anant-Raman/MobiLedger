package com.example.mobiledger.presentation.budget

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.mobiledger.common.utils.ConstantUtils.MAX_BUDGET
import com.example.mobiledger.common.utils.ConstantUtils.TOTAL_BUDGET
import com.example.mobiledger.domain.enums.TransactionType

sealed class BudgetViewItem(val viewType: BudgetViewType) {
    data class BudgetHeaderData(val headerData: HeaderData, val type: BudgetViewType = BudgetViewType.Header) : BudgetViewItem(type)

    data class BudgetOverviewData(val data: MonthlyBudgetOverviewData, val type: BudgetViewType = BudgetViewType.MonthlyBudgetOverview) :
        BudgetViewItem(type)

    data class BudgetCategory(val data: BudgetCategoryData, val type: BudgetViewType = BudgetViewType.BudgetData) : BudgetViewItem(type)

    object BudgetEmpty : BudgetViewItem(BudgetViewType.EmptyBudget)

    object BudgetCategoryEmpty : BudgetViewItem(BudgetViewType.CategoryBudgetEmpty)
}
//            -------------------- Helper Data classes ----------------------------

data class HeaderData(@StringRes val headerString: Int, val isSecondaryHeaderVisible: Boolean = false)

data class MonthlyBudgetData(val maxBudget: Long = 0, val totalBudget: Long = 0)

fun MonthlyBudgetData?.isEmpty(): Boolean = this == MonthlyBudgetData()

fun MonthlyBudgetData.toMutableMap(): MutableMap<String, Any> = mutableMapOf(
    MAX_BUDGET to maxBudget,
    TOTAL_BUDGET to totalBudget
)

data class MonthlyCategorySummary(val categoryName: String = "", val categoryAmount: Long = 0, val categoryType: String = TransactionType.Expense.type)

data class MonthlyCategoryBudget(val categoryName: String = "", val categoryBudget: Long = 0, val categoryExpense: Long = 0)

fun MonthlyCategorySummary.isEmpty(): Boolean = this == MonthlyCategorySummary()

//            -------------------- Data class for sealed class ----------------------------

data class BudgetCategoryData(
    val categoryName: String,
    val totalCategoryBudget: Long,
    val totalCategoryExpense: Long,
    @DrawableRes val categoryIcon: Int
)

data class MonthlyBudgetOverviewData(val maxBudget: Long, val totalBudget: Long, val totalMonthlyExpense: Long)

enum class BudgetViewType { Header, MonthlyBudgetOverview, BudgetData, EmptyBudget, CategoryBudgetEmpty }

