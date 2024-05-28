package com.example.app_2fa.view.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.app_2fa.databinding.FragmentSettingBinding
import com.example.app_2fa.utils.SaveData
import com.example.app_2fa.view.dialog.DialogOn2fa
import com.example.app_2fa.view.login.LoginActivity
import com.example.app_2fa.viewmodel.SettingViewModel
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private val viewModel: SettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        setListener()
        viewModel.initialize()
        lifecycleScope.launch {
            viewModel.twoFAState.collect { isOn ->
                binding.switch2fa.isChecked = isOn
                SaveData(requireContext()).update2faMode(isOn)
            }
//            viewModel.genKeyState.collect{state ->
//
//            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("bug_2fa", "${SaveData.IS_2FA} ${SaveData.IS_2FA}")
        binding.switch2fa.isChecked = SaveData.IS_2FA
    }

    private fun setListener() {
        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.switch2fa.setOnClickListener { buttonView ->
            var curFactorState = !binding.switch2fa.isChecked
            //da bat -> chuyen sang tat
            binding.switch2fa.isChecked = curFactorState
            if (curFactorState){
                //SaveData(requireContext()).update2faMode(false)

            }
            else {
                val dialog = context?.let {
                    DialogOn2fa(context = it)
                }
                dialog?.show()
            }
            viewModel.request2FA(curFactorState)
        }
    }

    private fun logout() {
        val saveData = SaveData(requireActivity())
        SaveData.IS_LOGIN = false
        saveData.updateLoginStatus(false)
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        requireActivity().startActivity(intent)
    }

}