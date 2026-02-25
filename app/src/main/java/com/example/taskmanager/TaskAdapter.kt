package com.example.taskmanager

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.data.TaskEntity

class TaskAdapter(
    private var tasks: List<TaskEntity>,
    private var onDeleteItem: (TaskEntity) -> Unit,
    private var onEditItem: (TaskEntity) -> Unit,
    private val onTaskChecked: (TaskEntity) -> Unit,
) :
    RecyclerView.Adapter<TaskAdapter.TaskVH>() {

    class TaskVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txtTaskTitle)
        val desc: TextView = itemView.findViewById(R.id.txtTaskDesc)
        val iconDelete: ImageView = itemView.findViewById(R.id.iconDelete)
        val iconEdit: ImageView = itemView.findViewById(R.id.iconEdit)
        val priorityDot: ImageView = itemView.findViewById(R.id.iconPriority)
        val checkTask: CheckBox = itemView.findViewById(R.id.checkboxTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVH {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.task_list_layout, parent, false)
        return TaskVH(view)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TaskVH, position: Int) {
        val i = tasks[position]
        val context = holder.itemView.context
        holder.title.text = i.taskTitle
        holder.desc.text = i.taskDesc

        // priority color dot change by priority
        when (i.priority) {
            "High" -> holder.priorityDot.setColorFilter(
                ContextCompat.getColor(context, R.color.high_pill)
            )

            "Medium" -> holder.priorityDot.setColorFilter(
                ContextCompat.getColor(context, R.color.medium_pill)
            )

            "Low" -> holder.priorityDot.setColorFilter(
                ContextCompat.getColor(context, R.color.low_pill)
            )
        }

        // for task completed
        holder.checkTask.setOnCheckedChangeListener(null)
        holder.checkTask.isChecked = i.isCompleted
        if (i.isCompleted) {
            holder.title.paintFlags = holder.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.title.alpha = 0.5f
            holder.desc.alpha = 0.5f
        } else {
            holder.title.paintFlags = holder.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.title.alpha = 1f
            holder.desc.alpha = 1f
        }

        // checkbox clicked
        holder.checkTask.setOnCheckedChangeListener { _, isChecked ->
            val updateTask = i.copy(isCompleted = isChecked)
            onTaskChecked(updateTask)
        }

        // on delete icon click
        holder.iconDelete.setOnClickListener {
            onDeleteItem(i)
        }

        // on edit icon click
        holder.iconEdit.setOnClickListener {
            onEditItem(i)
        }

    }

    fun updateList(newList: List<TaskEntity>) {
        tasks = newList
        notifyDataSetChanged()
    }

}