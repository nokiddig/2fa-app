package com.example.app_2fa.viewmodel
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_2fa.data.SocketManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private var _loginState = MutableStateFlow(-1)
    val loginState: StateFlow<Int> get() = _loginState
    private lateinit var socketManager: SocketManager

    fun initialize() {
        socketManager = SocketManager.getInstance()
        viewModelScope.launch {
            socketManager.loginState.collect { state ->
                _loginState.value = state
                Log.d("bug_2fa", "login socket $state")
            }
        }
    }

    fun sendLoginMessage(username: String, password: String) {
        socketManager.sendMessage("login $username $password")
    }

}
