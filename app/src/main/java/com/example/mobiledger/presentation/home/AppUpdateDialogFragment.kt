package com.example.mobiledger.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.disable
import com.example.mobiledger.common.extention.setOnSafeClickListener
import com.example.mobiledger.databinding.FragmentAppUpdateDialogBinding


class AppUpdateDialogFragment :
    BaseDialogFragment<FragmentAppUpdateDialogBinding, BaseNavigator>(R.layout.fragment_app_update_dialog) {

    private var isForceUpdate = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isForceUpdate = it.getBoolean(KEY_IS_FORCE_UPDATE)
            Log.i("Anant arg", isForceUpdate.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        Log.i("Anant", isForceUpdate.toString())
        viewBinding.apply {
            btnNotNow.setOnSafeClickListener {
                if (!isForceUpdate)
                    onNotNowClick.invoke()
            }

            if (isForceUpdate) {
                btnNotNow.disable()
                btnNotNow.alpha = 0.5f
            }

            btnYes.setOnSafeClickListener {
                onYesClick.invoke()
            }
        }
    }

    private val onYesClick = fun() {
        Toast.makeText(context, "Open Google Playstore", Toast.LENGTH_SHORT).show()
        dismiss()
    }

    private val onNotNowClick = fun() {
        dismiss()
    }

    companion object {
        private const val KEY_IS_FORCE_UPDATE = "KEY_IS_ADD_CATEGORY"
        fun newInstance(isForceUpdate: Boolean) = AppUpdateDialogFragment().apply {
            arguments = Bundle().apply {
                putBoolean(KEY_IS_FORCE_UPDATE, isForceUpdate)
            }
        }
    }
}
