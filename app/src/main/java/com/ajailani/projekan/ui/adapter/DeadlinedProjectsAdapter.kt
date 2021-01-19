package com.ajailani.projekan.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.databinding.ListDeadlinedProjectBinding
import com.bumptech.glide.Glide

class DeadlinedProjectsAdapter(
        private val deadlinedProjectsList: List<Project>
) : RecyclerView.Adapter<DeadlinedProjectsAdapter.ViewHolder>() {
    private lateinit var binding: ListDeadlinedProjectBinding

    class ViewHolder(private val binding: ListDeadlinedProjectBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.apply {
                if(project.icon != "") {
                    Glide.with(icon.context)
                            .load(project.icon)
                            .into(icon)
                }

                if(project.status == "done") status.visibility = View.VISIBLE else status.visibility = View.INVISIBLE

                title.text = project.title
                desc.text = project.desc
                platform.text = project.platform
                category.text = project.category
                deadline.text = project.deadline
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ListDeadlinedProjectBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(deadlinedProjectsList[position])
    }

    override fun getItemCount() = deadlinedProjectsList.size
}