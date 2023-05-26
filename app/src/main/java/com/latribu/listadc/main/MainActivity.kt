package com.latribu.listadc.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.firebase.messaging.FirebaseMessaging
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants.Companion.TOPIC_NAME
import com.latribu.listadc.common.EXTRA_PRODUCT
import com.latribu.listadc.common.MainViewModel
import com.latribu.listadc.common.SectionsPagerAdapter
import com.latribu.listadc.common.models.Product
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.network.RestApiManager
import com.latribu.listadc.common.settings.SettingsActivity
import com.latribu.listadc.databinding.ActivityMainBinding
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsButton: ImageButton
    private lateinit var fabAddProduct: FloatingActionButton
    private lateinit var mainViewModel: MainViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        // Buttons
        settingsButton = binding.settingsButton
        settingsButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }

        fabAddProduct = binding.fabAddProduct
        fabAddProduct.setOnClickListener{
            val intent = Intent(this@MainActivity, AddProductActivity::class.java)
            val product = ProductItem(-1, "", "", -1, "")

            intent.putExtra(EXTRA_PRODUCT, product)
            startActivity(intent)
        }

        requestList()

        // Authenticate to Firebase
        auth = Firebase.auth

        // Subscribe to Firebase notifications
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_NAME)
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d(TAG, msg)
            }
    }

    fun requestList() {
        val apiService = RestApiManager()

        val mainResponse: LiveData<Response<Product>> = liveData{
            val response = apiService.getProducts("")
            emit(response)
        }

        mainResponse.observe(this) {
            val response = it.body()
            if (response is Product) {
                mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
                mainViewModel.arrayListLiveData.postValue(response)
            } else {
                val snack = Snackbar.make(findViewById(R.id.app_container), R.string.getProductsError, Snackbar.LENGTH_SHORT)
                snack.show()
            }
        }
    }
}