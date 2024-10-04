package com.kimnlee.memberinvitation.presentation.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.kimnlee.common.FCMDataForInvitation
import com.kimnlee.common.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

private const val TAG = "MemberInvitationViewMod"
class MemberInvitationViewModel(private val authManager: AuthManager) : ViewModel() {

    private var secretKey1 by mutableStateOf("GSp4rEgkhZG4pzQF9Gyq/Fv0YZjPKsbjd4Ep3s7YWpVw/KfGomCBEArDI6ngwNjiwvcmLZMc+F/KwcWX5ublISQsN/Cs4o7dx8tH620pPTVyjSB2U08GRJM9OXITJ6GQvBfPvuHskVDqrjwNsI65YxlE1/1TZZ24lVkthLQoEebbIUfSkQH7ghKzUnniinrG") // Default username

    private val serviceUuid: UUID = UUID.fromString("0000180D-0000-1000-8000-00805F9B34FB")

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var scanner: BluetoothLeScanner? = null

    private var isScanning by mutableStateOf(false)
    val discoveredPhoneNumbers = mutableStateListOf<String>()

    private var advertiser: BluetoothLeAdvertiser? = null

    private var isAdvertising by mutableStateOf(false)

    private val _vehicleId = MutableStateFlow(-1)
    val vehicleId: StateFlow<Int> = _vehicleId.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _showInvitationBLE = MutableStateFlow(false)
    val showInvitationBLE: StateFlow<Boolean> = _showInvitationBLE.asStateFlow()

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()

    private val _navigateEvent = MutableStateFlow(false)
    val navigateEvent: StateFlow<Boolean> = _navigateEvent.asStateFlow()

    private lateinit var fcmDataForInvitation : FCMDataForInvitation

    fun triggerNavigate() {
        Log.d(TAG, "triggerNavigate: Navigate 지시함")
        _navigateEvent.value = true
    }
    fun onNavigateHandled() {
        _navigateEvent.value = false
    }

    private val _showInvitationWaitingScreen = MutableStateFlow(false)
    val showInvitationWaitingScreen: StateFlow<Boolean> = _showInvitationWaitingScreen.asStateFlow()

    fun initBluetoothAdapter(adapter: BluetoothAdapter) {
        this.bluetoothAdapter = adapter
    }

    fun openInvitationWaitingScreen(){
        _showInvitationWaitingScreen.value = true
    }
    fun closeInvitationWaitingScreen(){
        _showInvitationWaitingScreen.value = false
    }

    fun updatePhoneNumber(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
    }
    fun openBottomSheet() {
        _showBottomSheet.value = true
    }

    fun closeBottomSheet() {
        _showBottomSheet.value = false
    }

    fun openInvitationBLE(vehicleId: Int){
        _vehicleId.value = vehicleId
        Log.d(TAG, "openInvitationBLE: 차량 ID: ${vehicleId} // _vehicleId: ${_vehicleId.value}")
        Log.d(TAG, "openInvitationBLE: 주변 확인 시작")
        startScanning()
        _showInvitationBLE.value = true
    }
    fun closeInvitationBLE(){
        Log.d(TAG, "openInvitationBLE: 주변 확인 중단")
        stopScanning()
        _showInvitationBLE.value = false
    }
    fun inviteMember() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = checkUserExistence(_phoneNumber.value)
            if (result) {
                sendInvitation(_phoneNumber.value)
                _uiState.value = UiState.InvitationSent
            } else {
                _uiState.value = UiState.UserNotFound
            }
        }
    }

    private suspend fun checkUserExistence(phoneNumber: String): Boolean {
        // 가입된 유저인지 확인
        return true
    }

    private suspend fun sendInvitation(phoneNumber: String) {

    }

    private suspend fun bleAdvertise(phoneNumber: String) {

    }

    @SuppressLint("MissingPermission")
    fun startAdvertising() {
        if (!bluetoothAdapter.isEnabled) {
            // Handle Bluetooth not enabled
            // 사용자에게 블루투스 켜라고 해야함
            return
        }

        advertiser = bluetoothAdapter.bluetoothLeAdvertiser
        if (advertiser == null) {
            // Device does not support BLE advertising
            return
        }

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false)
            .build()

