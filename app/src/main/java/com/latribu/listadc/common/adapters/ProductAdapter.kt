package com.latribu.listadc.common.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.latribu.listadc.common.Constants.Companion.REGULAR_ITEM
import com.latribu.listadc.common.Constants.Companion.SEPARATOR_ITEM
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.normalize
import com.latribu.listadc.databinding.ListItemDesignBinding
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller

class ProductAdapter(
    val checkBoxClickListener: (ProductItem) -> Unit,
    val longClickListener: (ProductItem) -> Unit,
    val quantityClickListener: (ProductItem) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable, RecyclerViewFastScroller.OnPopupViewUpdate {
    private var productList = ArrayList<ProductItem>()
    private var filteredProductList = ArrayList<ProductItem>()
    private var LAST_UNCHECKED_ITEM = 0

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

    override fun getItemViewType(position: Int): Int {
        return if (position == LAST_UNCHECKED_ITEM) SEPARATOR_ITEM else REGULAR_ITEM
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
                        normalizedComment.contains(charSearch, ignoreCase = true)
                } as ArrayList<ProductItem>

                val filterResults = FilterResults()
                filterResults.values = filteredProductList

                emptyList.postValue(filteredProductList.isEmpty())
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results?.values != null && results.values != 0) {
                    filteredProductList = results.values as ArrayList<ProductItem>
                    getLastUncheckedItemId()
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onUpdate(position: Int, popupTextView: TextView) {
        val item = filteredProductList[position]
        val header = " ${item.name[0]} "
        with(popupTextView){
            text = header.toString()
            background.alpha = if (isChecked(item)) 230 else 255
        }
    }

    inner class ListViewHolder(private val binding: ListItemDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        val checkBox: CheckBox = binding.check
        val quantity: Button = binding.quantity
        private val lastUncheckedItem: ProductItem? = filteredProductList[LAST_UNCHECKED_ITEM]

        fun bind(item: ProductItem, longClickListener: (ProductItem) -> Unit) {
            val checked = isChecked(item)
            binding.check.isChecked = checked
            binding.quantity.text = item.quantity.toString()
            binding.name.text = item.name
            val opacity = getOpacity(item)
            binding.name.alpha = opacity
            binding.comment.text = item.comment
            binding.comment.alpha = opacity

            val isLastUnchecked = lastUncheckedItem != null && item.id == lastUncheckedItem.id
            if (isLastUnchecked) binding.root.setPadding(0, 0, 0, 50)

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
        getLastUncheckedItemId()
        notifyDataSetChanged()
    }

    fun isChecked(item: ProductItem) : Boolean {
        return item.isChecked == "1"
    }

    fun getOpacity(item: ProductItem) : Float {
        return if (isChecked(item)) 0.54f else 0.87f
    }

    fun getLastUncheckedItemId() {
        LAST_UNCHECKED_ITEM = filteredProductList.indexOfLast { product ->
            !isChecked(product)
        }
    }
}