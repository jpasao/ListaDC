package com.latribu.listadc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setTitle(R.string.settings_text_title)
        if (findViewById<View?>(R.id.idFrameLayout) != null) {
            if (savedInstanceState != null) {
                return
            }
            supportFragmentManager
                .beginTransaction()
                .add(R.id.idFrameLayout, SettingsFragment())
                .commit()
        }
    }
}