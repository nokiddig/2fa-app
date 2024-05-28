package com.example.app_2fa.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.app_2fa.databinding.FragmentSettingBinding
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

    private fun setListener() {
        binding.switch2fa.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView.isChecked = !isChecked
            viewModel.request2FA(buttonView.isChecked)
        }
    }
}