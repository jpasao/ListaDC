package com.latribu.listadc.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.latribu.listadc.R
import com.latribu.listadc.databinding.ActivityAddBinding
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.network.RestApiManager

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private lateinit var name: TextInputEditText
    private lateinit var nameLayout: TextInputLayout
    private lateinit var quantity: TextInputEditText
    private lateinit var comments: TextInputEditText
    private lateinit var saveButton: Button
    private val apiService = RestApiManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBinding.inflate(layoutInflater)

        saveButton = binding.btnSaveProduct
        name = binding.txtName
        nameLayout = binding.loName
        quantity = binding.txtQuantity
        comments = binding.txtComments

        name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.isNotEmpty()) {
                    saveButton.isEnabled = true
                } else {
                    saveButton.isEnabled = false
                    nameLayout.error = R.string.validationErrorName.toString()
                }
            }
        })

        saveButton.setOnClickListener {
            val qtity: Int? = quantity.text.toString().toIntOrNull()
            val product = ProductItem(
                id = null,
                name = name.text.toString(),
                quantity = qtity,
                isChecked = "0",
                comments = comments.text.toString())

            addProduct(product)
        }

        setContentView(binding.root)
    }

    private fun addProduct(product: ProductItem) {
        apiService.addProduct(product) {
            if (it != null) {
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
            } else {
                val message: String = R.string.saveError.toString()
                val snack = Snackbar.make(findViewById(R.id.constraintLayout2), message, Snackbar.LENGTH_SHORT)
                snack.show()
            }
        }
    }
}