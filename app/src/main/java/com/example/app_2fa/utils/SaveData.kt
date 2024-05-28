package com.example.app_2fa.utils

import android.content.Context
import android.content.SharedPreferences

class SaveData(val context: Context) {
    companion object {
        var IS_LOGIN = false
        var IS_SAVE = false
        var IS_2FA = false
        var USERNAME = ""
        var PASSWORD = ""
        var EMAIL = ""
    }

    private val preferenceName = "save_account"
    private val isLoginLabel = "is_login"
    private val isSaveLabel = "is_save"
    private val is2FALabel = "is_2fa"
    private val usernameLabel = "username"
    private val passwordLabel = "password"
    private val emailLabel = "email"
    var sharedPreferences : SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        IS_LOGIN = sharedPreferences.getBoolean(isLoginLabel, false)
        IS_SAVE = sharedPreferences.getBoolean(isSaveLabel, false)
        IS_2FA = sharedPreferences.getBoolean(is2FALabel, false)
        USERNAME = sharedPreferences.getString(usernameLabel, "").toString()
        PASSWORD = sharedPreferences.getString(passwordLabel, "").toString()
        EMAIL = sharedPreferences.getString(emailLabel, "").toString()
    }

    fun updateData(isLogin: Boolean, isSave:Boolean, is2fa: Boolean, username: String, password: String, email: String) {
        IS_LOGIN = isLogin
        IS_2FA = is2fa
        IS_SAVE = isSave
        USERNAME = username
        PASSWORD = password
        EMAIL = email
        val editor = sharedPreferences.edit()
        editor.putBoolean(isLoginLabel, isLogin)
        editor.putBoolean(is2FALabel, is2fa)
        editor.putBoolean(isSaveLabel, isSave)
        editor.putString(usernameLabel, username)
        editor.putString(passwordLabel, password)
        editor.putString(emailLabel, email)
        editor.apply()
    }

    fun updateLoginStatus(isLogin: Boolean){
        IS_LOGIN = isLogin
        val editor = sharedPreferences.edit()
        editor.putBoolean(isLoginLabel, isLogin)
        editor.apply()
    }

    fun updateAccount(username: String, password: String, email: String) {
        USERNAME = username
        PASSWORD = password
        EMAIL = email
        val editor = sharedPreferences.edit()
        editor.putString(usernameLabel, username)
        editor.putString(passwordLabel, password)
        editor.putString(emailLabel, email)
        editor.apply()
    }

    fun update2faMode(mode: Boolean) {
        IS_2FA = mode
        val editor = sharedPreferences.edit()
        editor.putBoolean(is2FALabel, mode)
        editor.apply()
    }
}