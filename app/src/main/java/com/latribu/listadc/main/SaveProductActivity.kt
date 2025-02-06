package com.latribu.listadc.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants.Companion.EXTRA_PRODUCT
import com.latribu.listadc.common.Constants.Companion.OPACITY_FADED
import com.latribu.listadc.common.factories.ProductViewModelFactory
import com.latribu.listadc.common.getSerializable
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.models.Status
import com.latribu.listadc.common.models.User
import com.latribu.listadc.common.repositories.product.AppCreator
import com.latribu.listadc.common.sendEmail
import com.latribu.listadc.common.settings.SettingsActivity
import com.latribu.listadc.common.showYesNoDialog
import com.latribu.listadc.common.viewmodels.PreferencesViewModel
import com.latribu.listadc.common.viewmodels.ProductViewModel
import com.latribu.listadc.databinding.ActivityAddBinding
import com.latribu.listadc.historic.HistoricActivity

class SaveProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private lateinit var name: TextInputEditText
    private lateinit var nameLayout: TextInputLayout
    private lateinit var quantity: TextInputEditText
    private lateinit var comment: TextInputEditText
    private lateinit var deleteButton: Button
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private var product: ProductItem? = null
    private lateinit var mProductViewModel: ProductViewModel
    private lateinit var preferencesViewModel: PreferencesViewModel
    private lateinit var savedUser: User
    private lateinit var spinner: ProgressBar
    private var installationId: String = ""
    private lateinit var settingsButton: ImageButton
    private lateinit var undoButton: ImageButton
    private lateinit var historicButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViews()
        setToolbarButtons()
        readPreferences()
        initData()
        getUser()
        getInstallationId()
        setListeners()
        setContentView(binding.root)
    }

    private fun initData() {
        mProductViewModel = ViewModelProvider(
            this,
            ProductViewModelFactory(AppCreator.getApiHelperInstance())
        )[ProductViewModel::class.java]

        preferencesViewModel = ViewModelProvider(
            this
        )[PreferencesViewModel::class.java]
    }

    private fun setListeners() {
        name.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    saveButton.isEnabled = true
                } else {
                    saveButton.isEnabled = false
                    nameLayout.error = getString(R.string.validationErrorName)
                }
            }
        })

        val editing: Boolean = getData()
        saveButton.isEnabled = editing
        saveButton.setOnClickListener { saveProduct(editing) }

        cancelButton.setOnClickListener { finish() }
        deleteButton.setOnClickListener { askForConfirmation(product!!) }
        deleteButton.isVisible = editing
    }

    private fun getUser() {
        binding.apply {
            preferencesViewModel.getUser.observe(this@SaveProductActivity) { data ->
                savedUser = data
                ListFragment.user.postValue(data!!)
            }
        }
    }

    private fun getInstallationId() {
        val firebaseInstance = Observer<String> { data ->
            if (data.isNotEmpty()) {
                installationId = data
            }
        }
        MainActivity.firebaseInstanceId.observeForever(firebaseInstance)
    }

    private fun saveProduct(editing: Boolean) {
        val quantity: Int = quantity.text.toString().toIntOrNull() ?: 1

        ProductItem(
            id = if (editing) product?.id else null,
            name = name.text.toString(),
            quantity = quantity,
            isChecked = "0",
            comment = comment.text.toString()
        ).apply {
            saveProduct(this)
        }
    }

    private fun bindViews() {
        binding = ActivityAddBinding.inflate(layoutInflater)
        spinner = binding.spinningHamburger
        spinner.visibility = View.GONE

        saveButton = binding.btnSaveProduct
        cancelButton = binding.btnCancel
        name = binding.txtName
        nameLayout = binding.loName
        quantity = binding.txtQuantity
        comment = binding.txtComments
        deleteButton = binding.btnDelete
    }

    private fun setToolbarButtons() {
        settingsButton = binding.toolbarContainer.settingsButton
        settingsButton.setOnClickListener {
            val intent = Intent(this@SaveProductActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
        undoButton = binding.toolbarContainer.undo
        undoButton.alpha = OPACITY_FADED
        historicButton = binding.toolbarContainer.historic
        historicButton.setOnClickListener {
            val intent = Intent(this@SaveProductActivity, HistoricActivity::class.java)
            startActivity(intent)
        }
    }

    private fun readPreferences() {
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

    private fun getData(): Boolean {
        // Check if exists sent data to fill the fields
        product = getSerializable(this, EXTRA_PRODUCT, ProductItem::class.java)
        val editing: Boolean = product != null && product!!.id != -1

        if (editing) {
            name.setText(product?.name)
            quantity.setText(product?.quantity.toString())
            comment.setText(product?.comment)
            binding.toolbarContainer.appName.text = getString(R.string.save_edit)
        } else {
            binding.toolbarContainer.appName.text = getString(R.string.save_add)
        }
        deleteButton.isVisible = editing
        return editing
    }

    private fun saveProduct(product: ProductItem) {
        if (product.id == null) {
            addProduct(product)
        } else {
            editProduct(product)
        }
    }

    private fun addProduct(product: ProductItem) {
        mProductViewModel
            .addProduct(product, savedUser, installationId)
            .observe(this) {
                when(it.status) {
                    Status.SUCCESS -> {
                        val i = Intent(this@SaveProductActivity, MainActivity::class.java)
                        startActivity(i)
                        spinner.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        spinner.visibility = View.VISIBLE
                    }
                    Status.FAILURE -> {
                        val savedUserName: String = savedUser.name.ifEmpty { installationId }
                        spinner.visibility = View.GONE
                        sendEmail(this,
                            this,
                            findViewById(R.id.constraintLayout2),
                            "Error en addProduct",
                            getString(R.string.saveError, savedUserName, "al guardar un elemento: ${it.message}"),
                            installationId)
                    }
                }
            }
    }

    private fun editProduct(product: ProductItem) {
        mProductViewModel
            .editProduct(product, savedUser, installationId)
            .observe(this) {
                when(it.status) {
                    Status.SUCCESS -> {
                        val i = Intent(this@SaveProductActivity, MainActivity::class.java)
                        startActivity(i)
                        spinner.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        spinner.visibility = View.VISIBLE
                    }
                    Status.FAILURE -> {
                        val savedUserName: String = savedUser.name.ifEmpty { installationId }
                        sendEmail(this,
                            this,
                            findViewById(R.id.constraintLayout2),
                            "Error en editProduct",
                            getString(R.string.saveError, savedUserName, "al editar un elemento: ${it.message}"),
                            installationId)
                        spinner.visibility = View.GONE
                    }
                }
            }
    }

    private fun itemDeleted(listItem: ProductItem) {
        mProductViewModel
            .deleteProduct(listItem, savedUser, installationId)
            .observe(this) {
                when (it.status) {
                    Status.SUCCESS -> {
                        val i = Intent(this@SaveProductActivity, MainActivity::class.java)
                        startActivity(i)
                        spinner.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        spinner.visibility = View.VISIBLE
                    }
                    Status.FAILURE -> {
                        val savedUserName: String = savedUser.name.ifEmpty { installationId }
                        sendEmail(this,
                            this,
                            findViewById(R.id.constraintLayout2),
                            "Error en SaveProductActivity:itemDeleted",
                            getString(R.string.saveError, savedUserName, "al borrar un elemento: ${it.message}"),
                            installationId)
                        spinner.visibility = View.GONE
                    }
                }
            }
    }

    private fun askForConfirmation(item: ProductItem) {
        showYesNoDialog(
            this@SaveProductActivity,
            getString(R.string.delete_dialog_title),
            getString(R.string.delete_dialog_message, item.name)
        ) { _, _ -> itemDeleted(item) }
    }
}