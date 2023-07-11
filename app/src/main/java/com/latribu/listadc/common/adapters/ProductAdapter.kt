package com.latribu.listadc.common.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.normalize
import com.latribu.listadc.databinding.ListItemDesignBinding

class ProductAdapter(
    val checkBoxClickListener: (ProductItem) -> Unit,
    val longClickListener: (ProductItem) -> Unit,
    val quantityClickListener: (ProductItem) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var productList = ArrayList<ProductItem>()
    private var filteredProductList = ArrayList<ProductItem>()

    init {
        filteredProductList = productList
    }

    companion object {
        // Observed in ListFragment
        val emptyList = MutableLiveData<Boolean>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemDesignBinding.inflate(inflater, parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount() = filteredProductList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: ProductItem = filteredProductList[position]
        (holder as ListViewHolder).bind(item, longClickListener)
        holder.checkBox.isChecked = isChecked(item)

        holder.checkBox.setOnClickListener{
            checkBoxClickListener(item)
        }
        holder.quantity.setOnClickListener {
            quantityClickListener(item)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = normalize(constraint.toString())

                filteredProductList = productList.filter { row ->
                    val normalizedName = normalize(row.name)
                    val normalizedComment = normalize(row.comment)

                    (normalizedName.contains(charSearch, ignoreCase = true)) or
                    (normalizedComment?.contains(charSearch, ignoreCase = true) ?: false)
                } as ArrayList<ProductItem>

                val filterResults = FilterResults()
                filterResults.values = filteredProductList

                emptyList.postValue(filteredProductList.isNullOrEmpty())
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredProductList = results?.values as ArrayList<ProductItem>
                notifyDataSetChanged()
            }
        }
    }

    inner class ListViewHolder(private val binding: ListItemDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        val checkBox: CheckBox = binding.check
        val quantity: Button = binding.quantity

        fun bind(item: ProductItem, longClickListener: (ProductItem) -> Unit) {
            val checked = isChecked(item)
            binding.check.isChecked = checked
            binding.quantity.text = item.quantity.toString()
            binding.name.text = item.name
            val opacity = if (checked) 0.54f else 0.87f
            binding.name.alpha = opacity
            binding.comment.text = item.comment
            binding.comment.alpha = opacity

            binding.root.setOnLongClickListener {
                longClickListener(item)
                true
            }
        }
    }

    fun updateRecyclerData(productList: List<ProductItem>) {
        this.productList.clear()
        this.productList.addAll(productList)
        this.filteredProductList = this.productList
        notifyDataSetChanged()
    }

    fun isChecked(item: ProductItem) : Boolean {
        return item.isChecked == "1"
    }
}