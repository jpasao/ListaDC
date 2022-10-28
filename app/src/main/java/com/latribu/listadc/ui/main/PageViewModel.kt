package com.latribu.listadc.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PageViewModel : ViewModel() {

    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }

    val ingredientListMock: MutableList<String> = mutableListOf<String>("Arroz", "Papas", "Beicon", "Fideos")
    val otherListMock: MutableList<String> = mutableListOf<String>("Cianoacrilato", "Skuffen")
    val mealListMock: MutableList<String> = mutableListOf("Arverjas", "Perritos")

    fun setIndex(index: Int) {
        _index.value = index
    }
}