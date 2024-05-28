package com.example.app_2fa.viewmodel

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

    private lateinit var socketManager: SocketManager

    fun initialize() {
        socketManager = SocketManager.getInstance()
        viewModelScope.launch {
            socketManager.twoFAState.collect { state ->
                _2FaState.value = state
            }
            socketManager.keyState.collect {state ->
                _genKeyState.value = state
            }
        }
    }

    fun request2FA(isChecked: Boolean) {
        val socket = SocketManager.getInstance()
        if (isChecked) {
            socket.sendMessage("tat2fa ${SaveData.USERNAME} ${SaveData.PASSWORD}")
//            SocketManager.SYSTEM_MODE = "tat2fa"
        } else {
            socket.sendMessage("bat2fa ${SaveData.USERNAME} ${SaveData.PASSWORD}")
//            SocketManager.SYSTEM_MODE = "bat2fa"
        }
    }
}
