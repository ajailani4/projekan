package com.ajailani.projekan.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.databinding.ItemDeadlinedProjectBinding
import com.bumptech.glide.Glide

class DeadlinedProjectsHeaderAdapter(
    private val deadlinedProjectsList: List<Project>,
    private val listener: (Int, Int) -> Unit
) : RecyclerView.Adapter<DeadlinedProjectsHeaderAdapter.ViewHolder>() {
    private lateinit var binding: ItemDeadlinedProjectBinding

    class ViewHolder(private val binding: ItemDeadlinedProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project, listener: (Int, Int) -> Unit) {
            binding.apply {
                if (project.icon != "") {
                    Glide.with(icon.context)
                        .load(project.icon)
                        .into(icon)
                }

                if (project.status == "done") status.visibility =
                    View.VISIBLE else status.visibility = View.INVISIBLE

                title.text = project.title
                desc.text = project.desc
                platform.text = project.platform
                category.text = project.category
                deadline.text = project.deadline

                root.setOnClickListener {
                    listener(project.onPage, project.itemNum)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemDeadlinedProjectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(deadlinedProjectsList[position], listener)
    }

    override fun getItemCount() = deadlinedProjectsList.size
}