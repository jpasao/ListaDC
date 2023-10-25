package com.latribu.listadc.common.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants
import com.latribu.listadc.common.models.Other
import com.latribu.listadc.common.models.ParentData
import com.latribu.listadc.common.normalize

class OtherAdapter(
    val checkBoxListener: (Other) -> Unit,
    val longClickListener: (Other) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var otherList = ArrayList<ParentData<Other>>()
    private var filteredOtherList = ArrayList<ParentData<Other>>()
    private var parentStatus = ArrayList<ParentData<Other>>()
    private val boughtLiteral = "comprados"

    companion object {
        // Observed in OtherFragment
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

    override fun getItemCount(): Int = filteredOtherList.size

    override fun getItemViewType(position: Int): Int = filteredOtherList[position].type

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = filteredOtherList[position]
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
                name?.alpha = if (isMainList(item)) Constants.OPACITY_FADED else Constants.OPACITY_NORMAL
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
                name?.text = singleElement?.name
                name?.setOnLongClickListener {
                    longClickListener(singleElement!!)
                    true
                }
                name?.alpha = if (isChecked) Constants.OPACITY_FADED else Constants.OPACITY_NORMAL
                image?.setOnClickListener {
                    // Add a photo
                }
                image!!.setImageResource(R.drawable.ic_twotone_photo_camera_24)
                check?.setOnClickListener {
                    checkBoxListener(item.subList?.first()!!)
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = normalize(constraint.toString())
                filteredOtherList.clear()
                otherList
                    .forEach { parent ->
                        val childrenData = parent.subList
                            ?.filter { row -> nameContains(row.name, charSearch)
                            } as ArrayList<Other>
                        val parentData = ParentData(parent.parentTitle, parent.type, childrenData, true)

                        filteredOtherList.add(parentData)
                    }

                val filterResults = FilterResults()
                filterResults.values = filteredOtherList

                emptyList.postValue(filteredOtherList.isEmpty())
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results?.values != null && results.values != 0) {
                    filteredOtherList = results.values as ArrayList<ParentData<Other>>
                    expandParentsWithContent(filteredOtherList, !constraint.isNullOrEmpty())
                    if (constraint.isNullOrEmpty()) {
                        collapseParents(false)
                        setDefaultParentStatus()
                        expandAccordingToStatus()
                    }
                }
            }
        }
    }

    private fun nameContains(name: String, search: String) =
        normalize(name).contains(search, ignoreCase = true)

    private fun isMainList(item: ParentData<Other>) : Boolean =
        item.parentTitle?.contains(boughtLiteral) == true

    private fun toggleParentItem(item: ParentData<Other>, position: Int) {
        val parentChanged = this.parentStatus.find { parent -> parent.parentTitle == item.parentTitle }
        parentChanged?.isExpanded = !item.isExpanded

        if (item.isExpanded) {
            collapseParentRow(position)
        } else {
            expandParentRow(position)
        }
    }

    private fun collapseParentRow(position: Int, notify: Boolean = true) {
        val currentRow = filteredOtherList[position]
        val elements = currentRow.subList
        filteredOtherList[position].isExpanded = false
        if (filteredOtherList[position].type == Constants.PARENT) {
            elements?.forEach {_ ->
                if ((position + 1) < filteredOtherList.size)
                    filteredOtherList.removeAt(position + 1)
            }
            if (notify) notifyDataSetChanged()
        }
    }

    private fun expandParentRow(position: Int, notify: Boolean = true) {
        if (position < filteredOtherList.size){
            val currentRow = filteredOtherList[position]
            val elements = currentRow.subList
            currentRow.isExpanded = true
            var nextPosition = position
            if (currentRow.type == Constants.PARENT) {
                elements?.forEach { other ->
                    val subListToAdd: ArrayList<Other> = ArrayList()
                    subListToAdd.add(other)

                    val parentModel = ParentData(type = Constants.CHILD, subList = subListToAdd)
                    filteredOtherList.add(++nextPosition, parentModel)
                }
                if (notify) notifyDataSetChanged()
            }
        }
    }

    private fun expandParentsWithContent(otherList: MutableList<ParentData<Other>>, notify: Boolean = true) {
        otherList.forEach {
            it.isExpanded = it.subList?.isNotEmpty()!!
        }
        for (i in otherList.indices.reversed()) {
            if (otherList[i].isExpanded) {
                expandParentRow(i, false)
            }
        }
        if (notify) notifyDataSetChanged()
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

    fun updateRecyclerData(otherList: MutableList<ParentData<Other>>, updateStatus: Boolean = false) {
        this.otherList.clear()
        this.filteredOtherList.clear()
        this.otherList.addAll(otherList)
        this.otherList.forEach {
            this.filteredOtherList.add(it)
        }

        if (this.parentStatus.isEmpty() || updateStatus) {
            setDefaultParentStatus()
        }
        //expandAccordingToStatus()
    }

    fun getParentData() = this.parentStatus

    private fun setDefaultParentStatus() {
        this.parentStatus.clear()
        this.parentStatus.addAll(this.filteredOtherList
            .filter { item -> item.type == Constants.PARENT }
            .map {item ->
                val subListToAdd = if (item.subList !== null)
                    item.subList?.first()
                else
                    Other(0, 0, item.parentTitle!!, item.parentTitle, 0)
                ParentData(
                    parentTitle = item.parentTitle,
                    type = item.type,
                    isExpanded = isMainList(item),
                    subList = arrayListOf(subListToAdd!!))
            })
    }

    private fun expandAccordingToStatus() {
        this.parentStatus.forEach { parent ->
            val parentPosition = this.filteredOtherList.indexOfFirst { row -> row.parentTitle == parent.parentTitle }
            if (!parent.isExpanded) {
                expandParentRow(parentPosition, false)
            }
        }
        notifyDataSetChanged()
    }

    private fun collapseParents(notify: Boolean = true) {
        this.parentStatus.forEach {parent ->
            val parentPosition = this.filteredOtherList.indexOfFirst { row -> row.parentTitle == parent.parentTitle }
            collapseParentRow(parentPosition, false)
        }
        if (notify) notifyDataSetChanged()
    }
}