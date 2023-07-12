package com.latribu.listadc.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants.Companion.EXTRA_PRODUCT
import com.latribu.listadc.common.factories.ProductViewModelFactory
import com.latribu.listadc.common.getSerializable
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.models.Status
import com.latribu.listadc.common.models.User
import com.latribu.listadc.common.repositories.product.AppCreator
import com.latribu.listadc.common.viewmodels.PreferencesViewModel
import com.latribu.listadc.common.viewmodels.ProductViewModel
import com.latribu.listadc.databinding.ActivityAddBinding

class SaveProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private lateinit var title: TextView
    private lateinit var name: TextInputEditText
    private lateinit var nameLayout: TextInputLayout
    private lateinit var quantity: TextInputEditText
    private lateinit var comment: TextInputEditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private var product: ProductItem? = null
    private lateinit var mProductViewModel: ProductViewModel
    private lateinit var preferencesViewModel: PreferencesViewModel
    private lateinit var savedUser: User
    private lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViews()
        initData()
        getUser()
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
    }

    private fun getUser() {
        binding.apply {
            preferencesViewModel.getUser.observe(this@SaveProductActivity) { data ->
                savedUser = data
                ListFragment.user.postValue(data!!)
            }
        }
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

        title = binding.lblAddProduct
        saveButton = binding.btnSaveProduct
        cancelButton = binding.btnCancel
        name = binding.txtName
        nameLayout = binding.loName
        quantity = binding.txtQuantity
        comment = binding.txtComments
    }

    private fun getData(): Boolean {
        // Check if exists sent data to fill the fields
        product = getSerializable(this, EXTRA_PRODUCT, ProductItem::class.java)
        val editing: Boolean = product != null && product!!.id != -1

        if (editing) {
            name.setText(product?.name)
            quantity.setText(product?.quantity.toString())
            comment.setText(product?.comment)
            title.text = getString(R.string.save_save_title, product?.name)
        }

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
            .addProduct(product, savedUser)
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
                        spinner.visibility = View.GONE
                        val message: String = getString(R.string.saveError, "al guardar: ${it.message}")
                        val snack = Snackbar.make(findViewById(R.id.constraintLayout2), message, Snackbar.LENGTH_SHORT)
                        snack.show()
                    }
                }
            }
    }

    private fun editProduct(product: ProductItem) {
        mProductViewModel
            .editProduct(product, savedUser)
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
                        val message: String = getString(R.string.saveError, "al editar el elemento: ${it.message}")
                        val snack = Snackbar.make(findViewById(R.id.constraintLayout2), message, Snackbar.LENGTH_SHORT)
                        snack.show()
                        spinner.visibility = View.GONE
                    }
                }
            }
    }
}