package com.ajailani.projekan.ui.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.ajailani.projekan.R
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.databinding.FragmentMoreBinding
import com.ajailani.projekan.ui.view.activity.AddProjectActivity
import com.ajailani.projekan.ui.viewmodel.MoreViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MoreFragment : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var binding: FragmentMoreBinding
    private val moreViewModel: MoreViewModel by activityViewModels()

    private var tagMore = ""
    private var projectMore = Project()

    companion object {
        const val TAG = "More Fragment"
    }

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(
            inflater, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        moreViewModel.tag.observe(viewLifecycleOwner, { tag ->
            tagMore = tag
        })

        moreViewModel.project.observe(viewLifecycleOwner, { project ->
            projectMore = project
        })

        binding.edit.setOnClickListener(this)
        binding.delete.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.edit -> {
                if(tagMore == "Project") {
                    val addProjectIntent = Intent(context, AddProjectActivity::class.java)
                    addProjectIntent.putExtra("tag", "Edit")
                    addProjectIntent.putExtra("project", projectMore)
                    startActivity(addProjectIntent)
                }
            }
            binding.delete -> {
                Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show()
            }
        }
    }
}