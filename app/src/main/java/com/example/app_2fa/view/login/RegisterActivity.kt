package com.example.app_2fa.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
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
    private val serverAddress = "107.178.102.172:3000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()
        viewModel.initialize()
        lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                    Log.d("bug_2fa", "login 1")
                if (state>=0){
                    var message = ""
                    if (state == 1){
                        message = "Create account successful."
                    }
                    else {
                        message = "Create account fail."
                    }
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    SocketManager.getInstance().clearRegisterState()
                }
                }

            }
        }

    private fun setListener() {
        binding.ivBack.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        binding.btLogin.setOnClickListener {
            SaveData(this).updateAccount(binding.edtUsername.text.toString(),
                binding.edtPassword.text.toString(), "")
            viewModel.sendRegisterMessage(
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