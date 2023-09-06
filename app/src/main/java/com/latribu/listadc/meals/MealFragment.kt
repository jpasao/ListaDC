package com.latribu.listadc.meals

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants
import com.latribu.listadc.common.Constants.Companion.EXTRA_MEAL
import com.latribu.listadc.common.adapters.MealAdapter
import com.latribu.listadc.common.factories.MealViewModelFactory
import com.latribu.listadc.common.models.Meal
import com.latribu.listadc.common.models.ParentData
import com.latribu.listadc.common.models.Status
import com.latribu.listadc.common.models.User
import com.latribu.listadc.common.network.FirebaseMessagingService
import com.latribu.listadc.common.repositories.meal.AppCreator
import com.latribu.listadc.common.viewmodels.MealViewModel
import com.latribu.listadc.common.viewmodels.PreferencesViewModel
import com.latribu.listadc.databinding.FragmentMealBinding
import com.latribu.listadc.main.MainActivity

class MealFragment : Fragment() {
    private var _binding: FragmentMealBinding? = null
    private var installationId: String = ""
    private val binding get() = _binding!!
    private lateinit var recyclerview: RecyclerView
    private var initialized = false
    private lateinit var mMealViewModel: MealViewModel
    private lateinit var preferencesViewModel: PreferencesViewModel
    private var savedUser: User = Constants.DEFAULT_USER
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var mRecyclerAdapter: MealAdapter
    private lateinit var fabAddMeal: FloatingActionButton
    private lateinit var search: SearchView

    private lateinit var spinner: ProgressBar

    companion object {
        // Observed in MainActivity.readPreferences()
        val user = MutableLiveData<User>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        getUser()
        getNotification()
        getInstallationId()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMealBinding.inflate(inflater, container, false)
        bindElements(binding)
        setListeners()
        return binding.root
    }

    private fun bindElements(binding: FragmentMealBinding) {
        spinner = binding.spinningHamburger
        fabAddMeal = binding.fabAddMeal
        pullToRefresh = binding.swipeLayout
        search = binding.mealSearch
    }

