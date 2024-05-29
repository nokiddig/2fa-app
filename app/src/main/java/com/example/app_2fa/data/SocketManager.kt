package com.example.app_2fa.data

import android.util.Log
import androidx.core.text.isDigitsOnly
import com.example.app_2fa.utils.Constants
import com.example.app_2fa.utils.MySecurity
import com.example.app_2fa.utils.SaveData
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SocketManager(serverAddress: String = "107.178.102.172:3000") {
    private val socket: Socket = IO.socket("http://$serverAddress")
    //live data
    private val _loginState = MutableStateFlow(-1)
    private val _twoFAState = MutableStateFlow(SaveData.IS_2FA)
    private val _keyState = MutableStateFlow("")
    private val _registerState = MutableStateFlow(-1)
    private val _otpState = MutableStateFlow(-1)
    private var connectStatus = false

    val loginState: StateFlow<Int> get() = _loginState
    val twoFAState: StateFlow<Boolean> get() = _twoFAState
    val keyState: StateFlow<String> get() = _keyState
    val registerState: StateFlow<Int> get() = _registerState
    val otpState: StateFlow<Int> get() = _otpState

    // Khởi tạo các listener
    private val onConnect = Emitter.Listener {
        connectStatus = true
        Log.d("bug_2fa","Connected to socket server")
    }

    private val onDisconnect = Emitter.Listener {
        connectStatus = false
        Log.d("bug_2fa","Disconnected from socket server")

    }

    private val onError = Emitter.Listener { args ->
        connectStatus = false
        val error = args[0] as Exception
        error.printStackTrace()
    }

    private val onMessage = Emitter.Listener { args ->
        val encodeMessage = args[0] as String
        Log.d("bug_2fa", "Received plaintext -${SYSTEM_MODE}-: ${MySecurity().decrypt(encodeMessage)}")
        Log.d("bug_2fa", "Received encode -${SYSTEM_MODE}-: ${encodeMessage}")
        val message = MySecurity().decrypt(encodeMessage)
        when (SYSTEM_MODE){
            Constants.MODE_LOGIN -> {
                if (message.isDigitsOnly()) {
                    _loginState.value = message.toInt()
                }
            }

            Constants.MODE_BAT_2FA -> {
                _keyState.value = message
            }

            Constants.MODE_TAT_2FA -> {
                if (message == "1") {
                    _twoFAState.value = false
                }

            }

            Constants.MODE_XAC_MINH_2FA -> {
                if (SaveData.IS_LOGIN){
                    if (message.isDigitsOnly()){
                        _otpState.value = message.toInt()
                    }
                }
                else {
                    if (message.isDigitsOnly() && message.toInt() == 1){
                        SYSTEM_MODE = Constants.MODE_LOGIN
                        _loginState.value = 1
                    }
                    else {
                        //_loginState.value = -1
                        _otpState.value = 0
                    }
                }

            }

            Constants.MODE_XAC_MINH_BAT_2FA -> {
                _twoFAState.value  = message == "1"
                if (message == "1"){
                    _otpState.value = 1
                }
                else {
                    _otpState.value = 0
                }
            }

            Constants.MODE_REGISTER -> {
                if (message.isDigitsOnly()) {
                    _registerState.value = message.toInt()
                }
            }
        }
    }

    init {
        socket.on(Socket.EVENT_CONNECT, onConnect)
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect)
        socket.on(Socket.EVENT_CONNECT_ERROR, onError)
        socket.on("message", onMessage)
        socket.connect()
        Thread.sleep(3000)
    }

    fun connect() {
        Log.d("bug_2fa", "Connecting")
        socket.connect()
    }

    fun disconnect() {
        socket.disconnect()
    }

    fun sendMessage(message: String) {
        Log.d("bug_2fa", "send message: $message")
        if (!connectStatus){
            connect()
            Thread.sleep(3000)
        }
        SYSTEM_MODE = message.split(" ").first()
        val encrypted = MySecurity().encrypt(message)
        Log.d("bug_2fa", "send message: $encrypted")

        socket.emit("message", encrypted)
    }

    companion object {
        var SYSTEM_MODE = ""
        @Volatile
        private var instance: SocketManager? = null

        fun getInstance(): SocketManager {
            return instance ?: synchronized(this) {
                instance ?: SocketManager().also { instance = it }
            }
        }
    }

    fun resetLoginState(){
        _loginState.value = -1
    }

    fun resetKey() {
        _keyState.value = ""
    }

    fun resetRegisterState(){
        _registerState.value = -1
    }

    fun resetOTPState() {
        _otpState.value = -1
    }
}