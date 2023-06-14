package com.latribu.listadc.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.firebase.messaging.FirebaseMessaging
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants.Companion.TOPIC_NAME
import com.latribu.listadc.common.SectionsPagerAdapter
import com.latribu.listadc.common.models.User
import com.latribu.listadc.common.network.FirebaseMessagingService
import com.latribu.listadc.common.settings.SettingsActivity
import com.latribu.listadc.common.showMessage
import com.latribu.listadc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsButton: ImageButton
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTabs(binding)
        setButtons(binding)
        setFirebase()
        readFirebaseMessage()
        readPreferences(binding)
    }

    private fun setTabs(binding: ActivityMainBinding) {
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
    }

    private fun setButtons(binding: ActivityMainBinding) {
        settingsButton = binding.settingsButton
        settingsButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setFirebase() {
        auth = Firebase.auth

        FirebaseMessaging
            .getInstance()
            .subscribeToTopic(TOPIC_NAME)
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d(TAG, msg)
            }
    }

    private fun readFirebaseMessage() {
        val messageObserver = Observer<String> { data ->
            showMessage(findViewById(R.id.app_container), data)
        }
        FirebaseMessagingService.notificationMessage.observeForever(messageObserver)
    }

    private fun readPreferences(binding: ActivityMainBinding) {
        val userObserver = Observer<User> { data ->
            binding.userName.text = data.name
        }
        val buyModeObserver = Observer<Boolean> { data ->
            if (data) {
                binding.buymodeButton.visibility = View.VISIBLE
                binding.buymodeButton.setBackgroundResource(R.drawable.twotone_buymode_on_24)
            }
            else
                binding.buymodeButton.visibility = View.INVISIBLE

        }
        Handler(Looper.getMainLooper()).post {
            ListFragment.user.observeForever(userObserver)
            ListFragment.buyMode.observeForever(buyModeObserver)
        }
    }
}