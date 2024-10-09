package com.kimnlee.mobipay

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.app.AlertDialog
import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.kimnlee.payment.presentation.viewmodel.AuthenticationState
import com.kimnlee.payment.presentation.viewmodel.BiometricViewModel
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.common.FCMData
import com.kimnlee.common.FCMDataForInvitation
import com.kimnlee.common.ui.theme.MobiPayTheme
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import com.kimnlee.mobipay.navigation.AppNavGraph
import com.kimnlee.payment.PaymentApprovalReceiver
import com.kimnlee.payment.data.repository.PaymentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {

    private lateinit var biometricViewModel: BiometricViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var memberInvitationViewModel: MemberInvitationViewModel
    private lateinit var cardManagementViewModel: CardManagementViewModel

    private var paymentApprovalReceiver: PaymentApprovalReceiver? = null

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var backgroundLocationPermissionLauncher: ActivityResultLauncher<String>
    private var alertDialog: AlertDialog? = null
    private lateinit var paymentRepository: PaymentRepository

    private val fcmDataFromIntent = MutableStateFlow<FCMData?>(null)
    private val fcmDataForInvitationFromIntent = MutableStateFlow<FCMDataForInvitation?>(null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Splash 스크린 가동 이후 Theme을 원래대로 되돌리는 코드
        setTheme(R.style.Theme_MobiPay)

        val authManager = (application as MobiPayApplication).authManager
        val apiClient = (application as MobiPayApplication).apiClient
        val fcmService = (application as MobiPayApplication).fcmService

        biometricViewModel = ViewModelProvider(this).get(BiometricViewModel::class.java)
        cardManagementViewModel = CardManagementViewModel(authManager, apiClient)

        loginViewModel = LoginViewModel(authManager, apiClient, fcmService)

        val app = application as MobiPayApplication
        memberInvitationViewModel = app.aMemberInvitationViewModel

        setupPermissionsLauncher()
        setupBackgroundLocationPermissionLauncher()

        requestPermissions()

        registerPayReceiver()

        paymentRepository = (application as MobiPayApplication).paymentOperations as PaymentRepository

        handleIntent(intent)

        setContent {
            MobiPayTheme {
                val navController = rememberNavController()
                val isLoggedIn by authManager.isLoggedIn.collectAsState(initial = false)
                val fcmData by fcmDataFromIntent.collectAsState()
                val registeredCards by cardManagementViewModel.registeredCards.collectAsState()

                val fcmDataForInvitation by fcmDataForInvitationFromIntent.collectAsState()

                LaunchedEffect(isLoggedIn, fcmData, registeredCards) {
                    if (isLoggedIn && fcmData != null && fcmData!!.type != "payment_success" && registeredCards.isNotEmpty()) {
                        Log.d(TAG, "로그인 + FCM데이터 확인되어 수동결제 처리")

                        val registeredCardsJson = Uri.encode(Gson().toJson(registeredCards))

                        Log.d(TAG, "등록 된 카드목록 JSON: $registeredCardsJson")

                        val fcmDataJson = Uri.encode(Gson().toJson(fcmData))
                        navController.navigate("payment_requestmanualpay?fcmData=$fcmDataJson&registeredCards=$registeredCardsJson") {
                            popUpTo("home") { inclusive = false }
                        }

                        // 처리 후 fcmData 리셋
                        fcmDataFromIntent.value = null
                    }
                }

                LaunchedEffect(isLoggedIn, fcmData, registeredCards) {
                    Log.d(TAG, "onCreate: 여긴 불림")
                    if (isLoggedIn && fcmData != null && fcmData!!.type == "payment_successful") {
                        Log.d(TAG, "결제완료 화면 넘어감")

                        val registeredCardsJson = Uri.encode(Gson().toJson(registeredCards))

                        val fcmDataJson = Uri.encode(Gson().toJson(fcmData))
                        navController.navigate("payment_successful?fcmData=$fcmDataJson") {
                            popUpTo("home") { inclusive = false }
                        }

                        // 처리 후 fcmData 리셋
                        fcmDataFromIntent.value = null
                    }else if (isLoggedIn && fcmData != null && fcmData!!.type == "transactionCancel"){
                        Log.d(TAG, "결제취소 성공 화면으로 이동")

                        val fcmDataJson = Uri.encode(Gson().toJson(fcmData))
                        navController.navigate("payment_cancelled?fcmData=$fcmDataJson") {
                            popUpTo("home") { inclusive = false }
                        }

                        // 처리 후 fcmData 리셋
                        fcmDataFromIntent.value = null
                    }else{
                        Log.d(TAG, "onCreate: 여기도 불림")
                        if(fcmData != null)
                            Log.d(TAG, "onCreate: fcmData 타입 = ${fcmData!!.type}")
                    }
                }

                LaunchedEffect(isLoggedIn, fcmDataForInvitation) {
                    Log.d(TAG, "onCreate fcmDataForInvitation: 여긴 불림")
                    if (isLoggedIn && fcmDataForInvitation != null && fcmDataForInvitation!!.type == "invitation") {
                        Log.d(TAG, "멤버초대 화면 넘어감")

                        val fcmDataForInvitationJson = Uri.encode(Gson().toJson(fcmDataForInvitation))
                        navController.navigate("memberinvitation_invited?fcmDataForInvitation=$fcmDataForInvitationJson") {
                            popUpTo("home") { inclusive = false }
                        }

                        // 처리 후 fcmData 리셋
                        fcmDataForInvitationFromIntent.value = null
                    }
                }

                AppNavGraph(
                    navController,
                    authManager,
                    applicationContext,
                    apiClient,
                    loginViewModel,
                    memberInvitationViewModel,
                    paymentRepository
                )
            }
        }
    }


    private fun setupPermissionsLauncher() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsGranted ->
            val allPermissionsGranted = permissionsGranted.values.all { it }
            if (!allPermissionsGranted) {
                Toast.makeText(this, "모든 권한을 허용해야 모비페이를 사용할 수 있어요.", Toast.LENGTH_LONG).show()
                finish()
            } else {
                val accessFineLocationGranted = permissionsGranted[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
                if (accessFineLocationGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    requestBackgroundLocationPermission()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
        requestPermissions()
    }

    private fun requestPermissions() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.CAMERA)
        permissions.add(Manifest.permission.RECORD_AUDIO)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }


    private fun setupBackgroundLocationPermissionLauncher() {
        backgroundLocationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "백그라운드 위치 권한 허용됨")
                alertDialog?.dismiss()
            } else {
                Log.d(TAG, "백그라운드 위치 권한 거절됨")
            }
        }
    }


    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                showBackgroundLocationRationale {
                    backgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
            } else {
                Log.d(TAG, "백그라운드 권한 이미 허용됨")
            }
        }
    }


    private fun showBackgroundLocationRationale(onRationaleAccepted: () -> Unit) {
        alertDialog?.dismiss()
        alertDialog = AlertDialog.Builder(this)
            .setTitle("백그라운드 위치 권한 필요")
            .setMessage("안전한 결제를 위해 백그라운드 위치 권한을 '항상 허용' 해주세요.")
            .setPositiveButton("확인") { _, _ -> onRationaleAccepted() }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "백그라운드 위치 권한이 필요합니다. 일부 기능이 제한될 수 있습니다.", Toast.LENGTH_LONG).show()
            }
            .setCancelable(false)
            .create()

        alertDialog?.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == biometricViewModel.BIO_AUTH) {
            when (resultCode) {
                RESULT_OK -> {
                    biometricViewModel.updateAuthenticationState(AuthenticationState.Success)
                }
                RESULT_CANCELED -> {
                    biometricViewModel.updateAuthenticationState(AuthenticationState.Failure)
                }
                else -> {
                    biometricViewModel.updateAuthenticationState(AuthenticationState.Error("Unknown error occurred"))
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.scheme == "mobipay" && (uri.host == "payment_requestmanualpay" || uri.host == "payment_successful"|| uri.host == "payment_cancelled")) {
                val fcmDataJson = uri.getQueryParameter("fcmData")
                Log.d(TAG, "handleIntent: $fcmDataJson")
                fcmDataJson?.let {
                    val fcmData = Gson().fromJson(it, FCMData::class.java)
                    fcmDataFromIntent.value = fcmData
                    Log.d(TAG, "handleIntent: $fcmDataJson")
                }
            }
            if (uri.scheme == "mobipay" && (uri.host == "youvegotinvited")) {
                val fcmDataJson = uri.getQueryParameter("fcmDataForInvitation")
                Log.d(TAG, "handleIntent: $fcmDataJson")
                fcmDataJson?.let {
                    val fcmData = Gson().fromJson(it, FCMDataForInvitation::class.java)
                    fcmDataForInvitationFromIntent.value = fcmData
                    Log.d(TAG, "handleIntent: $fcmDataJson")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterPayReceiver()
    }


    private fun registerPayReceiver() {
        val intentFilter = IntentFilter("com.kimnlee.mobipay.PAYMENT_APPROVAL")
        paymentApprovalReceiver = PaymentApprovalReceiver()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(paymentApprovalReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(paymentApprovalReceiver, intentFilter)
        }
    }


    private fun unregisterPayReceiver() {
        paymentApprovalReceiver?.let {
            unregisterReceiver(it)
            paymentApprovalReceiver = null
        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause called")
        super.onPause()
    }

}