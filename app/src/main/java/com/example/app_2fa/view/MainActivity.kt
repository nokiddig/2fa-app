package com.example.app_2fa.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.app_2fa.R
import com.example.app_2fa.databinding.ActivityLoginBinding
import com.example.app_2fa.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)
        //val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation)

        // Set initial fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, HomeFragment())
                .commit()
        }

        binding.bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            var selectedFragment: Fragment? = null
            when (menuItem.itemId) {
                R.id.tab_home -> {
                    selectedFragment = HomeFragment()
                }
                R.id.tab_setting -> {
                    selectedFragment = SettingFragment()
                }
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(binding.frameLayout.id, selectedFragment)
                    .commit()
            }
            true
        }
    }
}