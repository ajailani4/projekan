package com.ajailani.projekan.ui.view.fragment

import android.app.AlertDialog
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
import com.ajailani.projekan.ui.view.activity.MainActivity
import com.ajailani.projekan.ui.viewmodel.MoreViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MoreFragment : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var binding: FragmentMoreBinding
    private val moreViewModel: MoreViewModel by activityViewModels()

    private var tagMore = ""
    private var projectMore = Project()

    companion object {
        const val TAG = "More Fragment"
    }

    override fun getTheme(): Int = R.style.CustomMoreDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreBinding.inflate(
            inflater, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Observe tag
        moreViewModel.tag.observe(viewLifecycleOwner, {
            tagMore = it
        })

        //Observe project
        moreViewModel.project.observe(viewLifecycleOwner, {
            projectMore = it
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
                showConfirmDialog()
            }
        }
    }

    private fun showConfirmDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.delete)
            .setMessage(R.string.confirm_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                this.isCancelable = false
                binding.progressBar.visibility = View.VISIBLE
                binding.edit.isEnabled = false
                binding.delete.isEnabled = false

                if(tagMore == "Project") {
                    moreViewModel.deleteProject(projectMore).observe(viewLifecycleOwner, { isProjectDeleted ->
                        if(isProjectDeleted) {
                            Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show()

                            val homeIntent = Intent(context, MainActivity::class.java)
                            startActivity(homeIntent)
                            activity?.finish()
                        } else {
                            Toast.makeText(context, "Unsuccessfully deleted", Toast.LENGTH_SHORT).show()

                            this.isCancelable = true
                            binding.progressBar.visibility = View.GONE
                            binding.edit.isEnabled = true
                            binding.delete.isEnabled = true
                        }
                    })
                }
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.cancel()
            }

        val alertDialog = builder.create()
        alertDialog.show()
    }
}