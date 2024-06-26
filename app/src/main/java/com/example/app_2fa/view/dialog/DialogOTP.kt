package com.example.app_2fa.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import com.example.app_2fa.data.SocketManager
import com.example.app_2fa.databinding.DialogInputOtpBinding
import com.example.app_2fa.utils.SaveData

class DialogOTP(context: Context) : Dialog(context) {
    private lateinit var binding: DialogInputOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogInputOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.dialog_input_otp)
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, // Chiều rộng tùy chỉnh
            ViewGroup.LayoutParams.WRAP_CONTENT  // Chiều cao tùy chỉnh
        )
        window?.setLayout(layoutParams.width, layoutParams.height)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setListener()
    }

    private fun setListener() {
        binding.tvYes.setOnClickListener {
            val code = binding.edtOTP.text.toString()
            SocketManager.getInstance().sendMessage("xacminh2fa ${SaveData.USERNAME} ${SaveData.PASSWORD} $code")
            dismiss()
        }
        binding.tvNo.setOnClickListener {
            dismiss()
        }
    }
}
