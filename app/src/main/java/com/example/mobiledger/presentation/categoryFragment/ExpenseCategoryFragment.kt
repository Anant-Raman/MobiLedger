package com.example.mobiledger.presentation.categoryFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.setOnSafeClickListener
import com.example.mobiledger.common.utils.showAddCategoryDialogFragment
import com.example.mobiledger.databinding.FragmentExpenseCategoryBinding
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.categoryFragment.adapter.CategoryAdapter

class ExpenseCategoryFragment :
    BaseFragment<FragmentExpenseCategoryBinding, BaseNavigator>(R.layout.fragment_expense_category, StatusBarColor.BLUE) {

    private val viewModel: ExpenseCategoryViewModel by viewModels { viewModelFactory }

    private var list: List<String> = emptyList()

    private val expenseCategoryAdapter: CategoryAdapter by lazy {
        CategoryAdapter(TransactionType.Income, onCategoryDeleteClick)
    }

    override fun isBottomNavVisible(): Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        initRecyclerView()
        setOnCLickListener()
        viewModel.getExpenseCategoryList()
    }

    private fun setUpObserver() {
        viewModel.expenseCategoryList.observe(viewLifecycleOwner, OneTimeObserver {
            val arrList = it.expenseCategoryList as ArrayList
            arrList.sort()
            list = arrList
            expenseCategoryAdapter.addList(list)
        })

        viewModel.loadingState.observe(viewLifecycleOwner, {
            if (it) {
                viewBinding.expenseCategoryProgressBar.visibility = View.VISIBLE
            } else {
                viewBinding.expenseCategoryProgressBar.visibility = View.GONE
            }
        })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            when (it.viewErrorType) {
                ExpenseCategoryViewModel.ViewErrorType.NON_BLOCKING -> {
                    showSnackBarErrorView(it.message ?: getString(it.resID), true)
                }
            }
        })

        activityViewModel.addCategoryResultLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            viewModel.getExpenseCategoryList()
        })
    }

    private val onCategoryDeleteClick = fun(category: String, list: List<String>) {
        val newList = list as ArrayList
        newList.remove(category)
        viewModel.updateUserCategoryList(newList)
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        viewBinding.rvExpenseCategory.apply {
            layoutManager = linearLayoutManager
            adapter = expenseCategoryAdapter
        }
    }

    private fun setOnCLickListener() {
        viewBinding.btnAddCategory.setOnSafeClickListener {
            showAddCategoryDialogFragment(requireActivity().supportFragmentManager, list, false)
        }
    }

    companion object {
        fun newInstance() = ExpenseCategoryFragment()
    }
}