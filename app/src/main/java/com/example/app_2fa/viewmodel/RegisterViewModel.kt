package com.example.app_2fa.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_2fa.data.SocketManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private var _registerState = MutableStateFlow(-1)
    val registerState: StateFlow<Int> get() = _registerState
    private lateinit var socketManager: SocketManager

    fun initialize() {
        socketManager = SocketManager.getInstance()
        viewModelScope.launch {
            socketManager.registerState.collect { state ->
                _registerState.value = state
            }
        }
    }

    fun sendRegisterMessage(username: String, password: String) {
        socketManager.sendMessage("signup $username $password")
    }

}
