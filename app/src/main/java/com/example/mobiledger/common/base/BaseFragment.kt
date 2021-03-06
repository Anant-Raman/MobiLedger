package com.example.mobiledger.common.base

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mobiledger.R
import com.example.mobiledger.common.di.DependencyProvider
import com.example.mobiledger.common.extention.changeStatusBarColor
import com.example.mobiledger.common.extention.gone
import com.example.mobiledger.common.extention.visible
import com.example.mobiledger.common.extention.showToast
import com.example.mobiledger.common.utils.PermissionUtils
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.NormalObserver
import com.example.mobiledger.presentation.main.MainActivity
import com.example.mobiledger.presentation.main.MainActivityViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseFragment<B : ViewDataBinding, NV : BaseNavigator>(
    @LayoutRes private val layoutId: Int,
    private val statusBarColor: StatusBarColor? = StatusBarColor.WHITE
) : Fragment() {

    private var _viewBinding: B? = null
    protected var navigator: NV? = null

    protected val viewModelFactory = DependencyProvider.provideViewModelFactory()
    protected val activityViewModel: MainActivityViewModel by activityViewModels()

    protected var firebaseAnalytics: FirebaseAnalytics? = null

    val viewBinding get() = _viewBinding!!

    private val errorTimeOut: Long = 5000

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, backPressCallback)

        val activity = activity
        if (activity is BaseActivity<*, *>) {
            try {
                @Suppress("UNCHECKED_CAST")
                navigator = activity.getFragmentNavigator() as NV
            } catch (e: Exception) {
                Timber.e("Activity Navigator should implement Fragment navigator")
            }
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        viewBinding.lifecycleOwner = viewLifecycleOwner
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSwipeRefreshLayout()
        observeInternetState()
        setBottomNavVisibility()
    }

    private fun setBottomNavVisibility() {
        if (isBottomNavVisible()) {
            showBottomNav()
        } else {
            hideBottomNav()
        }
    }

    protected fun showBottomNav() {
        activityViewModel.showBottomNavigationView()
    }

    protected fun hideBottomNav() {
        activityViewModel.hideBottomNavigationView()
    }

    private fun observeInternetState() {
        activityViewModel.isInternetAvailableLiveData.observe(viewLifecycleOwner, NormalObserver { state ->
            if (state.current && !state.previous) {
                hideSnackBarErrorView()
            } else if (!state.current) {
                showSnackBarErrorView(getString(R.string.device_offline_error_message), false)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (statusBarColor != null) {
            viewBinding.root.changeStatusBarColor(requireActivity(), statusBarColor)
        }
//        registerForAuthResult()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    protected open fun isBottomNavVisible(): Boolean = true

    /*----------------------------------------Back Press----------------------------------------*/

    protected open fun onBackPressHandled(): Boolean = false

    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!onBackPressHandled()) {
                isEnabled = false
                activity?.onBackPressed()
            }
        }
    }

    /*----------------------------------------Swipe Refresh----------------------------------------*/

    private fun initSwipeRefreshLayout() {
        swipeRefreshLayout()?.setOnRefreshListener {
            refreshView()
        }
    }

    protected fun hideSwipeRefresh() {
        when (swipeRefreshLayout()?.isRefreshing) {
            true -> swipeRefreshLayout()?.isRefreshing = false
        }
    }

    protected fun showSwipeRefresh() {
        when (swipeRefreshLayout()?.isRefreshing) {
            false -> swipeRefreshLayout()?.isRefreshing = true
        }
    }

    open fun swipeRefreshLayout(): SwipeRefreshLayout? = null
    open fun refreshView() = Unit

    /*----------------------------------------Toast----------------------------------------*/

    protected fun handleNativeToast(toast: NativeToastData) {
        toast.msg?.let { requireActivity().showToast(it) }
            ?: toast.msgRes?.let { requireActivity().showToast(getString(it)) }
    }

    /*---------------------------------------Analytics--------------------------------------*/

    protected fun logEvent(msg: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, msg)
        firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    /*------------------------------------Snack Bar error ----------------------------------------*/

    protected open fun getSnackBarErrorView(): SnackViewErrorBinding? = null

    protected fun showSnackBarErrorView(message: String, isVanishing: Boolean) {
        getSnackBarErrorView()?.apply {
            root.visible()
            tvWarning.text = message
        }

        if (isVanishing) {
            Handler(Looper.getMainLooper()).postDelayed({
                hideSnackBarErrorView()
            }, errorTimeOut)

        }
    }

    protected fun hideSnackBarErrorView(forceHide: Boolean = false) {
        val isInternetAvailable: Boolean =
            activityViewModel.isInternetAvailableLiveData.value?.peekContent()?.current ?: true
        if (!forceHide && isInternetAvailable) {
            getSnackBarErrorView()?.root?.gone()
        } else if (forceHide) {
            getSnackBarErrorView()?.root?.gone()
        }
    }

    protected fun sendNotification(notificationManager: NotificationManager?, CHANNEL_ID: String, textTitle: String, textContent: String) {

        val notificationID = (0..1000000).random()
        val notificationBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager?.notify(notificationID, notificationBuilder.build())
    }

    protected fun createNotificationChannel(notificationManager: NotificationManager?, channelDescription: String, CHANNEL_ID: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_transaction)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = channelDescription
            }
            // Register the channel with the system
            notificationManager?.createNotificationChannel(channel)
        }
    }

