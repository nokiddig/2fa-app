package com.example.app_2fa.view.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.app_2fa.data.SocketManager
import com.example.app_2fa.databinding.ActivityLoginBinding
import com.example.app_2fa.utils.SaveData
import com.example.app_2fa.view.dialog.DialogOTP
import com.example.app_2fa.view.main.MainActivity
import com.example.app_2fa.viewmodel.LoginViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@Suppress("UNREACHABLE_CODE")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

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

                    0 -> {
                        Toast.makeText(applicationContext, "Login fail!", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
        lifecycleScope.launch {
            loginViewModel.otpState.collect { otpSt ->
                when (otpSt) {
                    0 -> {
                        Toast.makeText(applicationContext, "OTP wrong!", Toast.LENGTH_SHORT).show()
                        SocketManager.getInstance().resetOTPState()
                    }
                }
            }
        }
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
        binding.tvRegister.setOnClickListener {
            goToRegister()
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

    private  fun goToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    override fun finish() {
        super.finish()
        lifecycleScope.cancel()
    }
}