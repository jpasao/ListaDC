package com.latribu.listadc.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.latribu.listadc.R
import com.latribu.listadc.common.EXTRA_PRODUCT
import com.latribu.listadc.common.getSerializable
import com.latribu.listadc.databinding.ActivityAddBinding
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.network.RestApiManager

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private lateinit var title: TextView
    private lateinit var name: TextInputEditText
    private lateinit var nameLayout: TextInputLayout
    private lateinit var quantity: TextInputEditText
    private lateinit var comment: TextInputEditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private var product: ProductItem? = null
    private val apiService = RestApiManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViews()

        val editing: Boolean = getData()
        saveButton.isEnabled = editing

        name.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.isNotEmpty()) {
                    saveButton.isEnabled = true
                } else {
                    saveButton.isEnabled = false
                    nameLayout.error = getString(R.string.validationErrorName)
                }
            }
        })

        saveButton.setOnClickListener { saveProduct(editing) }

        cancelButton.setOnClickListener { finish() }

        setContentView(binding.root)
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
        // Check if getting data to fill the fields
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
        apiService.saveProduct(product) {
            if (it != null) {
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
            } else {
                val message: String = getString(R.string.saveError)
                val snack = Snackbar.make(findViewById(R.id.constraintLayout2), message, Snackbar.LENGTH_SHORT)
                snack.show()
            }
        }
    }
}