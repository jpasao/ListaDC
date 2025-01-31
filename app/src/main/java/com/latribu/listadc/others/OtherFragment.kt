package com.latribu.listadc.others

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants
import com.latribu.listadc.common.adapters.OtherAdapter
import com.latribu.listadc.common.factories.OtherViewModelFactory
import com.latribu.listadc.common.models.Other
import com.latribu.listadc.common.models.ParentData
import com.latribu.listadc.common.models.Status
import com.latribu.listadc.common.models.Undo
import com.latribu.listadc.common.models.User
import com.latribu.listadc.common.network.FirebaseMessagingService
import com.latribu.listadc.common.repositories.other.AppCreator
import com.latribu.listadc.common.sendEmail
import com.latribu.listadc.common.showMessage
import com.latribu.listadc.common.showYesNoDialog
import com.latribu.listadc.common.viewmodels.OtherViewModel
import com.latribu.listadc.common.viewmodels.PreferencesViewModel
import com.latribu.listadc.databinding.FragmentOtherBinding
import com.latribu.listadc.main.MainActivity

class OtherFragment : Fragment() {
    private var _binding: FragmentOtherBinding? = null
    private var installationId: String = ""
    private val binding get() = _binding!!
    private lateinit var recyclerview: RecyclerView
    private var initialized = false
    private lateinit var mOtherViewModel: OtherViewModel
    private lateinit var preferencesViewModel: PreferencesViewModel
    private var savedUser: User = Constants.DEFAULT_USER
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var mRecyclerAdapter: OtherAdapter
    private lateinit var fabToTop: FloatingActionButton
    private lateinit var fabAddOther: FloatingActionButton
    private lateinit var search: SearchView

    private lateinit var spinner: ProgressBar

    companion object {
        // Observed in MainActivity.readPreferences()
        val user = MutableLiveData<User>()
        // Observed in MainActivity
        var otherFragmentBadge = MutableLiveData<Int>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        getUser()
        getNotification()
        getInstallationId()
        getUndoAction()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOtherBinding.inflate(inflater, container, false)
        bindElements(binding)
        setListeners()
        return binding.root
    }

    private fun bindElements(binding: FragmentOtherBinding) {
        spinner = binding.spinningHamburger
        fabAddOther = binding.fabAddOther
        pullToRefresh = binding.swipeLayout
        search = binding.otherSearch
    }

