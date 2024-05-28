package com.example.app_2fa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_2fa.data.SocketManager
import com.example.app_2fa.utils.SaveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingViewModel : ViewModel() {
    private val _2FaState = MutableStateFlow(false)
    val twoFAState: StateFlow<Boolean> get() = _2FaState

    private lateinit var socketManager: SocketManager

    fun initialize(serverAddress: String) {
        socketManager = SocketManager(serverAddress)
        viewModelScope.launch {
            socketManager.twoFAState.collect { state ->
                _2FaState.value = state
            }
        }
    }

    fun sendLoginMessage(username: String, password: String) {
        socketManager.sendMessage("login $username $password")
    }

    fun request2FA(isChecked: Boolean) {
        val socket = SocketManager()
        if (isChecked) {
            socket.sendMessage("tat2fa ${SaveData.USERNAME} ${SaveData.PASSWORD}")
            SocketManager.SYSTEM_MODE = "tat2fa"
        } else {
            socket.sendMessage("bat2fa ${SaveData.USERNAME} ${SaveData.PASSWORD}")
            SocketManager.SYSTEM_MODE = "bat2fa"
        }
    }
}
