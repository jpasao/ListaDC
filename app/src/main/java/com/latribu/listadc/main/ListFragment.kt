package com.latribu.listadc.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.ProgressBar
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
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants.Companion.DEFAULT_USER
import com.latribu.listadc.common.Constants.Companion.EXTRA_PRODUCT
import com.latribu.listadc.common.Constants.Companion.TAB_MAINLIST
import com.latribu.listadc.common.adapters.ProductAdapter
import com.latribu.listadc.common.factories.ProductViewModelFactory
import com.latribu.listadc.common.models.FirebaseData
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.models.Status
import com.latribu.listadc.common.models.Undo
import com.latribu.listadc.common.models.User
import com.latribu.listadc.common.network.FirebaseMessagingService
import com.latribu.listadc.common.repositories.product.AppCreator
import com.latribu.listadc.common.sendEmail
import com.latribu.listadc.common.showMessage
import com.latribu.listadc.common.viewmodels.PreferencesViewModel
import com.latribu.listadc.common.viewmodels.ProductViewModel
import com.latribu.listadc.databinding.FragmentListBinding


class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerview: RecyclerView
    private lateinit var noResults: TextView
    private lateinit var quantityAndItem: Pair<Int, ProductItem>
    private lateinit var fabToTop: FloatingActionButton
    private lateinit var fabAddProduct: FloatingActionButton
    private lateinit var mProductViewModel: ProductViewModel
    private lateinit var mRecyclerAdapter: ProductAdapter
    private lateinit var preferencesViewModel: PreferencesViewModel
    private var savedUser: User = DEFAULT_USER
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var spinner: ProgressBar
    private lateinit var search: SearchView
    private var installationId: String = ""

    companion object {
        // Observed in FirebaseMessagingService.readPreferences()
        // and MainActivity.readPreferences()
        val user = MutableLiveData<User>()
        val buyMode = MutableLiveData<Boolean>()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        search = binding.productSearch
        spinner = binding.spinningHamburger
        noResults = binding.noResults

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        getInstallationId()
        getUndoAction()
        setListeners()
        setObservers()
        setRecycler()
        search.clearFocus()
    }

    private fun getInstallationId() {
        val firebaseInstance = Observer<String> { data ->
            if (data.isNotEmpty()) {
                installationId = data
                getProducts()
            }
        }
        MainActivity.firebaseInstanceId.observeForever(firebaseInstance)
    }

    private fun getUndoAction() {
        val undoAction = Observer<Int> {tab ->
            if (tab == TAB_MAINLIST) {
                val elementToUndo = Undo.getElement(TAB_MAINLIST) as ProductItem?
                if (elementToUndo !== null) {
                    itemChecked(elementToUndo, false)
                } else {
                    showMessage(requireView(), getString(R.string.undo_max))
                }
            }
        }
        MainActivity.undoAction.observeForever(undoAction)
    }

    private fun setListeners() {
        fabToTop = binding.fabToTop
        fabToTop.setOnClickListener{
            recyclerview.smoothScrollToPosition(0)
        }

        fabAddProduct = binding.fabAddProduct
        fabAddProduct.setOnClickListener{
            val intent = Intent(requireContext(), SaveProductActivity::class.java)
            val product = ProductItem(-1, "", "", -1, "")

            intent.putExtra(EXTRA_PRODUCT, product)
            startActivity(intent)
        }
        pullToRefresh = binding.swipeLayout
        pullToRefresh.setOnRefreshListener {
            getProducts()
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

    private fun setObservers() {
        getNotification()
        getUser()
        getListStatus()
    }

    private fun initData() {
        mProductViewModel = ViewModelProvider(
            this,
            ProductViewModelFactory(AppCreator.getApiHelperInstance())
        )[ProductViewModel::class.java]

        preferencesViewModel = ViewModelProvider(
            this
        )[PreferencesViewModel::class.java]

        mRecyclerAdapter = ProductAdapter(
            checkBoxClickListener = { listItem: ProductItem -> itemChecked(listItem) },
            longClickListener = { listItem: ProductItem -> itemLongPressed(listItem) },
            quantityClickListener = { listItem: ProductItem -> quantityClicked(listItem) })
    }

    private fun getNotification() {
        val firebaseObserver = Observer<FirebaseData> { data ->
            if (data.user != savedUser.id) {
                getProducts()
            }
        }
        FirebaseMessagingService.firebaseData.observeForever(firebaseObserver)
    }

    private fun getUser() {
        binding.apply {
            preferencesViewModel.getUser.observe(viewLifecycleOwner) { data ->
                savedUser = data
                user.postValue(data!!)
            }
            preferencesViewModel.getBuyMode.observe(viewLifecycleOwner) { data ->
                buyMode.postValue(data!!)
            }
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

    private fun setRecycler() {
        recyclerview = requireView().findViewById(R.id.listRecyclerview)
        with(recyclerview) {
            layoutManager = LinearLayoutManager(this.context)
            setHasFixedSize(false)
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
    }

    private fun getProducts() {
        if (view != null){
            mProductViewModel
                .getAllProducts(installationId)
                .observe(viewLifecycleOwner) {
                    when(it.status) {
                        Status.SUCCESS -> {
                            mRecyclerAdapter.updateRecyclerData(it.data!!)
                            spinner.visibility = View.GONE
                        }
                        Status.LOADING -> {
                            spinner.visibility = View.VISIBLE
                        }
                        Status.FAILURE -> {
                            val savedUserName: String = savedUser.name.ifEmpty { installationId }
                            sendEmail(this,
                                viewLifecycleOwner,
                                requireView(),
                                "Error en getProducts",
                                getString(R.string.saveError, savedUserName, "al obtener la lista: ${it.message}"),
                                installationId)
                            spinner.visibility = View.GONE
                        }
                    }
                }
        }
    }

    private fun quantityClicked(listItem: ProductItem) {
        val numberPicker = NumberPicker(requireContext())
        numberPicker.minValue = 1
        numberPicker.maxValue = 50
        numberPicker.value = listItem.quantity!!
        numberPicker.setOnValueChangedListener { _, _, newVal ->
            quantityAndItem = Pair(newVal, listItem)
        }

        val builder = AlertDialog.Builder(context)
        with(builder){
            setTitle(R.string.choose_quantity_message)
            setView(numberPicker)
            setPositiveButton(R.string.ok_button_text) { _, _ -> saveQuantity() }
            setNegativeButton(R.string.cancel_button_text, null)
            create()
            show()
        }
    }

    private fun saveQuantity() {
        if (this::quantityAndItem.isInitialized) {
            quantityAndItem.second.quantity = quantityAndItem.first
            editProduct(quantityAndItem.second)
        } else {
            sendEmail(this,
                viewLifecycleOwner,
                requireView(),
                "Error en saveQuantity",
                getString(R.string.saveError, savedUser.name, "al obtener los datos del elemento"),
                installationId)
        }
    }

    private fun editProduct(product: ProductItem) {
        mProductViewModel
            .editProduct(product, savedUser, installationId)
            .observe(viewLifecycleOwner) {
                when(it.status) {
                    Status.SUCCESS -> {
                        search.setQuery("", false)
                        search.clearFocus()
                        getProducts()
                        spinner.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        spinner.visibility = View.VISIBLE
                    }
                    Status.FAILURE -> {
                        val savedUserName: String = savedUser.name.ifEmpty { installationId }
                        sendEmail(this,
                            viewLifecycleOwner,
                            requireView(),
                            "Error en editProduct",
                            getString(R.string.saveError, savedUserName, "al editar el elemento: ${it.message}"),
                            installationId)
                        spinner.visibility = View.GONE
                    }
                }
            }
    }

    private fun itemLongPressed(listItem: ProductItem) {
        val intent = Intent(requireContext(), SaveProductActivity::class.java)
        intent.putExtra(EXTRA_PRODUCT, listItem)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun itemChecked(listItem: ProductItem, undo: Boolean = true) {
        if (undo) { Undo.addElement(listItem) }

        mProductViewModel
            .checkProductItem(listItem, savedUser, installationId)
            .observe(viewLifecycleOwner) {
                when(it.status) {
                    Status.SUCCESS -> {
                        mRecyclerAdapter.updateRecyclerData(it.data!!)
                        spinner.visibility = View.GONE
                        search.setQuery("", false)
                        search.clearFocus()
                    }
                    Status.LOADING -> {
                        spinner.visibility = View.VISIBLE
                    }
                    Status.FAILURE -> {
                        val savedUserName: String = savedUser.name.ifEmpty { installationId }
                        sendEmail(this,
                            viewLifecycleOwner,
                            requireView(),
                            "Error en ListFragment:itemChecked",
                            getString(R.string.saveError, savedUserName, "al marcar un elemento: ${it.message}"),
                            installationId)
                        spinner.visibility = View.GONE
                    }
                }
            }
    }
}