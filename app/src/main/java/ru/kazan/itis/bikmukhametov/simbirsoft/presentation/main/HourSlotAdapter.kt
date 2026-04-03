package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import ru.kazan.itis.bikmukhametov.simbirsoft.databinding.ItemHourSlotBinding
import ru.kazan.itis.bikmukhametov.simbirsoft.databinding.ItemHourTaskRowBinding
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.HourSlot
import ru.kazan.itis.bikmukhametov.simbirsoft.domain.model.TaskItem

class HourSlotAdapter(
    private val onTaskClick: (Long) -> Unit
) : RecyclerView.Adapter<HourSlotAdapter.HourSlotViewHolder>() {
    private val items = mutableListOf<HourSlot>()
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun submitItems(newItems: List<HourSlot>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourSlotViewHolder {
        val binding = ItemHourSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourSlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourSlotViewHolder, position: Int) {
        holder.bind(items[position], onTaskClick, timeFormatter)
    }

    override fun getItemCount(): Int = items.size

    class HourSlotViewHolder(
        private val binding: ItemHourSlotBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            hourSlot: HourSlot,
            onTaskClick: (Long) -> Unit,
            timeFormatter: DateTimeFormatter
        ) {
            binding.slotRangeText.text = String.format(
                Locale.getDefault(),
                "%02d:00 - %02d:00",
                hourSlot.hourStart,
                hourSlot.hourEnd
            )

            val tasks = hourSlot.tasks
            val container = binding.tasksList
            container.removeAllViews()

            if (tasks.isEmpty()) {
                binding.taskBlock.visibility = View.GONE
                return
            }

            binding.taskBlock.visibility = View.VISIBLE

            val inflater = LayoutInflater.from(binding.root.context)
            tasks.forEachIndexed { index, task ->
                val rowBinding = ItemHourTaskRowBinding.inflate(inflater, container, true)
                rowBinding.rowTitle.text = task.name
                rowBinding.rowTime.text = task.toTimeRange(timeFormatter)
                rowBinding.rowDivider.visibility =
                    if (index < tasks.lastIndex) View.VISIBLE else View.GONE
                rowBinding.root.setOnClickListener {
                    onTaskClick(task.id)
                }
            }
        }

        private fun TaskItem.toTimeRange(formatter: DateTimeFormatter): String {
            val zone = ZoneId.systemDefault()
            val start = Instant.ofEpochMilli(dateStart).atZone(zone).toLocalTime().format(formatter)
            val end = Instant.ofEpochMilli(dateFinish).atZone(zone).toLocalTime().format(formatter)
            return "$start - $end"
        }
    }
}
