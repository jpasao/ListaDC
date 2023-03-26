package com.latribu.listadc

import android.content.Intent
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.latribu.listadc.ui.main.SectionsPagerAdapter
import com.latribu.listadc.databinding.ActivityMainBinding
import com.latribu.listadc.models.Product
import com.latribu.listadc.network.RestApiManager
import com.latribu.listadc.ui.main.MainViewModel
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsButton: ImageButton
    private lateinit var fabButton: FloatingActionButton
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiService = RestApiManager()

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
            val i = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(i)
        }

        fabButton = binding.fab
        fabButton.setOnClickListener{
            val i = Intent(this@MainActivity, AddActivity::class.java)
            startActivity(i)
        }

        val mainResponse: LiveData<Response<Product>> = liveData{
            val response = apiService.getProducts("")
            emit(response)
        }

        mainResponse.observe(this) {
            val response = it.body()
            mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
            mainViewModel.arrayListLiveData.postValue(response)
        }
    }
}