package br.edu.ufabc.todocloud.view

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import br.edu.ufabc.todocloud.R
import br.edu.ufabc.todocloud.databinding.ListItemBinding
import br.edu.ufabc.todocloud.model.Task
import com.google.android.material.chip.Chip
import java.util.*

/**
 * Adapter for task lists.
 */
class TaskAdapter(private val tasks: List<Task>, private val navController: NavController) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    /**
     * ViewHolder for a task adapter.
     */
    inner class TaskViewHolder(itemBinding: ListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        /**
         * The title field.
         */
        val title = itemBinding.textviewTitle

        /**
         * The layout that store tags.
         */
        val tagsContainer = itemBinding.containerTags

        /**
         * The deadline field.
         */
        val deadline = itemBinding.textviewDeadline

        init {
            itemBinding.root.setOnClickListener {
                ListFragmentDirections.showDetail(getItemId(bindingAdapterPosition)).let {
                    navController.navigate(it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.title.text = task.title
        holder.deadline.text = if (task.deadline != null) task.formattedDeadline() else ""
        if (task.completed)
            holder.title.setTextColor(
                ContextCompat.getColor(
                    holder.title.context,
                    R.color.gray
                )
            )
        holder.title.paintFlags =
            if (task.completed)
                holder.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else
                holder.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        holder.deadline.setTextColor(
            ContextCompat.getColor(
                holder.deadline.context,
                when {
                    task.completed -> R.color.gray
                    task.deadline?.before(Task.simplifyDate(Date())) ?: false -> R.color.red
                    else -> R.color.black
                }
            )
        )
        task.tags?.sorted()?.forEach { tag ->
            LayoutInflater.from(holder.tagsContainer.context)
                .inflate(R.layout.chip, holder.tagsContainer, false).let { view ->
                    (view as Chip).apply {
                        text = tag
                        setOnClickListener {
                            ListFragmentDirections.onTagClick(
                                FilterCriteria.TAG,
                                "Tag: $tag",
                                tag
                            )
                                .let {
                                    navController.navigate(it)
                                }
                        }
                        holder.tagsContainer.addView(this)
                    }
                }
        }
    }

    override fun onViewRecycled(holder: TaskViewHolder) {
        super.onViewRecycled(holder)
        holder.tagsContainer.removeAllViews()
    }

    override fun getItemCount(): Int = tasks.size

    override fun getItemId(position: Int): Long = tasks[position].id
}