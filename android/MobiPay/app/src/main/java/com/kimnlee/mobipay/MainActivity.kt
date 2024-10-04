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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.kimnlee.auth.presentation.viewmodel.AuthenticationState
import com.kimnlee.auth.presentation.viewmodel.BiometricViewModel
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.common.ui.theme.MobiPayTheme
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import com.kimnlee.mobipay.navigation.AppNavGraph
import com.kimnlee.payment.PaymentApprovalReceiver

private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {

    private lateinit var biometricViewModel: BiometricViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var memberInvitationViewModel: MemberInvitationViewModel

    private var paymentApprovalReceiver: PaymentApprovalReceiver? = null

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var backgroundLocationPermissionLauncher: ActivityResultLauncher<String>
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Splash 스크린 가동 이후 Theme을 원래대로 되돌리는 코드
        setTheme(R.style.Theme_MobiPay)

        val authManager = (application as MobiPayApplication).authManager
        val apiClient = (application as MobiPayApplication).apiClient
        val fcmService = (application as MobiPayApplication).fcmService

        biometricViewModel = ViewModelProvider(this).get(BiometricViewModel::class.java)

        loginViewModel = LoginViewModel(authManager, apiClient, fcmService)

        val app = application as MobiPayApplication
        memberInvitationViewModel = app.aMemberInvitationViewModel

        setupPermissionsLauncher()
        setupBackgroundLocationPermissionLauncher()

        requestPermissions()

        registerPayReceiver()

        setContent {
            MobiPayTheme {
                val navController = rememberNavController()
                val isLoggedIn by authManager.isLoggedIn.collectAsState(initial = false)

                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn) {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                }

                AppNavGraph(
                    navController,
                    authManager,
                    applicationContext,
                    apiClient,
                    loginViewModel,
                    memberInvitationViewModel
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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        } else {

        }
    }


    private fun setupBackgroundLocationPermissionLauncher() {
        backgroundLocationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "Background location permission granted")
                alertDialog?.dismiss()
                // Proceed with your app's logic now that permission is granted
            } else {
                Log.d(TAG, "Background location permission denied")
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
                Log.d(TAG, "Background location permission already granted")
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
//                    Log.d("MainActivity", "Authentication successful")
                }
                RESULT_CANCELED -> {
                    biometricViewModel.updateAuthenticationState(AuthenticationState.Failure)
//                    Log.d("MainActivity", "Authentication canceled or failed")
                }
                else -> {
                    biometricViewModel.updateAuthenticationState(AuthenticationState.Error("Unknown error occurred"))
//                    Log.d("MainActivity", "Authentication error: Unknown result code")
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
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

}