    private fun setListeners() {
        fabAddOther.setOnClickListener {
            val item = Other(0, 0, "", "", 0)
            showDialog(item)
        }
        fabToTop = binding.fabToTop
        fabToTop.setOnClickListener{
            recyclerview.smoothScrollToPosition(0)
        }
        pullToRefresh.setOnRefreshListener {
            getOthers()
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
        mOtherViewModel = ViewModelProvider(
            this,
            OtherViewModelFactory(AppCreator.getApiHelperInstance())
        )[OtherViewModel::class.java]

        preferencesViewModel = ViewModelProvider(
            this
        )[PreferencesViewModel::class.java]

        mRecyclerAdapter = OtherAdapter(
            checkBoxListener = { item: Other -> itemChecked(item) },
            longClickListener = { item: Other -> showDialog(item) }
        )
    }

    private fun getNotification() {
        val firebaseObserver =  Observer<Int> {data ->
            if (data != savedUser.id) {
                getOthers()
            }
        }
        FirebaseMessagingService.otherNotificationMessage.observeForever(firebaseObserver)
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
                getOthers()
            }
        }
        MainActivity.firebaseInstanceId.observeForever(firebaseInstance)
    }

    private fun getUndoAction() {
        val undoAction = Observer<Int> {tab ->
            if (tab == Constants.TAB_OTHERS) {
                val elementToUndo = Undo.getElement(Constants.TAB_OTHERS) as Other?
                if (elementToUndo !== null) {
                    itemChecked(elementToUndo, false)
                } else {
                    showMessage(requireView(), getString(R.string.undo_max))
                }
            }
        }
        MainActivity.undoAction.observeForever(undoAction)
    }

    private fun getOthers(updateStatus: Boolean = false) {
        if (view != null) {
            mOtherViewModel
                .getAllOthers(installationId)
                .observe(viewLifecycleOwner) {
                    when(it.status) {
                        Status.SUCCESS -> {
                            processOthers(it.data!!, updateStatus)
                            spinner.visibility = View.GONE
                            if (updateStatus) {
                                val item = Other(0, 0, "", "", 0)
                                showDialog(item)
                            }
                        }
                        Status.LOADING -> {
                            spinner.visibility = View.VISIBLE
                        }
                        Status.FAILURE -> {
                            val savedUserName: String = savedUser.name.ifEmpty { installationId }
                            sendEmail(this,
                                viewLifecycleOwner,
                                requireView(),
                                "Error en getOthers",
                                getString(R.string.saveError, savedUserName, "al obtener las otras cosas: ${it.message}"),
                                installationId)
                            spinner.visibility = View.GONE
                        }
                    }
                }
        }
    }

    private fun setTabBadge(others: List<Other>) {
        val uncheckedItems = others.count { it.isChecked == 0 }
        otherFragmentBadge.postValue(uncheckedItems)
    }

    private fun saveOthers(item: Other, alertDialog: AlertDialog? = null, updateStatus: Boolean = false) {
        val isChecked: Int = item.isChecked + 1
        mOtherViewModel
            .saveOther(item.id, item.parentId, item.name.trim(), isChecked, savedUser.id, installationId)
            .observe(viewLifecycleOwner) {
                when(it.status) {
                    Status.SUCCESS -> {
                        search.setQuery("", false)
                        search.clearFocus()
                        alertDialog?.dismiss()
                        spinner.visibility = View.GONE
                        getOthers(updateStatus)
                    }
                    Status.LOADING -> {
                        spinner.visibility = View.VISIBLE
                    }
                    Status.FAILURE -> {
                        val savedUserName: String = savedUser.name.ifEmpty { installationId }
                        sendEmail(this,
                            viewLifecycleOwner,
                            requireView(),
                            "Error en saveOthers",
                            getString(R.string.saveError, savedUserName, "con lo que acaba de hacer: ${it.message}"),
                            installationId)
                        spinner.visibility = View.GONE
                    }
                }
            }
    }

    private fun processOthers(others: List<Other>, updateStatus: Boolean = false) {
        val othersToAdd = ArrayList<ParentData<Other>>()

        others
            .groupBy { it.parentName }
            .forEach { (parentName, children) ->
                children.forEach {
                    val titleToCheck = if (it.isChecked == 1) {
                        "$parentName ${getString(R.string.others_buyed_literal)}"
                    } else {
                        parentName
                    }
                    val existParent = othersToAdd.any { parent -> parent.parentTitle == titleToCheck }
                    if (existParent) {
                        val parentList = othersToAdd.first { parent -> parent.parentTitle == titleToCheck }
                        if (parentList.subList.isEmpty()) {
                            parentList.subList = arrayListOf(it)
                        } else {
                            parentList.subList!!.add(it)
                        }
                    } else {
                        othersToAdd.add(ParentData(
                            parentTitle = titleToCheck,
                            subList = arrayListOf(it),
                            isExpanded = it.isChecked == 0
                        ))
                    }
                }
            }

        othersToAdd.sortBy { it.parentTitle }
        setTabBadge(others)

        mRecyclerAdapter.updateRecyclerData(othersToAdd, updateStatus)
    }

    private fun setRecyclers() {
        recyclerview = requireView().findViewById(R.id.otherRecyclerview)
        with(recyclerview) {
            layoutManager = LinearLayoutManager(this.context)
            adapter = mRecyclerAdapter
            addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition()

                    if (firstVisiblePosition == 0)
                        fabToTop.visibility = View.INVISIBLE
                    else
                        fabToTop.visibility = View.VISIBLE
                }
            })
        }
        initialized = true
    }

    private fun showDialog(item: Other) {
        val boughtLiteral = getString(R.string.others_buyed_literal)
        val builder = AlertDialog.Builder(context)
            .create()
        val view = layoutInflater.inflate(R.layout.dialog_parent_child_add,null)
        val ingredientsLayout: LinearLayout = view.findViewById(R.id.loIngredients)
        ingredientsLayout.isVisible = false
        val parents = mRecyclerAdapter
            .getParentData()

        val tempParents = parents
            .map {
                it.parentTitle?.replace(boughtLiteral, "")?.trim() ?: ""
            }.distinct()
        val parentTitles = arrayListOf<String>()
        if (tempParents.size == 1) {
            parentTitles.add(tempParents.elementAt(0).toString())
        } else {
            parentTitles.addAll(tempParents)
        }
        parentTitles.add(getString(R.string.others_new_parent_option))
        val parentFromList = parentTitles
            .find { it == item.parentName }

        val selectedParent =
            if (parentFromList.isNullOrEmpty()) 0
            else {
                val withoutBought = parentFromList.replace(" $boughtLiteral", "")
                parentTitles.indexOf(withoutBought)
            }

        val spinner: Spinner = view.findViewById(R.id.parentList)
        val spAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, parentTitles)
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(spinner) {
            adapter = spAdapter
            setSelection(selectedParent, false)
            onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { }
            }
        }

        val name: TextView = view.findViewById(R.id.childName)
        name.text = item.name

        val cancelButton: Button = view.findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            builder.dismiss()
        }
        val saveButton: Button = view.findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            val isNewParent = spinner.selectedItem == getString(R.string.others_new_parent_option)
            val isNewChild = item.id == 0
            val parent = if (isNewParent) {
                Other(0, 0, name.text.toString(), name.text.toString(), 0)
                } else if (isNewChild) {
                    val selectedNewParent = parents.find { it.parentTitle?.contains(spinner.selectedItem.toString(), false)!! }
                    val parentId = selectedNewParent?.subList?.first()?.parentId ?: 0
                    Other(0, parentId, selectedNewParent?.parentTitle!!, selectedNewParent.parentTitle!!, 0)
                }
                else {
                    parents
                        .find { it.parentTitle == spinner.selectedItem }
                        ?.subList?.first()
                }

            val isChecked: Int = item.isChecked + 1
            val itemEdited = Other(item.id, parent?.parentId!!, parent.parentName, name.text.toString(), isChecked)
            saveOthers(itemEdited, builder, isNewParent)
        }
        val deleteButton: Button = view.findViewById(R.id.btnDeleteChild)
        deleteButton.setOnClickListener { askForConfirmation(item, builder) }
        deleteButton.isVisible = item.id != 0

        val title: String = if (item.id == 0) getString(R.string.others_dialog_title_new) else getString(R.string.others_dialog_title_edit, item.name)
        builder.setTitle(title)
        builder.setView(view)
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    private fun itemChecked(item: Other, undo: Boolean = true) {
        if (undo) { Undo.addElement(item) }
        saveOthers(item)
    }

    private fun itemDeleted(item: Other, alertDialog: AlertDialog? = null, updateStatus: Boolean = false) {
        mOtherViewModel
            .deleteOther(item.id, savedUser.id, installationId)
            .observe(viewLifecycleOwner) {
                when(it.status) {
                    Status.SUCCESS -> {
                        search.setQuery("", false)
                        search.clearFocus()
                        alertDialog?.dismiss()
                        spinner.visibility = View.GONE
                        getOthers(updateStatus)
                    }
                    Status.LOADING -> {
                        spinner.visibility = View.VISIBLE
                    }
                    Status.FAILURE -> {
                        val savedUserName: String = savedUser.name.ifEmpty { installationId }
                        sendEmail(this,
                            viewLifecycleOwner,
                            requireView(),
                            "Error en OtherFragment:itemDeleted",
                            getString(R.string.saveError, savedUserName, "al borrar el elemento: ${it.message}"),
                            installationId)
                        spinner.visibility = View.GONE
                    }
                }
            }
    }

    private fun askForConfirmation(item: Other, alertDialog: AlertDialog) {
        showYesNoDialog(
            requireContext(),
            getString(R.string.delete_dialog_title),
            getString(R.string.delete_dialog_message, item.name)
        ) { _, _ -> itemDeleted(item, alertDialog) }
    }
}