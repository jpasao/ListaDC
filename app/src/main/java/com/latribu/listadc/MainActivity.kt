package com.latribu.listadc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.google.android.material.snackbar.Snackbar
import com.latribu.listadc.ui.main.SectionsPagerAdapter
import com.latribu.listadc.databinding.ActivityMainBinding
import com.latribu.listadc.models.Product
import com.latribu.listadc.network.ProductService
import com.latribu.listadc.network.RetrofitInstance
import com.latribu.listadc.ui.main.MainViewModel
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsButton: ImageButton

    private lateinit var mainViewModel: MainViewModel

    private lateinit var retrofitInstance: ProductService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        settingsButton = binding.settingsButton

        settingsButton.setOnClickListener {
            val i = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(i)
        }
        retrofitInstance = RetrofitInstance
            .getRetrofitInstance()
            .create(ProductService::class.java)

        val mainResponse: LiveData<Response<Product>> = liveData{
            val response = retrofitInstance.getProducts()
            emit(response)
        }

        mainResponse.observe(this, Observer{
            val response = it.body()
            mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
            mainViewModel.arrayListLiveData.postValue(response)
        })
    }
}