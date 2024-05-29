package com.example.app_2fa.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.AppBarConfiguration
import com.example.app_2fa.data.SocketManager
import com.example.app_2fa.databinding.ActivityRegisterBinding
import com.example.app_2fa.utils.SaveData
import com.example.app_2fa.viewmodel.RegisterViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@Suppress("UNREACHABLE_CODE")
class RegisterActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()
        viewModel.initialize()
        lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                Log.d("bug_2fa", "login 1")
                if (state >= 0) {
                    var message = ""
                    message = if (state == 1) {
                        "Create account successful."
                    } else {
                        "Create account fail."
                    }
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    SocketManager.getInstance().resetRegisterState()
                    if (state == 1) {
                        goToLogin()
                    }
                }
            }

        }
    }

    private fun isValidateRegister(
        username: String,
        password: String,
        rePassword: String
    ): Boolean {
        val passwordPattern = "^[A-Za-z0-9]{5,20}\$"
        val usernamePattern = "^[A-Za-z0-9]{3,20}\$"
        return password == rePassword && username.matches(usernamePattern.toRegex()) && password.matches(
            passwordPattern.toRegex()
        )
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    private fun setListener() {
        binding.ivBack.setOnClickListener {
            goToLogin()
        }

        binding.btLogin.setOnClickListener {
            val username = binding.edtUsername.text.toString()
            val password = binding.edtPassword.text.toString()
            val rePassword = binding.edtRepassword.text.toString()

            if (isValidateRegister(username, password, rePassword)) {
                SaveData(this).updateAccount(username, password, "")
                viewModel.sendRegisterMessage(username, password)
            } else {
                showAlertDialog()
            }
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Invalid input")
        builder.setMessage("- Username at least characters.\n- Password must be between 5 and 20 characters. \n- Password and re-password are the same.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun finish() {
        super.finish()
        lifecycleScope.cancel()
    }
}