package com.latribu.listadc.common.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants
import com.latribu.listadc.common.Constants.Companion.OPACITY_FADED
import com.latribu.listadc.common.Constants.Companion.OPACITY_NORMAL
import com.latribu.listadc.common.models.Meal
import com.latribu.listadc.common.models.ParentData
import com.latribu.listadc.common.normalize

class MealAdapter(
    val checkBoxListener: (Meal) -> Unit,
    val longClickListener: (Meal) -> Unit,
    val lunchClickListener: (Meal) -> Unit,
    val dinnerClickListener: (Meal) -> Unit,
    val ingredientsClickListener: (Meal) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var mealList = ArrayList<ParentData<Meal>>()
    private var filteredMealList = ArrayList<ParentData<Meal>>()
    private var parentStatus = ArrayList<ParentData<Meal>>()

    companion object {
        // Observed in MealFragment
        val emptyList = MutableLiveData<Boolean>()
    }
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

    override fun getItemCount(): Int = filteredMealList.size

    override fun getItemViewType(position: Int): Int = filteredMealList[position].type

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = filteredMealList[position]
        if (item.type == Constants.PARENT) {
            holder as GroupViewHolder
            holder.apply {
                val parentExpanded = filteredMealList.find { element -> element.parentTitle == item.parentTitle }?.isExpanded
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
                val singleElement = item.subList?.first()
                val isChecked =  singleElement?.isChecked == 1
                check?.isChecked = isChecked
                check?.isVisible = !isChecked
                check?.setOnClickListener {
                    checkBoxListener(item.subList?.first()!!)
                }
                name?.text = singleElement?.name
                name?.alpha = if (isChecked) OPACITY_FADED else OPACITY_NORMAL
                name?.setOnLongClickListener {
                    longClickListener(singleElement!!)
                    true
                }
                ingredients?.isVisible = !isChecked
                ingredients?.setOnClickListener {
                    item.subList?.first()?.let { it1 -> ingredientsClickListener(it1) }
                }
                lunch?.isVisible = isChecked
                lunch?.setOnClickListener {
                    lunchClickListener(item.subList?.first()!!)
                }
                dinner?.isVisible = isChecked
                dinner?.setOnClickListener {
                    dinnerClickListener(item.subList?.first()!!)
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = normalize(constraint.toString())
                filteredMealList.clear()
                mealList
                    .forEach { parent ->
                        val childrenData = parent.subList
                            ?.filter { row -> nameContains(row.name, charSearch)
                            } as ArrayList<Meal>
                        val parentData = ParentData(parent.parentTitle, parent.type, childrenData, true)

                        filteredMealList.add(parentData)
                    }

                val filterResults = FilterResults()
                filterResults.values = filteredMealList

                emptyList.postValue(filteredMealList.isEmpty())
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results?.values != null && results.values != 0) {
                    filteredMealList = results.values as ArrayList<ParentData<Meal>>

                    expandParentRow(3, false)
                    expandParentRow(2, false)
                    expandParentRow(1, false)
                    expandParentRow(0, true)
                }
                updateParentStatus()
            }
        }
    }

    private fun nameContains(name: String, search: String) =
        normalize(name).contains(search, ignoreCase = true)

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
        val currentRow = filteredMealList[position]
        val elements = currentRow.subList
        filteredMealList[position].isExpanded = false
        if (filteredMealList[position].type == Constants.PARENT) {
            elements?.forEach {_ ->
                if ((position + 1) < filteredMealList.size)
                    filteredMealList.removeAt(position + 1)
            }
            notifyDataSetChanged()
        }
    }

    private fun expandParentRow(position: Int, notify: Boolean = true) {
        if (position < filteredMealList.size){
            val currentRow = filteredMealList[position]
            val elements = currentRow.subList
            currentRow.isExpanded = true
            var nextPosition = position
            if (currentRow.type == Constants.PARENT) {
                elements?.forEach { meal ->
                    val subListToAdd: ArrayList<Meal> = ArrayList()
                    subListToAdd.add(meal)

                    val parentModel = ParentData(type = Constants.CHILD, subList = subListToAdd)
                    filteredMealList.add(++nextPosition, parentModel)
                }
                if (notify) notifyDataSetChanged()
            }
        }
    }

    class GroupViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val name: TextView? = row.findViewById(R.id.parentTitle)
        val image: ImageView? = row.findViewById(R.id.downArrow)
    }

    class ChildViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val check: CheckBox? = row.findViewById(R.id.check)
        val name: TextView? = row.findViewById(R.id.name)
        val ingredients: ImageView? = row.findViewById(R.id.ingredients)
        val lunch: ImageView? = row.findViewById(R.id.lunch)
        val dinner: ImageView? = row.findViewById(R.id.dinner)
    }

    fun updateRecyclerData(mealList: MutableList<ParentData<Meal>>) {
        this.mealList.clear()
        this.filteredMealList.clear()
        this.mealList.addAll(mealList)
        this.mealList.forEach {
            this.filteredMealList.add(it)
        }
        if (this.parentStatus.isEmpty()) {
            updateParentStatus()
        } else {
            this.parentStatus.forEach { parent ->
                val parentPosition = this.filteredMealList.indexOfFirst { row -> row.parentTitle == parent.parentTitle }
                if (parent.isExpanded) {
                    expandParentRow(parentPosition, false)
                }
            }
        }
        notifyDataSetChanged()
    }

    private fun updateParentStatus() {
        this.parentStatus.clear()
        this.parentStatus.addAll(this.filteredMealList
            .filter { item -> item.type == Constants.PARENT }
            .map {
                item -> ParentData(
                parentTitle = item.parentTitle,
                type = item.type,
                isExpanded = isMainList(item),
                subList = ArrayList())
            })
    }
}