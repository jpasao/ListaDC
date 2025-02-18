package com.latribu.listadc.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants.Companion.MAIN_TOPIC
import com.latribu.listadc.common.Constants.Companion.MEAL_TOPIC
import com.latribu.listadc.common.Constants.Companion.TAB_MAINLIST
import com.latribu.listadc.common.Constants.Companion.TAB_MEALS
import com.latribu.listadc.common.Constants.Companion.TAB_OTHERS
import com.latribu.listadc.common.TAB_TITLES
import com.latribu.listadc.common.TabsAdapter
import com.latribu.listadc.common.models.User
import com.latribu.listadc.common.network.FirebaseMessagingService
import com.latribu.listadc.common.settings.SettingsActivity
import com.latribu.listadc.common.showMessage
import com.latribu.listadc.databinding.ActivityMainBinding
import com.latribu.listadc.historic.HistoricActivity
import com.latribu.listadc.meals.MealFragment
import com.latribu.listadc.others.OtherFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsButton: ImageButton
    private lateinit var undoButton: ImageButton
    private lateinit var historicButton: ImageButton
    private lateinit var auth: FirebaseAuth
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var selectedTab: Fragment
    private var previousTab: Int = 0
    private var currentTab: Int = 0

    companion object {
        // Observed in SettingsFragment.initData() and
        // getInstallationId() of ListFragment and MealFragment and MealIngredientsActivity
        val firebaseInstanceId = MutableLiveData<String>()
        // Observed in ListFragment, OtherFragment and MealFragment
        val undoAction = MutableLiveData<Int>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedTab = ListFragment()
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setTabs(binding)
        setContentView(binding.root)
        setToolbarButtons(binding)
        setFirebase()
        readFirebaseMessage()
        readPreferences(binding)
        getBadges()
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    @SuppressLint("MissingSuperCall")
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
            tab.orCreateBadge.number = 0
            tab.badge?.backgroundColor = ContextCompat.getColor(baseContext, R.color.teal_700)
            tab.badge?.clearNumber()
            tab.badge?.horizontalOffset = -20
        }.attach()

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab) {
                currentTab = tab.position
                selectedTab = when(tab.position) {
                    TAB_MAINLIST -> { ListFragment() }
                    TAB_MEALS -> { MealFragment() }
                    TAB_OTHERS -> { OtherFragment() }
                    else -> { ListFragment() }
                }
            }
            override fun onTabReselected(tab: Tab) { }
            override fun onTabUnselected(tab: Tab) {
                previousTab = tab.position
            }
        })
    }

    private fun setToolbarButtons(binding: ActivityMainBinding) {
        settingsButton = binding.toolbarContainer.settingsButton
        settingsButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
        undoButton = binding.toolbarContainer.undo
        undoButton.setOnClickListener {
            undoAction.postValue(currentTab)
        }
        historicButton = binding.toolbarContainer.historic
        historicButton.setOnClickListener {
            val intent = Intent(this@MainActivity, HistoricActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setFirebase() {
        auth = FirebaseAuth.getInstance()

        FirebaseMessaging
            .getInstance()
            .subscribeToTopic(MAIN_TOPIC)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    showMessage(findViewById(R.id.app_container), getString(R.string.firebase_not_subscribed, "de la compra"))
                }
            }

        FirebaseMessaging
            .getInstance()
            .subscribeToTopic(MEAL_TOPIC)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    showMessage(findViewById(R.id.app_container), getString(R.string.firebase_not_subscribed, "de comidas"))
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
            binding.toolbarContainer.user.text = data.name.subSequence(0, 1)
        }
        val buyModeObserver = Observer<Boolean> { data ->
            val visible = if (data) View.VISIBLE else View.INVISIBLE
            binding.toolbarContainer.buyMode.visibility = visible
        }
        Handler(Looper.getMainLooper()).post {
            ListFragment.user.observeForever(userObserver)
            ListFragment.buyMode.observeForever(buyModeObserver)
        }
    }

    private fun getBadges() {
        val listObserver = Observer<Int> { number -> setBadges(number, TAB_MAINLIST) }
        val mealObserver = Observer<Int> { number -> setBadges(number, TAB_MEALS) }
        val otherObserver = Observer<Int> { number -> setBadges(number, TAB_OTHERS) }
        ListFragment.listFragmentBadge.observeForever(listObserver)
        MealFragment.mealFragmentBadge.observeForever(mealObserver)
        OtherFragment.otherFragmentBadge.observeForever(otherObserver)
    }

    private fun setBadges(badgeValue: Int, selectedTab: Int) {
        val tab = binding.tabs.getTabAt(selectedTab)
        if (tab != null) {
            if (badgeValue != 0) {
                tab.orCreateBadge.number = badgeValue
            } else {
                tab.removeBadge()
            }
        }
    }
}