package com.latribu.listadc.common.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.latribu.listadc.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

}