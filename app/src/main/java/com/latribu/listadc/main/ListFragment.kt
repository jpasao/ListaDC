package com.latribu.listadc.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.latribu.listadc.R
import com.latribu.listadc.common.EXTRA_PRODUCT
import com.latribu.listadc.common.MainViewModel
import com.latribu.listadc.common.adapters.ProductAdapter
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.network.RestApiManager
import com.latribu.listadc.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerview: RecyclerView
    private lateinit var quantityAndItem: Pair<Int, ProductItem>
    private val apiService = RestApiManager()
    private lateinit var fabToTop: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        recyclerview = view.findViewById(R.id.listRecyclerview)
        recyclerview.layoutManager = LinearLayoutManager(this.context)

        mainViewModel.arrayListLiveData.observe(viewLifecycleOwner) { products ->
            run {
                binding.listRecyclerview.adapter = ProductAdapter(
                    products,
                    checkBoxClickListener = { listItem: ProductItem -> itemChecked(listItem) },
                    longClickListener = { listItem: ProductItem -> itemLongPressed(listItem) },
                    quantityClickListener = { listItem: ProductItem -> quantityClicked(listItem) })
            }
        }

        fabToTop = binding.fabToTop
        fabToTop.setOnClickListener{
            recyclerview.smoothScrollToPosition(0)
        }

        recyclerview.addOnScrollListener(object: RecyclerView.OnScrollListener(){
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
        quantityAndItem.second.quantity = quantityAndItem.first
        saveProduct(quantityAndItem.second)
    }

    private fun saveProduct(product: ProductItem) {
        apiService.saveProduct(product) {
            if (it != null) {
                val index = mainViewModel.arrayListLiveData.value?.indexOf(product)!!
                mainViewModel.arrayListLiveData.value?.set(index, product)
                binding.listRecyclerview.adapter?.notifyItemChanged(index)
            } else {
                val message = getString(R.string.saveError, "al actualizar la cantidad")
                val snack = Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
                snack.show()
            }
        }
    }

    private fun itemLongPressed(listItem: ProductItem) {
        val intent = Intent(requireContext(), AddProductActivity::class.java)
        intent.putExtra(EXTRA_PRODUCT, listItem)
        startActivity(intent)
    }

    private fun itemChecked(listItem: ProductItem) {
        val apiService = RestApiManager()
        apiService.checkProduct(listItem) {
            if (it == null) {
                val message: String = getString(R.string.saveError, "al marcar un elemento")
                val snack = Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
                snack.show()
            } else {
                mainViewModel.arrayListLiveData.value!!.clear()
                mainViewModel.arrayListLiveData.value!!.addAll(it)
                binding.listRecyclerview.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}