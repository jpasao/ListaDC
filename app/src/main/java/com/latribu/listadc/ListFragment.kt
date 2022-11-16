package com.latribu.listadc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
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

        mainViewModel.arrayListLiveData.observe(viewLifecycleOwner) { arrayList ->
            var texto: StringBuilder = java.lang.StringBuilder("Lista")
            if (arrayList != null) {
                arrayList.forEach {
                    texto.append("Nombre: ${it.name}\n")
                }
                binding.test.text = texto.toString()
            }
            else {
                binding.test.text = "No llegó nada"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}