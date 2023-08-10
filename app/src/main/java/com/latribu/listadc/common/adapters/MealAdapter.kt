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
                image?.setOnClickListener {
                    toggleParentItem(item, position, image)
                }

                name?.text = item.parentTitle
                name?.alpha = if (item.parentTitle?.contains(" ") == true) OPACITY_FADED else OPACITY_NORMAL
                name?.setOnClickListener {
                    toggleParentItem(item, position, image)
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

    private fun toggleParentItem(singleBoarding: ParentData<Meal>, position: Int, element: ImageView?) {
        if (singleBoarding.isExpanded) {
            collapseParentRow(position, element)
        } else {
            expandParentRow(position, element)
        }
    }

    private fun collapseParentRow(position: Int, element: ImageView?) {
        element?.setImageResource(R.drawable.twotone_arrow_drop_down_24)
        val currentBoardingRow = mealList[position]
        val elements = currentBoardingRow.subList
        mealList[position].isExpanded = false
        if (mealList[position].type == Constants.PARENT) {
            elements.forEach {_ ->
                mealList.removeAt(position + 1)
            }
            notifyDataSetChanged()
        }
    }

    private fun expandParentRow(position: Int, element: ImageView?) {
        element?.setImageResource(R.drawable.baseline_arrow_drop_up_24)
        val currentBoardingRow = mealList[position]
        val elements = currentBoardingRow.subList
        currentBoardingRow.isExpanded = true
        var nextPosition = position
        if (currentBoardingRow.type == Constants.PARENT) {
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

        notifyDataSetChanged()
    }
}