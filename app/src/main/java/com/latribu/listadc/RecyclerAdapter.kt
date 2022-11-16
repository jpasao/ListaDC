package com.latribu.listadc


import android.graphics.Color
import android.view.LayoutInflater

import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.latribu.listadc.databinding.ListItemDesignBinding
import com.latribu.listadc.generated.callback.OnClickListener
import com.latribu.listadc.models.ProductItem

class RecyclerAdapter(
    val clickListener: ProductListener,
    val imageClickListener: ImageListener
) : ListAdapter<ProductItem, RecyclerAdapter.ViewHolder>(ProductDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener, imageClickListener)
    }

    class ViewHolder private constructor(val binding: ListItemDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductItem, clickListener: ProductListener, imageClickListener: ImageListener) {
            binding.product = item
            binding.clickListener = clickListener
            binding.imageClickListener = imageClickListener

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemDesignBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class ProductDiffCallback : DiffUtil.ItemCallback<ProductItem>() {
    override fun areItemsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean {
        return oldItem == newItem
    }
}

class ProductListener(val clickListener: (id: Int) -> Unit) {
    fun onClick(product: ProductItem) = clickListener(product.id)
}

class ImageListener(val imageClickListener: (id: Int) -> Unit) {
    fun onImageClick(product: ProductItem) = imageClickListener(product.id)
}