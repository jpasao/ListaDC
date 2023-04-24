package com.latribu.listadc.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.latribu.listadc.R
import com.latribu.listadc.common.EXTRA_PRODUCT
import com.latribu.listadc.common.adapters.ProductAdapter
import com.latribu.listadc.databinding.FragmentListBinding
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.MainViewModel
import com.latribu.listadc.common.network.RestApiManager
import com.google.android.material.snackbar.Snackbar
import com.latribu.listadc.common.models.Product

class ListFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerview: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        recyclerview = view.findViewById(R.id.listRecyclerview)
        recyclerview.layoutManager = LinearLayoutManager(this.context)

        mainViewModel.arrayListLiveData.observe(viewLifecycleOwner) { arrayList -> renderData(arrayList) }
    }

    private fun renderData(products: Product) {
        binding.listRecyclerview.adapter = ProductAdapter(
            products,
            checkBoxClickListener = { listItem: ProductItem -> itemChecked(listItem) },
            longClickListener = { listItem: ProductItem -> itemLongPressed(listItem) })
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
                val message: String = getString(R.string.saveError)
                val snack = Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
                snack.show()
            } else {
                renderData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}