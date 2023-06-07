package com.latribu.listadc.common.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.databinding.ListItemDesignBinding

class ProductAdapter(
    val checkBoxClickListener: (ProductItem) -> Unit,
    val longClickListener: (ProductItem) -> Unit,
    val quantityClickListener: (ProductItem) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mProductList = ArrayList<ProductItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemDesignBinding.inflate(inflater, parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount() = mProductList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: ProductItem = mProductList[position]
        (holder as ListViewHolder).bind(item, longClickListener)
        holder.checkBox.isChecked = isChecked(item)

        holder.checkBox.setOnClickListener{
            checkBoxClickListener(item)
        }
        holder.quantity.setOnClickListener {
            quantityClickListener(item)
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

            binding.root.setOnLongClickListener {
                longClickListener(item)
                true
            }
        }
    }

    fun updateRecyclerData(productList: List<ProductItem>) {
        mProductList.clear()
        mProductList.addAll(productList)
        notifyDataSetChanged()
    }

    fun updateRecyclerElement(product: ProductItem) {
        val index = mProductList.indexOfFirst { it.id == product.id }
        mProductList[index] = product
        mProductList.sortWith(compareBy<ProductItem>{ it.isChecked }.thenBy { it.name })
        notifyDataSetChanged()
    }

    fun isChecked(item: ProductItem) : Boolean {
        return item.isChecked == "1"
    }
}