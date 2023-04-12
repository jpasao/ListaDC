package com.latribu.listadc.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.latribu.listadc.databinding.ListItemDesignBinding
import com.latribu.listadc.models.Product
import com.latribu.listadc.models.ProductItem

// With the help of https://www.andreasjakl.com/recyclerview-kotlin-style-click-listener-android/
class ProductAdapter(
    private val productItemList: Product,
    val checkBoxClickListener: (ProductItem) -> Unit,
    val longClickListener: (ProductItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemDesignBinding.inflate(inflater, parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount() = productItemList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: ProductItem = productItemList[position]
        (holder as ListViewHolder).bind(item, longClickListener)
        holder.checkBox.isChecked = isChecked(item)

        holder.checkBox.setOnClickListener{
            checkBoxClickListener(item)
        }
    }

    inner class ListViewHolder(private val binding: ListItemDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        val checkBox: CheckBox = binding.check

        fun bind(item: ProductItem, longClickListener: (ProductItem) -> Unit) {
            val checked = isChecked(item)
            binding.check.isChecked = checked
            binding.quantity.text = item.quantity.toString()
            binding.name.text = item.name
            val opacity = if (checked) 0.54f else 0.87f
            binding.name.alpha = opacity

            binding.root.setOnLongClickListener {
                longClickListener(item)
                true
            }
        }
    }

    fun isChecked(item: ProductItem) : Boolean {
        return item.isChecked == "1"
    }
}