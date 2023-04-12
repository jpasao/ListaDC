package com.latribu.listadc.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.latribu.listadc.R
import com.latribu.listadc.common.adapters.ProductAdapter
import com.latribu.listadc.databinding.FragmentListBinding
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.MainViewModel

class ListFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val recyclerview = view.findViewById<RecyclerView>(R.id.listRecyclerview)
        recyclerview.layoutManager = LinearLayoutManager(this.context)

        mainViewModel.arrayListLiveData.observe(viewLifecycleOwner) { arrayList ->
            binding.listRecyclerview.adapter = ProductAdapter(
                arrayList,
                checkBoxClickListener = { listItem: ProductItem -> itemChecked(listItem) },
                longClickListener = { listItem: ProductItem -> itemLongPressed(listItem) })
            }
    }

    private fun itemLongPressed(listItem: ProductItem) {
        Log.d("pruebas", "longClicked!, id ${listItem.name}")
    }
    private fun itemChecked(listItem: ProductItem) {
        Log.d("pruebas", "clicked!, id ${listItem.name}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}