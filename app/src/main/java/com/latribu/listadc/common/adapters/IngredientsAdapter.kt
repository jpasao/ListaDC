package com.latribu.listadc.common.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.latribu.listadc.R
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.normalize

class IngredientsAdapter(
    val checkBoxClickListener: (ProductItem) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var ingredientList = ArrayList<ProductItem>()
    private var filteredIngredientList = ArrayList<ProductItem>()

    init {
        filteredIngredientList = ingredientList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = inflater.inflate(R.layout.ingredients_item_design, parent, false)
        return IngredientsViewHolder(binding)
    }

    override fun getItemCount(): Int = filteredIngredientList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: ProductItem = filteredIngredientList[position]

        holder as IngredientsViewHolder

        holder.apply {
            name.text = item.name
            comment.text = item.comment
            checkBox.isChecked = isChecked(item)
            checkBox.setOnClickListener {
                checkBoxClickListener(item)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = normalize(constraint.toString())

                filteredIngredientList = ingredientList.filter { row ->
                    val normalizedName = normalize(row.name)

                    normalizedName.contains(charSearch, ignoreCase = true)
                } as ArrayList<ProductItem>

                val filterResults = FilterResults()
                filterResults.values = filteredIngredientList

                ProductAdapter.emptyList.postValue(filteredIngredientList.isEmpty())
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results?.values != null && results.values != 0) {
                    filteredIngredientList = results.values as ArrayList<ProductItem>
                    notifyDataSetChanged()
                }
            }
        }
    }

    inner class IngredientsViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val checkBox: CheckBox = row.findViewById(R.id.check)
        val name: TextView = row.findViewById(R.id.name)
        val comment: TextView = row.findViewById(R.id.comment)
    }

    fun updateRecyclerData(ingredientList: List<ProductItem>) {
        this.ingredientList.clear()
        this.ingredientList.addAll(ingredientList)
        this.filteredIngredientList = this.ingredientList
        notifyDataSetChanged()
    }

    fun isChecked(item: ProductItem) : Boolean {
        return item.isChecked == "1"
    }
}