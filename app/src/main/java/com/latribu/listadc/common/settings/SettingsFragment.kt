package com.latribu.listadc.common.settings

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants.Companion.DEFAULT_USER
import com.latribu.listadc.common.factories.UserViewModelFactory
import com.latribu.listadc.common.models.Status
import com.latribu.listadc.common.models.User
import com.latribu.listadc.common.repositories.user.AppCreator
import com.latribu.listadc.common.sendEmail
import com.latribu.listadc.common.viewmodels.PreferencesViewModel
import com.latribu.listadc.common.viewmodels.UserViewModel
import com.latribu.listadc.databinding.ActivitySettingsBinding
import com.latribu.listadc.main.MainActivity
import kotlinx.coroutines.runBlocking

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var mUserViewModel: UserViewModel
    private lateinit var preferencesViewModel: PreferencesViewModel
    private lateinit var savedUser: User
    private lateinit var spinner: ProgressBar
    private lateinit var installationId: String

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        initData()
        getUsers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initData() {
        mUserViewModel = ViewModelProvider(
            this,
            UserViewModelFactory(AppCreator.getApiHelperInstance())
        )[UserViewModel::class.java]
        preferencesViewModel = ViewModelProvider(
            this
        )[PreferencesViewModel::class.java]
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        spinner = binding.spinningHamburger

        val installationPreference: EditTextPreference? = findPreference(getString(R.string.settings_installation_id))
        val firebaseInstance = Observer<String> { data ->
            if (data.isNotEmpty()) {
                if (installationPreference != null) {
                    installationId = data
                    installationPreference.text = data
                }
            }
        }
        MainActivity.firebaseInstanceId.observeForever(firebaseInstance)
    }

    private fun getUsers() {
        mUserViewModel
            .getAllUsers(installationId)
            .observe(this) {
                when(it.status) {
                    Status.SUCCESS -> {
                        loadOptions(it.data!!)
                        spinner.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        spinner.visibility = View.VISIBLE
                    }
                    Status.FAILURE -> {
                        sendEmail(this,
                            viewLifecycleOwner,
                            requireView(),
                            "Error en getUsers",
                            getString(R.string.saveError, DEFAULT_USER.name, "al obtener los usuarios: ${it.message}"),
                            installationId)
                        spinner.visibility = View.GONE
                    }
                }
            }
    }

    private fun saveUser(user: User) = runBlocking {
        binding.apply {
            preferencesViewModel.setUser(user)
        }
    }

    private fun setBuyMode(buyMode: Boolean) = runBlocking {
        binding.apply {
            preferencesViewModel.setBuyMode(buyMode)
        }
    }

    private fun setListeners() {
        val buyModeSwitch: SwitchPreference? = findPreference(getString(R.string.settings_buy_mode))
        buyModeSwitch?.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue ->
            if (newValue.toString().isNotEmpty()) {
                setBuyMode(newValue.toString().toBooleanStrict())
            }
            true
        }
        binding.apply {
            preferencesViewModel.getBuyMode.observe(viewLifecycleOwner){ buyMode ->
                if (buyModeSwitch != null) {
                    buyModeSwitch.isChecked = buyMode
                }
            }
        }
    }

    private fun loadOptions(users: List<User>) {
        val userList: ListPreference? = findPreference(getString(R.string.settings_user))

        val entries: MutableList<String> = ArrayList()
        val entryValues: MutableList<String> = ArrayList()

        users.forEach {
            entries.add(it.name)
            entryValues.add(it.id.toString())
        }
        val charEntries = entries.toTypedArray<CharSequence>()
        val charValues = entryValues.toTypedArray<CharSequence>()

        if (userList != null) {
            setUsername(userList, users)

            userList.entries = charEntries
            userList.entryValues = charValues
            userList.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().isNotEmpty()) {
                    val selectedUser = users.first{ it.id == newValue.toString().toInt() }
                    saveUser(selectedUser)
                }
                true
            }
        } else {
            sendEmail(this,
                viewLifecycleOwner,
                requireView(),
                "Error en SettingsFragment:loadOptions",
                getString(R.string.saveError, savedUser.name, "al obtener los usuarios"),
                installationId)
        }
    }

    private fun setUsername(userList: ListPreference?, users: List<User>) {
        var userName = getString(R.string.settings_text_user_not_defined)
        val defaultText = getString(R.string.settings_user_subtext)
        var resume = "$defaultText: <br><b>$userName</b>"
        userList?.summary = HtmlCompat.fromHtml(resume, FROM_HTML_MODE_LEGACY)
        binding.apply {
            preferencesViewModel.getUser.observe(this@SettingsFragment){ user ->
                savedUser = user
                if (savedUser.id != 0) {
                    val selectedUser = users.first { it.id == savedUser.id }
                    userName = selectedUser.name
                    resume = "$defaultText: <br><b>Soy $userName</b>"
                    userList?.summary = HtmlCompat.fromHtml(resume, FROM_HTML_MODE_LEGACY)
                }
            }
        }
    }
}