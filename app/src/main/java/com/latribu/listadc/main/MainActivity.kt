package com.latribu.listadc.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants.Companion.TOPIC_NAME
import com.latribu.listadc.common.TAB_TITLES
import com.latribu.listadc.common.TabsAdapter
import com.latribu.listadc.common.models.User
import com.latribu.listadc.common.network.FirebaseMessagingService
import com.latribu.listadc.common.settings.SettingsActivity
import com.latribu.listadc.common.showMessage
import com.latribu.listadc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsButton: ImageButton
    private lateinit var auth: FirebaseAuth
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private var previousTab: Int = 0

    companion object {
        // Observed in SettingsFragment.initData() and
        // ListFragment.getInstallationId()
        val firebaseInstanceId = MutableLiveData<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setTabs(binding)
        setContentView(binding.root)
        setButtons(binding)
        setFirebase()
        readFirebaseMessage()
        readPreferences(binding)
    }

    override fun onBackPressed() {
        val tab: Tab = tabLayout.getTabAt(previousTab)!!
        tab.select()
    }

    private fun setTabs(binding: ActivityMainBinding) {
        viewPager = binding.viewPager

        tabLayout = binding.tabs
        viewPager.adapter = TabsAdapter(supportFragmentManager, lifecycle)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getString(TAB_TITLES[position])
        }.attach()

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab) { }
            override fun onTabReselected(tab: Tab) { }
            override fun onTabUnselected(tab: Tab) {
                previousTab = tab.position
            }
        })
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
                if (!task.isSuccessful) {
                    showMessage(findViewById(R.id.app_container), getString(R.string.firebase_not_subscribed))
                }
            }

        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseInstanceId.postValue(task.result)
            } else {
                showMessage(findViewById(R.id.app_container), getString(R.string.firebase_installation_id))
            }
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