//        val userinfo = authManager.getUserInfo().phoneNumber
//        val username = authManager.getUserInfo().phoneNumber
        updatePhoneNumber(authManager.getUserInfo().phoneNumber)
        val username = _phoneNumber.value
//        val usernameBytes = username.toByteArray(StandardCharsets.UTF_8)
        Log.d(TAG, "startAdvertising: 암호화 전: ${username}")
        val usernameEnc = encrypt(username, generateKey())
        Log.d(TAG, "startAdvertising: 암호화 후: ${usernameEnc}")
        val usernameEncTrim = usernameEnc.substring(0, usernameEnc.length-2)
//        val usernameBytes = usernameEnc.toByteArray(StandardCharsets.UTF_8)
        val usernameBytes = usernameEncTrim.toByteArray(StandardCharsets.UTF_8)

        val data = AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(serviceUuid))
            .addServiceData(ParcelUuid(serviceUuid), usernameBytes)
            .setIncludeDeviceName(false)
            .build()

        advertiser?.startAdvertising(settings, data, advertiseCallback)
    }

    @SuppressLint("MissingPermission")
    fun stopAdvertising() {
        Log.d(TAG, "stopAdvertising: 멈춰!!!")
        advertiser?.stopAdvertising(advertiseCallback)
    }

    fun handleInvitation(fcmDataForInvitation: FCMDataForInvitation){
        this.fcmDataForInvitation = fcmDataForInvitation
        stopAdvertising()
        triggerNavigate()
    }

    fun triggerNavigateToInvitedScreen(navController: NavController) {
        navController.currentBackStackEntry?.savedStateHandle?.set("invitationData", this.fcmDataForInvitation)
        navController.navigate("memberinvitation_invited")
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            // Advertising started successfully
            // You might update UI or notify the user
        }

        override fun onStartFailure(errorCode: Int) {
            // Advertising failed
            // Handle error codes appropriately
            isAdvertising = false
        }
    }


    @SuppressLint("MissingPermission")
    fun startScanning() {
        Log.d(TAG, "startScanning: 진짜시작1")
        if (!bluetoothAdapter.isEnabled) {
            Log.d(TAG, "startScanning: 블루투스가 안 켜짐")
            return
        }
        Log.d(TAG, "startScanning: 진짜시작2")

        scanner = bluetoothAdapter.bluetoothLeScanner

        if (scanner == null) {
            return
        }
        Log.d(TAG, "startScanning: 진짜시작3")

        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(serviceUuid))
            .build()

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner?.startScan(listOf(scanFilter), scanSettings, scanCallback)
    }

    @SuppressLint("MissingPermission")
    fun stopScanning() {
        discoveredPhoneNumbers.clear()
        scanner?.stopScan(scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val scanRecord = result.scanRecord
            scanRecord?.let {
                val data = it.getServiceData(ParcelUuid(serviceUuid))
                data?.let { bytes ->
//                    val username = String(bytes, StandardCharsets.UTF_8)
                    var username = String(bytes, StandardCharsets.UTF_8)
                    Log.d(TAG, "onScanResult: 복호화 전: ${username}")
                    username = decrypt(username+"==", generateKey())
                    Log.d(TAG, "onScanResult: 복호화 후: ${username}")
                    if (!discoveredPhoneNumbers.contains(username)) {
                        discoveredPhoneNumbers.add(username)
                    }
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            // Handle scan failure
            isScanning = false
        }
    }

    fun encrypt(input: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(input.toByteArray())
        // Encode the result to Base64
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
    }

    fun decrypt(encrypted: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedBytes = cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP))
        return String(decryptedBytes)
    }

    fun generateKey(): SecretKey {
        val keyBytes = secretKey1.toByteArray(Charsets.UTF_8)
        val sha = MessageDigest.getInstance("SHA-256")
        val key = sha.digest(keyBytes).copyOf(16)  // Use the first 128 bits (16 bytes)
        return SecretKeySpec(key, "AES")
    }


    sealed class UiState {
        object Initial : UiState()
        object Loading : UiState()
        object InvitationSent : UiState()
        object UserNotFound : UiState()
    }
}