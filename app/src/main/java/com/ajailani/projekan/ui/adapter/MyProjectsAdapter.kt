package com.ajailani.projekan.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.databinding.ItemMyProjectBinding
import com.bumptech.glide.Glide

class MyProjectsAdapter(
    private val listener: (Int, Int) -> Unit
) : PagingDataAdapter<Project, MyProjectsAdapter.ViewHolder>(DataDifferentiator) {
    private lateinit var binding: ItemMyProjectBinding

    object DataDifferentiator : DiffUtil.ItemCallback<Project>() {
        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem == newItem
        }

    }

    class ViewHolder(private val binding: ItemMyProjectBinding) :
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
        binding = ItemMyProjectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { project ->
            holder.bind(project, listener)
        }
    }
}