    private fun setListeners() {
        fabAddMeal.setOnClickListener {
            val item = Meal(-1, "", 0, 0)
            showDialog(item)
        }
        pullToRefresh.setOnRefreshListener {
            getMeals()
            pullToRefresh.isRefreshing = false
        }
        search.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                mRecyclerAdapter.filter.filter(newText)
                if (newText.isNullOrEmpty()) search.clearFocus()
                return false
            }
        })
    }

    private fun initData() {
        mMealViewModel = ViewModelProvider(
            this,
            MealViewModelFactory(AppCreator.getApiHelperInstance())
        )[MealViewModel::class.java]

        preferencesViewModel = ViewModelProvider(
            this
        )[PreferencesViewModel::class.java]

        mRecyclerAdapter = MealAdapter(
            checkBoxListener = { item: Meal -> itemChecked(item) },
            longClickListener = { item: Meal -> showDialog(item) },
            ingredientClickListener = { item: Meal -> ingredientPressed(item) }
        )
    }

    private fun getNotification() {
        val firebaseObserver = Observer<Int> { data ->
            if (data != savedUser.id) {
                getMeals()
            }
        }
        FirebaseMessagingService.mealNotificationMessage.observeForever(firebaseObserver)
    }

    private fun getUser() {
        binding.apply {
            preferencesViewModel.getUser.observe(viewLifecycleOwner) { data ->
                savedUser = data
            }
        }
    }

    private fun getInstallationId() {
        val firebaseInstance = Observer<String> { data ->
            if (data.isNotEmpty()) {
                installationId = data
                if (!initialized) setRecyclers()
                getMeals()
            }
        }
        MainActivity.firebaseInstanceId.observeForever(firebaseInstance)
    }

    private fun getMeals() {
        if (view != null) {
            mMealViewModel
                .getAllMeals(installationId)
                .observe(viewLifecycleOwner) {
                    when(it.status) {
                        Status.SUCCESS -> {
                            processMeals(it.data!!)
                            spinner.visibility = View.GONE
                        }
                        Status.LOADING -> {
                            spinner.visibility = View.VISIBLE
                        }
                        Status.FAILURE -> {
                            val message: String = getString(R.string.saveError, "al obtener las comidas: ${it.message}")
                            Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
                            spinner.visibility = View.GONE
                        }
                    }
                }
        }
    }

    private fun processMeals(meals: List<Meal>) {
        val lunches = meals.filter {meal -> meal.isLunch == 1 }
        val dinners = meals.filter { meal -> meal.isLunch == 0 }

        // Filter the whole list by type and status
        val currentLunches = ParentData(
            parentTitle = getString(R.string.meal_lunch_name),
            subList = lunches.filter { lunch -> lunch.isChecked == 0 } as ArrayList<Meal>
        )
        val currentDinners = ParentData(
            parentTitle = getString(R.string.meal_dinner_name),
            subList = dinners.filter { dinner -> dinner.isChecked == 0 } as ArrayList<Meal>
        )
        val checkedLunches = ParentData(
            parentTitle = getString(R.string.meal_lunch_name_checked),
            subList = lunches.filter { lunch -> lunch.isChecked == 1 } as ArrayList<Meal>
        )
        val checkedDinners = ParentData(
            parentTitle = getString(R.string.meal_dinner_name_checked),
            subList = dinners.filter { dinner -> dinner.isChecked == 1 } as ArrayList<Meal>
        )

        val mealsToAdd = ArrayList<ParentData<Meal>>()

        mealsToAdd.add(currentLunches)
        mealsToAdd.add(currentDinners)
        mealsToAdd.add(checkedLunches)
        mealsToAdd.add(checkedDinners)

        mRecyclerAdapter.updateRecyclerData(mealsToAdd)
    }

    private fun setRecyclers() {
        recyclerview = requireView().findViewById(R.id.mealRecyclerview)
        with(recyclerview) {
            layoutManager = LinearLayoutManager(this.context)
            adapter = mRecyclerAdapter
        }
        initialized = true
    }

    private fun itemChecked(item: Meal) {
        val isChecked: Int = item.isChecked + 1
        mMealViewModel
            .checkMeal(item.mealId, isChecked, savedUser.id, installationId)
            .observe(viewLifecycleOwner) {
                when(it.status) {
                    Status.SUCCESS -> {
                        processMeals(it.data!!)
                        spinner.visibility = View.GONE
                        search.setQuery("", false)
                        search.clearFocus()
                    }
                    Status.LOADING -> {
                        spinner.visibility = View.VISIBLE
                    }
                    Status.FAILURE -> {
                        val message: String = getString(R.string.saveError, "al marcar una comida: ${it.message}")
                        Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
                        spinner.visibility = View.GONE
                    }
                }
            }
    }

    private fun showDialog(item: Meal) {
        val builder = AlertDialog.Builder(context)
            .create()
        val view = layoutInflater.inflate(R.layout.dialog_parent_child_add,null)
        val parents = arrayOf(getString(R.string.meal_lunch_name), getString(R.string.meal_dinner_name))
        var selectedParent: Int = if (item.isLunch == 0) 1 else 0

        val spinner: Spinner = view.findViewById(R.id.parentList)
        val spAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, parents)
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(spinner) {
            adapter = spAdapter
            setSelection(selectedParent, false)
            onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedParent = position + 1
                }
            }
        }
        selectedParent += 1

        val name: TextView = view.findViewById(R.id.childName)
        name.text = item.name

        val cancelButton: Button = view.findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            builder.dismiss()
        }
        val saveButton: Button = view.findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            val itemEdited = Meal(item.mealId, name.text.toString(), selectedParent, 1)
            if (item.mealId == -1)
                addMeal(itemEdited, builder)
            else
                editMeal(itemEdited, builder)
        }

        val title: String = if (item.mealId == -1) getString(R.string.meal_dialog_title_new) else getString(R.string.meal_dialog_title_edit, item.name)
        builder.setTitle(title)
        builder.setView(view)
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    private fun ingredientPressed(item: Meal) {
        val intent = Intent(requireContext(), MealIngredientsActivity::class.java)
        intent.putExtra(EXTRA_MEAL, item)
        startActivity(intent)
    }

    private fun addMeal(item: Meal, alertDialog: AlertDialog) {
        mMealViewModel
            .addMeal(item.name, item.isLunch, savedUser.id)
            .observe(viewLifecycleOwner) {
                when(it.status) {
                    Status.SUCCESS -> {
                        search.setQuery("", false)
                        search.clearFocus()
                        getMeals()
                        alertDialog.dismiss()
                        spinner.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        spinner.visibility = View.VISIBLE
                    }
                    Status.FAILURE -> {
                        val message: String = getString(R.string.saveError, "al marcar una comida: ${it.message}")
                        Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
                        spinner.visibility = View.GONE
                    }
                }
            }
    }

    private fun editMeal(item: Meal, alertDialog: AlertDialog) {
        mMealViewModel
            .editMeal(item.mealId, item.name, item.isLunch, savedUser.id)
            .observe(viewLifecycleOwner) {
                when(it.status) {
                    Status.SUCCESS -> {
                        search.setQuery("", false)
                        search.clearFocus()
                        getMeals()
                        alertDialog.dismiss()
                        spinner.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        spinner.visibility = View.VISIBLE
                    }
                    Status.FAILURE -> {
                        val message: String = getString(R.string.saveError, "al marcar una comida: ${it.message}")
                        Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
                        spinner.visibility = View.GONE
                    }
                }
            }
    }
}