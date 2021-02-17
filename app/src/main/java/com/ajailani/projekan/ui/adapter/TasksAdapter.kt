package com.ajailani.projekan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajailani.projekan.data.model.Task
import com.ajailani.projekan.databinding.ItemTaskBinding

class TasksAdapter(
    private val tasksList: List<Task>,
    private val statusListener: (String, String) -> Unit,
    private val moreListener: (Task) -> Unit
) : RecyclerView.Adapter<TasksAdapter.ViewHolder>() {
    private lateinit var binding: ItemTaskBinding

    class ViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            task: Task,
            statusListener: (String, String) -> Unit,
            moreListener: (Task) -> Unit
        ) {
            binding.apply {
                title.text = task.title

                if (task.status == "done") status.isChecked = true

                //Handle status CheckBox event
                status.setOnClickListener {
                    if (status.isChecked) {
                        task.id?.let { id -> statusListener(id, "done") }
                    } else {
                        task.id?.let { id -> statusListener(id, "undone") }
                    }
                }

                moreIv.setOnClickListener {
                    moreListener(task)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tasksList[position], statusListener, moreListener)
    }

    override fun getItemCount() = tasksList.size
}