package com.latribu.listadc.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants.Companion.EXTRA_PRODUCT
import com.latribu.listadc.common.factories.ViewModelFactory
import com.latribu.listadc.common.getSerializable
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.models.Status
import com.latribu.listadc.common.network.AppCreator
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViews()
        setListeners()

        setContentView(binding.root)

        mProductViewModel = ViewModelProvider(
            this,
            ViewModelFactory(AppCreator.getApiHelperInstance())
        )[ProductViewModel::class.java]
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

    private fun saveProduct(editing: Boolean) {
        val quantity: Int? = quantity.text.toString().toIntOrNull()

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
            .addProduct(product)
            .observe(this) {
                when(it.status) {
                    Status.SUCCESS -> {
                        val i = Intent(this@SaveProductActivity, MainActivity::class.java)
                        startActivity(i)
                    }
                    Status.LOADING -> {
                        Toast.makeText(this, "Loading...", Toast.LENGTH_LONG).show()
                    }
                    Status.FAILURE -> {
                        val message: String = getString(R.string.saveError, "al guardar: ${it.message}")
                        val snack = Snackbar.make(findViewById(R.id.constraintLayout2), message, Snackbar.LENGTH_SHORT)
                        snack.show()
                    }
                }
            }
    }

    private fun editProduct(product: ProductItem) {
        mProductViewModel
            .editProduct(product)
            .observe(this) {
                when(it.status) {
                    Status.SUCCESS -> {
                        val i = Intent(this@SaveProductActivity, MainActivity::class.java)
                        startActivity(i)
                    }
                    Status.LOADING -> {
                        Toast.makeText(this, "Loading...", Toast.LENGTH_LONG).show()
                    }
                    Status.FAILURE -> {
                        val message: String = getString(R.string.saveError, "al editar el elemento: ${it.message}")
                        val snack = Snackbar.make(findViewById(R.id.constraintLayout2), message, Snackbar.LENGTH_SHORT)
                        snack.show()
                    }
                }
            }
    }
}