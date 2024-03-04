package com.latribu.listadc.meals

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants.Companion.EXTRA_MEAL
import com.latribu.listadc.common.Constants.Companion.SNACKBAR_DURATION
import com.latribu.listadc.common.adapters.IngredientsAdapter
import com.latribu.listadc.common.adapters.ProductAdapter
import com.latribu.listadc.common.factories.MealViewModelFactory
import com.latribu.listadc.common.factories.ProductViewModelFactory
import com.latribu.listadc.common.getSerializable
import com.latribu.listadc.common.models.Meal
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.models.SelectedIngredient
import com.latribu.listadc.common.models.Status
import com.latribu.listadc.common.repositories.product.AppCreator
import com.latribu.listadc.common.sendEmail
import com.latribu.listadc.common.viewmodels.MealViewModel
import com.latribu.listadc.common.viewmodels.ProductViewModel
import com.latribu.listadc.databinding.ActivityMealIngredientsBinding
import com.latribu.listadc.main.MainActivity


class MealIngredientsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMealIngredientsBinding
    private lateinit var recyclerview: RecyclerView
    private lateinit var recyclerAdapter: IngredientsAdapter
    private var meal: Meal? = null
    private lateinit var mealName: TextView
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var chipGroup: ChipGroup
    private lateinit var spinner: ProgressBar
    private lateinit var search: SearchView
    private lateinit var noResults: TextView
    private lateinit var mProductViewModel: ProductViewModel
    private var installationId: String = ""
    private lateinit var ingredientList: RecyclerView
    private lateinit var mMealViewModel: MealViewModel
    private var selectedIngredients: MutableList<SelectedIngredient> = mutableListOf()
    private lateinit var ingredients: List<ProductItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        meal = getSerializable(this, EXTRA_MEAL, Meal::class.java)

        if (meal != null) {
            getInstallationId()
            bindElements(binding)
            setListeners()
            setRecycler(binding)
            getListStatus()
        } else {
            val message = getString(R.string.meal_no_meal_received)
            Snackbar.make(findViewById(R.id.app_container), message, SNACKBAR_DURATION).show()
        }
    }

    private fun bindElements(binding: ActivityMealIngredientsBinding) {
        mealName = binding.mealName
        saveButton = binding.saveMeal
        saveButton.isEnabled = false
        cancelButton = binding.cancelButton
        mealName.text = getString(R.string.meal_add_title, meal?.name)
        chipGroup = binding.selectedIngredients
        spinner = binding.spinningHamburger
        search = binding.productSearch
        noResults = binding.noResults
        ingredientList = binding.ingredientList
    }

    private fun setListeners() {
        cancelButton.setOnClickListener {
            finish()
        }
        saveButton.setOnClickListener {
            saveIngredients()
        }

        search.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                recyclerAdapter.filter.filter(newText)
                if (newText.isNullOrEmpty()) search.clearFocus()
                return false
            }
        })
    }


    private fun initData() {
        mProductViewModel = ViewModelProvider(
            this,
            ProductViewModelFactory(AppCreator.getApiHelperInstance())
        )[ProductViewModel::class.java]

        mMealViewModel = ViewModelProvider(
            this,
            MealViewModelFactory(com.latribu.listadc.common.repositories.meal.AppCreator.getApiHelperInstance())
        )[MealViewModel::class.java]

        recyclerAdapter = IngredientsAdapter(
            checkBoxClickListener = { listItem: ProductItem -> itemChecked(listItem) }
        )
    }

    private fun getInstallationId() {
        val firebaseInstance = Observer<String> { data ->
            if (data.isNotEmpty()) {
                installationId = data
                getMeals()
                getProducts()
            }
        }
        MainActivity.firebaseInstanceId.observeForever(firebaseInstance)
    }

    private fun setRecycler(binding: ActivityMealIngredientsBinding) {
        recyclerview = binding.ingredientList
        with(recyclerview) {
            layoutManager = LinearLayoutManager(this.context)
            setHasFixedSize(false)
            adapter = recyclerAdapter
        }
    }

    private fun getListStatus() {
        val emptyListObserver = Observer<Boolean> { data ->
            val searchSomething = !search.query.isNullOrEmpty()
            if (data && searchSomething) {
                recyclerview.visibility = View.GONE
                noResults.visibility = View.VISIBLE
            } else {
                noResults.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE
            }
        }

        Handler(Looper.getMainLooper()).post {
            ProductAdapter.emptyList.observeForever(emptyListObserver)
        }
    }

    private fun getProducts() {
        mProductViewModel
            .getAllProducts(installationId)
            .observe(this) {
                when(it.status) {
                    Status.SUCCESS -> {
                        if (it.data != null) {
                            ingredients = it.data
                                .sortedBy { item -> item.name }
                            ingredients.forEach { item ->
                                item.isChecked =
                                    if (selectedIngredients.any { element -> element.ingredientId == item.id }) "1" else "0"
                            }
                            recyclerAdapter.updateRecyclerData(ingredients)
                        } else {
                            val message = getString(R.string.meals_no_ingredient_received)
                            Snackbar.make(findViewById(R.id.app_container), message, SNACKBAR_DURATION).show()
                        }
                        spinner.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        spinner.visibility = View.VISIBLE
                    }
                    Status.FAILURE -> {
                        sendEmail(this,
                            this,
                            findViewById(R.id.mealIngredientsActivity),
                            "Error en MealIngredientsActivity:getProducts",
                            getString(R.string.saveError, installationId, "al obtener los ingredientes: ${it.message}"),
                            installationId)
                        spinner.visibility = View.GONE
                    }
                }
            }
    }

    private fun getMeals() {
        mMealViewModel
            .getMeal(meal?.mealId!!, installationId)
            .observe(this) {
                when(it.status) {
                    Status.SUCCESS -> {
                        if (it.data !== null) {
                            selectedIngredients = it.data as MutableList<SelectedIngredient>
                            renderSelectedIngredients(selectedIngredients)
                            saveButton.isEnabled = selectedIngredients.isNotEmpty()
                        }
                        spinner.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        spinner.visibility = View.VISIBLE
                    }
                    Status.FAILURE -> {
                        sendEmail(this,
                            this,
                            findViewById(R.id.mealIngredientsActivity),
                            "Error en MealIngredientsActivity:getMeals",
                            getString(R.string.saveError, installationId, "al obtener las comidas: ${it.message}"),
                            installationId)
                        spinner.visibility = View.GONE
                    }
                }
            }
    }

    private fun itemChecked(listItem: ProductItem) {
        val reversedValue = if (listItem.isChecked == "0") "1" else "0"
        listItem.isChecked = reversedValue
        updateIngredientList(listItem, reversedValue)

        renderSelectedIngredients(selectedIngredients)
        search.setQuery("", false)
        search.clearFocus()
    }

    private fun updateIngredientList(listItem: ProductItem, value: String) {
        run breaking@{
            ingredients.forEach {
                if (it.id == listItem.id) {
                    it.isChecked = listItem.isChecked
                    return@breaking
                }
            }
        }
        recyclerAdapter.updateRecyclerData(ingredients)
        if (value == "0") {
            selectedIngredients.removeAll { item -> item.ingredientId == listItem.id }
        } else {
            selectedIngredients.add(SelectedIngredient(meal?.mealId!!, listItem.name, listItem.id!!))
        }
        saveButton.isEnabled = selectedIngredients.isNotEmpty()
    }

    private fun renderSelectedIngredients(list: List<SelectedIngredient>) {
        chipGroup.removeAllViews()
        list
            .sortedBy { item -> item.name }
            .forEach { item ->
                displaySelectedIngredients(item)
            }
    }

    private fun displaySelectedIngredients(item: SelectedIngredient) {
        val chip = Chip(this)
        with(chip){
            text = item.name
            isChipIconVisible = false
            isCloseIconVisible = true
            isClickable = true
            isCheckable = false
            chip.setOnCloseIconClickListener {
                removeChipAndIngredient(item, chip)
            }
        }
        chipGroup.addView(chip as View)
    }

    private fun removeChipAndIngredient(item: SelectedIngredient, chip: Chip) {
        chipGroup.removeView(chip as View)
        val productItem = ProductItem(item.ingredientId, item.name, "0", 1, "")
        updateIngredientList(productItem, "0")
    }

    private fun saveIngredients() {
        val commaSeparatedValues = selectedIngredients
            .map { it.ingredientId }
            .joinToString(",")

        mMealViewModel
            .saveMealIngredients(meal?.mealId!!, commaSeparatedValues, installationId)
            .observe(this) {
                when(it.status) {
                    Status.SUCCESS -> {
                        finish()
                    }
                    Status.LOADING -> {
                        spinner.visibility = View.VISIBLE
                    }
                    Status.FAILURE -> {
                        sendEmail(this,
                            this,
                            findViewById(R.id.mealIngredientsActivity),
                            "Error en saveIngredients",
                            getString(R.string.saveError, installationId, "al guardar los ingredientes: ${it.message}"),
                            installationId)
                        spinner.visibility = View.GONE
                    }
                }
            }
    }
}