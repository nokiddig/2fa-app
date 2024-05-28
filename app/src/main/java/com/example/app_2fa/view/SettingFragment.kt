package com.example.app_2fa.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.app_2fa.databinding.FragmentSettingBinding
import com.example.app_2fa.utils.SaveData
import com.example.app_2fa.viewmodel.SettingViewModel

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private val viewModel: SettingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        setListener()
        return binding.root
        //return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.switch2fa.isChecked = SaveData.IS_2FA
    }
    private fun setListener() {
        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.switch2fa.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView.isChecked = !isChecked
            //da bat -> chuyen sang tat
            if (buttonView.isChecked){
                val dialog = context?.let {
                    DialogOTP(context = it)
                }
                dialog?.show()
            }
            else {
                val dialog = context?.let {
                    DialogOn2fa(context = it)
                }
                dialog?.show()
            }
            viewModel.request2FA(buttonView.isChecked)
        }
    }

    private fun logout() {
        context?.let { SaveData(it).updateLoginStatus(false) }
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finishAffinity()
    }
}