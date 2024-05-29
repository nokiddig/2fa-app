package com.example.app_2fa.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_2fa.data.SocketManager
import com.example.app_2fa.utils.SaveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Suppress("UNREACHABLE_CODE")
class SettingViewModel : ViewModel() {
    private val _2FaState = MutableStateFlow(false)
    val twoFAState: StateFlow<Boolean> get() = _2FaState
    private val _genKeyState = MutableStateFlow("")
    val genKeyState: StateFlow<String> get() = _genKeyState
    private var _otpState = MutableStateFlow(-1)
    val otpState: StateFlow<Int> get() = _otpState

    private lateinit var socketManager: SocketManager

    fun initialize() {
        socketManager = SocketManager.getInstance()
        viewModelScope.launch {
            socketManager.keyState.collect { state ->
                Log.d("bug_2fa", "gen key: $state")
                _genKeyState.value = state
            }
        }

        viewModelScope.launch {
            socketManager.twoFAState.collect { state ->
                _2FaState.value = state
            }
        }

        viewModelScope.launch {
            socketManager.otpState.collect { state ->
                Log.d("bug_2fa", "gen key: $state")
                _otpState.value = state
            }
        }
    }

    fun request2FA(isChecked: Boolean) {
        val socket = SocketManager.getInstance()
        if (isChecked) {
            socket.sendMessage("tat2fa ${SaveData.USERNAME} ${SaveData.PASSWORD}")
        } else {
            socket.sendMessage("bat2fa ${SaveData.USERNAME} ${SaveData.PASSWORD}")
        }
    }
}
