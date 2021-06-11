package com.example.mobiledger.presentation.budget.budgetscreen

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.utils.DateUtils
import com.example.mobiledger.common.utils.showAddBudgetDialogFragment
import com.example.mobiledger.common.utils.showUpdateBudgetDialogFragment
import com.example.mobiledger.databinding.FragmentBudgetBinding
import com.example.mobiledger.presentation.OneTimeObserver

class BudgetFragment : BaseFragment<FragmentBudgetBinding, BudgetNavigator>(R.layout.fragment_budget) {

    private val viewModel: BudgetViewModel by viewModels { viewModelFactory }

    private val budgetAdapter: BudgetAdapter by lazy { BudgetAdapter(onMakeBudgetClick, onBudgetOverviewClick, onAddBudgetCategoryClick, onBudgetCategoryClick) }

//    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun swipeRefreshLayout(): SwipeRefreshLayout {
        return viewBinding.swipeRefreshLayout
    }

    override fun refreshView() {
        hideSnackBarErrorView()
        viewModel.reloadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        setUpObserver()
        initRecyclerView()
        viewModel.getBudgetData()
        viewModel.getExpenseCategoryList()
    }

    private fun setOnClickListener() {
        viewBinding.apply {
            ivProfileIcon.setOnClickListener {
                navigator?.navigateToProfileScreen()
            }

            monthNavigationBar.leftArrow.setOnClickListener { handleLeftClick() }
            monthNavigationBar.rightArrow.setOnClickListener { handleRightClick() }
        }
    }

    private fun setUpObserver() {

        activityViewModel.updateBudgetResultLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            refreshView()
        })

        activityViewModel.addBudgetResultLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            refreshView()
        })

        viewModel.budgetViewItemListLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            budgetAdapter.addItemList(it)
        })

        viewModel.monthNameLiveData.observe(viewLifecycleOwner, {
            viewBinding.monthNavigationBar.tvMonth.text = it
        })

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showSwipeRefresh()
            } else {
                hideSwipeRefresh()
            }
        }

    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        viewBinding.rvBudget.apply {
            layoutManager = linearLayoutManager
            adapter = budgetAdapter
        }
    }

    private val onMakeBudgetClick = fun() {
        showAddBudgetDialogFragment(
            requireActivity().supportFragmentManager,
            false,
            viewModel.giveFinalExpenseList(),
            DateUtils.getDateInMMyyyyFormat(viewModel.getCurrentMonth()),
            0
        )
    }

    private val onBudgetOverviewClick = fun() {
        showAddBudgetDialogFragment(
            requireActivity().supportFragmentManager,
            false,
            viewModel.giveFinalExpenseList(),
            DateUtils.getDateInMMyyyyFormat(viewModel.getCurrentMonth()),
            viewModel.budgetTotal
        )
    }

    private val onAddBudgetCategoryClick = fun() {
        showAddBudgetDialogFragment(
            requireActivity().supportFragmentManager,
            true,
            viewModel.giveFinalExpenseList(),
            DateUtils.getDateInMMyyyyFormat(viewModel.getCurrentMonth()),
            viewModel.budgetTotal
        )
    }

    private val onBudgetCategoryClick = fun(category: String, categoryBudget: Long){
        showUpdateBudgetDialogFragment(
            requireActivity().supportFragmentManager, viewModel.getCurrentMonth(), category, categoryBudget
        )
    }

    private fun handleRightClick() {
        if (!viewModel.isCurrentMonth()) {
            viewModel.getNextMonthData()
        }
        handleRightArrowState()
    }

    private fun handleLeftClick() {
        viewModel.getPreviousMonthData()
        handleRightArrowState()
    }

    private fun handleRightArrowState() {
        val color = if (!viewModel.isCurrentMonth())
            R.color.prussianBlue
        else
            R.color.colorGrey

        ImageViewCompat.setImageTintList(
            viewBinding.monthNavigationBar.rightArrow,
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
        )
    }

    companion object {
        fun newInstance() = BudgetFragment()
    }
}