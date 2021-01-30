package com.ajailani.projekan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajailani.projekan.data.model.Task
import com.ajailani.projekan.databinding.ItemTaskBinding

class TasksAdapter(
    private val tasksList: List<Task>,
    private val listener: (Task) -> Unit
): RecyclerView.Adapter<TasksAdapter.ViewHolder>() {
    private lateinit var binding: ItemTaskBinding

    class ViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task, listener: (Task) -> Unit) {
            binding.apply {
                title.text = task.title

                if(task.status == "done") {
                    status.isChecked = true
                }

                moreIv.setOnClickListener {
                    listener(task)
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
        holder.bind(tasksList[position], listener)
    }

    override fun getItemCount() = tasksList.size
}