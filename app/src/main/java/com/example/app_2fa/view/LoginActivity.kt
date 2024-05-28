package com.example.app_2fa.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.AppBarConfiguration
import com.example.app_2fa.data.SocketManager
import com.example.app_2fa.databinding.ActivityLoginBinding
import com.example.app_2fa.utils.SaveData
import com.example.app_2fa.viewmodel.LoginViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@Suppress("UNREACHABLE_CODE")
class LoginActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val serverAddress = "107.178.102.172:3000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()
        checkPermission()
        checkSaveAccount()
        loginViewModel.initialize()
        lifecycleScope.launch {
            loginViewModel.loginState.collect { loggedIn ->
                when (loggedIn) {
                    10 -> {
                        Log.d("bug_2fa", "login 10")
                        saveNewAccount(false)
                        onLoginSuccess()
                    }

                    1 -> {
                        Log.d("bug_2fa", "login 1")
                        saveNewAccount(true)
                        onLoginSuccess()
                    }

                    11 -> {
                        Log.d("bug_2fa", "Mode: login 11 ${SocketManager.SYSTEM_MODE}")
                        val dialog = DialogOTP(this@LoginActivity)
                        dialog.show()
                    }

                }
            }
        }
    }

    private fun saveNewAccount(is2fa: Boolean) {
        val saveAccount = SaveData(this)

        if (binding.saveCheckbox.isChecked) {
            saveAccount.updateData(
                true,
                true,
                is2fa,
                binding.edtUsername.text.toString(),
                binding.edtPassword.text.toString(),
                ""
            )
        } else {
            saveAccount.updateData(true, false, is2fa,
                binding.edtUsername.text.toString(),
                binding.edtPassword.text.toString(),
                "")
        }


        Log.d("bug_2fa", "is2fa ${SaveData.IS_2FA}")
    }

    private fun checkSaveAccount() {
        val saveAccount = SaveData(this)
        if (SaveData.IS_LOGIN) {

            Log.d("bug_2fa", "login save")
            onLoginSuccess()
        }
        else {
            if (SaveData.IS_SAVE){
                binding.edtUsername.setText(SaveData.USERNAME)
                binding.edtPassword.setText(SaveData.PASSWORD)
            }
        }
    }

    private fun checkPermission() {
        val permissionsNeeded = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val permissionsToRequest = mutableListOf<String>()

        permissionsNeeded.forEach { permission ->
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 1)
        }
    }

    private fun onLoginSuccess() {
        SocketManager.getInstance().clearLoginState()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    private fun setListener() {
        binding.btLogin.setOnClickListener {
            SaveData(this).updateAccount(binding.edtUsername.text.toString(),
            binding.edtPassword.text.toString(), "")
            loginViewModel.sendLoginMessage(
                binding.edtUsername.text.toString(),
                binding.edtPassword.text.toString()
            )
        }
    }

    override fun finish() {
        super.finish()
        lifecycleScope.cancel()
    }
}