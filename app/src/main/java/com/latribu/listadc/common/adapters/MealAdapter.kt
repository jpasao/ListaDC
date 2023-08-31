package com.latribu.listadc.common.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants
import com.latribu.listadc.common.Constants.Companion.OPACITY_FADED
import com.latribu.listadc.common.Constants.Companion.OPACITY_NORMAL
import com.latribu.listadc.common.models.Meal
import com.latribu.listadc.common.models.ParentData

class MealAdapter(
    val checkBoxListener: (Meal) -> Unit,
    val longClickListener: (Meal) -> Unit,
    val ingredientClickListener: (Meal) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mealList = ArrayList<ParentData<Meal>>()
    private var parentStatus = ArrayList<ParentData<Meal>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == Constants.PARENT) {
            val binding = inflater.inflate(R.layout.list_item_parent_row, parent, false)
            GroupViewHolder(binding)
        } else {
            val binding = inflater.inflate(R.layout.list_item_child_row, parent, false)
            ChildViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = mealList.size

    override fun getItemViewType(position: Int): Int = mealList[position].type

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mealList[position]
        if (item.type == Constants.PARENT) {
            holder as GroupViewHolder
            holder.apply {
                val parentExpanded = parentStatus.find { element -> element.parentTitle == item.parentTitle }?.isExpanded
                val arrowResource = if (parentExpanded == true) {
                    R.drawable.baseline_arrow_drop_up_24
                }
                else {
                    R.drawable.twotone_arrow_drop_down_24
                }
                image?.setImageResource(arrowResource)
                image?.setOnClickListener {
                    toggleParentItem(item, position)
                }

                name?.text = item.parentTitle
                name?.alpha = if (isMainList(item)) OPACITY_FADED else OPACITY_NORMAL
                name?.setOnClickListener {
                    toggleParentItem(item, position)
                }
            }
        } else {
            holder as ChildViewHolder
            holder.apply {
                val singleElement = item.subList.first()
                val isChecked =  singleElement.isChecked == 1
                check?.isChecked = isChecked
                name?.text = singleElement.name
                name?.setOnLongClickListener {
                    longClickListener(singleElement)
                    true
                }
                name?.alpha = if (isChecked) OPACITY_FADED else OPACITY_NORMAL
                image?.setOnClickListener {
                    // Open screen in which user can choose ingredients for that element
                    ingredientClickListener(item.subList.first())
                }
                check?.setOnClickListener {
                    checkBoxListener(item.subList.first())
                }
            }
        }
    }

    private fun isMainList(item: ParentData<Meal>) : Boolean =
        item.parentTitle?.contains(" ") == true

    private fun toggleParentItem(item: ParentData<Meal>, position: Int) {
        val parentChanged = this.parentStatus.find { parent -> parent.parentTitle == item.parentTitle }
        parentChanged?.isExpanded = !item.isExpanded

        if (item.isExpanded) {
            collapseParentRow(position)
        } else {
            expandParentRow(position)
        }
    }

    private fun collapseParentRow(position: Int) {
        val currentRow = mealList[position]
        val elements = currentRow.subList
        mealList[position].isExpanded = false
        if (mealList[position].type == Constants.PARENT) {
            elements.forEach {_ ->
                mealList.removeAt(position + 1)
            }
            notifyDataSetChanged()
        }
    }

    private fun expandParentRow(position: Int) {
        val currentRow = mealList[position]
        val elements = currentRow.subList
        currentRow.isExpanded = true
        var nextPosition = position
        if (currentRow.type == Constants.PARENT) {
            elements.forEach { meal ->
                val subListToAdd: ArrayList<Meal> = ArrayList()
                subListToAdd.add(meal)

                val parentModel = ParentData(type = Constants.CHILD, subList = subListToAdd)
                mealList.add(++nextPosition, parentModel)
            }
            notifyDataSetChanged()
        }
    }

    class GroupViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val name = row.findViewById(R.id.parentTitle) as TextView?
        val image = row.findViewById(R.id.downArrow) as ImageView?
    }

    class ChildViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val check = row.findViewById(R.id.check) as CheckBox?
        val name = row.findViewById(R.id.name) as TextView?
        val image = row.findViewById(R.id.ingredients) as ImageView?
    }

    fun updateRecyclerData(mealList: MutableList<ParentData<Meal>>) {
        this.mealList.clear()
        this.mealList.addAll(mealList)
        if (this.parentStatus.isEmpty()) {
            this.parentStatus.addAll(mealList
                .filter { item -> item.type == Constants.PARENT }
                .map {
                    item -> ParentData(
                        parentTitle = item.parentTitle,
                        type = item.type,
                        isExpanded = isMainList(item),
                        subList = ArrayList())
                })
            expandParentRow(1)
            expandParentRow(0)
        } else {
            this.parentStatus.forEach { parent ->
                val parentPosition = this.mealList.indexOfFirst { row -> row.parentTitle == parent.parentTitle }
                if (parent.isExpanded) {
                    expandParentRow(parentPosition)
                }
            }
        }
    }
}