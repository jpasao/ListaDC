package com.latribu.listadc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.latribu.listadc.databinding.FragmentListBinding
import com.latribu.listadc.ui.main.MainViewModel

class ListFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val recyclerview = view.findViewById<RecyclerView>(R.id.listRecyclerview)
        recyclerview.layoutManager = LinearLayoutManager(this.context)

        mainViewModel.arrayListLiveData.observe(viewLifecycleOwner) { arrayList ->
            val adapter = RecyclerAdapter(arrayList)
            recyclerview.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}