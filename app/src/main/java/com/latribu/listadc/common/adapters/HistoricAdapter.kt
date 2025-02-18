package com.latribu.listadc.common.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.latribu.listadc.R
import com.latribu.listadc.common.models.Historic
import com.latribu.listadc.databinding.FragmentHistoricItemBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class HistoricAdapter(
    val longClickListener: (Historic) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var historicList = ArrayList<Historic>()
    private var filteredHistoricList = ArrayList<Historic>()

    init {
        filteredHistoricList = historicList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentHistoricItemBinding.inflate(inflater, parent, false)
        return HistoricViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: Historic = filteredHistoricList[position]
        val color = if (position % 2 == 0) "#33BABABA" else "#00000000"
        holder.itemView.setBackgroundColor(Color.parseColor(color))
        (holder as HistoricViewHolder).bind(item, longClickListener)
    }

    inner class HistoricViewHolder(private val binding: FragmentHistoricItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Historic, longClickListener: (Historic) -> Unit) {
            val code = item.operationId.toString()
            val iconName: Int = getIconName(code)
            val iconColor: Int = getIconColor(code)
            binding.operationIcon.setImageState(intArrayOf(iconName), true)
            binding.operationIcon.setTint(iconColor)
            binding.operationElement.text = item.itemName
            binding.operationDate.text = getDisplayDateTime(item.createdAt)

            binding.root.setOnLongClickListener {
                longClickListener(item)
                true
            }
        }
        private fun getIconName(operation: String): Int {
            return when(operation) {
                "1" -> R.attr.create
                "2" -> R.attr.update
                "3" -> R.attr.uncheck
                "4" -> R.attr.check
                "5" -> R.attr.delete
                else -> R.drawable.twotone_question_mark_24
            }
        }
        private fun getIconColor(operation: String): Int {
            return when(operation) {
                "1" -> R.color.teal_700
                "2" -> R.color.purple_200
                "5" -> R.color.red
                else -> R.color.grey
            }
        }
    }

    private fun ImageView.setTint(@ColorRes colorRes: Int) {
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)))
    }

    private fun getDisplayDateTime(dateTime: String): String {
        try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
            val date = simpleDateFormat.parse(dateTime)
            val convertDateFormat = SimpleDateFormat("dd LLL HH:mm", Locale.getDefault())
            return convertDateFormat.format(date)
        } catch (e: Exception) {
            return ""
        }
    }

    override fun getItemCount(): Int = filteredHistoricList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                // CharSequence follows the 'days|user' structure
                if (!constraint.isNullOrEmpty()) {
                    val filters = constraint.split('|')

                    filteredHistoricList = historicList.filter { row ->
                        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val created = LocalDateTime.parse(row.createdAt, pattern)
                        val createdDay = created.dayOfYear
                        val today = LocalDateTime.now()
                        val todayDay = today.dayOfYear
                        val days: Long = filters[0].toLong()
                        val appliedFilterDay = todayDay - days

                        if (filters[0] == "1") {
                            createdDay == appliedFilterDay.toInt() &&
                                    (row.userName == filters[1] || filters[1] == "Todos")
                        } else {
                            createdDay >= appliedFilterDay &&
                                    (row.userName == filters[1] || filters[1] == "Todos")
                        }

                    } as ArrayList<Historic>
                }
                filterResults.values = filteredHistoricList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results?.values != null && results.values != 0) {
                    filteredHistoricList = results.values as ArrayList<Historic>
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun updateRecyclerData(historicList: List<Historic>) {
        this.historicList.clear()
        this.historicList.addAll(historicList)
        this.filteredHistoricList = this.historicList
        notifyDataSetChanged()
    }
}