/*-----------------------------------Authentication Error-----------------------------------------*/

//    private var authResultCallback: ((Boolean) -> Unit)? = null
//
//    protected fun addAuthCallback(authCallback: (Boolean) -> Unit) {
//        authResultCallback = authCallback
//    }
//
//    private val delayToStartAuthFlow: Int = 3000
//    private var lastTimeAuthFlowStarted: Long = 0
//
//    protected fun navigateToLoginScreen() {
//        if (SystemClock.elapsedRealtime() - lastTimeAuthFlowStarted < delayToStartAuthFlow) {
//            return
//        }
//        lastTimeAuthFlowStarted = SystemClock.elapsedRealtime()
//        Handler(Looper.getMainLooper()).postDelayed({
//            navigator?.navigateToLoginScreen(RequestCode.REQUEST_CODE_AUTH_RESULT)
//        }, delayToStartAuthFlow.toLong())
//    }

    /*-----------------------------------Permissions-----------------------------------------*/

    protected fun checkAndAskForPermissions(
        permissions: Array<String>,
        @StringRes dialogString: Int,
        requestMultiplePermissions: ActivityResultLauncher<Array<String>>,
        isPermissionsGranted: (Boolean) -> Unit
    ) {
        val permissionsNeededList = PermissionUtils.permissionsNeeded(requireActivity(), permissions.toList())

        lifecycleScope.launch {
            when {
                permissionsNeededList.isEmpty() -> {
                    // All Permissions are granted
                    isPermissionsGranted(true)
                }

                (activity as MainActivity).shouldShowRequestPermissionRationale(permissionsNeededList) -> {
                    // User Denied permission so requesting permission again
                    requestMultiplePermissions.launch(permissionsNeededList)
                    isPermissionsGranted(false)
                }

                else -> {
                    val isFirstTime = activityViewModel.isPermissionsFirstTime(permissionsNeededList)
                    if (isFirstTime) {
                        // First time permission is asked
                        activityViewModel.setPermissionNotFirstTime(permissionsNeededList)
                        requestMultiplePermissions.launch(permissionsNeededList)
                    } else {
                        // User denied and pressed do not show again
                        (activity as MainActivity).showGoToSettingsDialog(dialogString)
                    }
                    isPermissionsGranted(false)
                }
            }
        }
    }

/*----------------------------------------Status Bar----------------------------------------*/

    enum class StatusBarColor(@ColorRes val color: Int, val isLightColor: Boolean) {
        BLUE(R.color.colorAppBlue, isLightColor = false),
        LIGHT_GREY(R.color.colorGray, isLightColor = true),
        TRANSPARENT(R.color.transparent, isLightColor = true),
        WHITE(R.color.colorWhite, isLightColor = true),
    }
}