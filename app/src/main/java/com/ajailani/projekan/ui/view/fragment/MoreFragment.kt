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
import com.ajailani.projekan.data.model.Task
import com.ajailani.projekan.databinding.FragmentMoreBinding
import com.ajailani.projekan.ui.view.activity.AddProjectActivity
import com.ajailani.projekan.ui.view.activity.MainActivity
import com.ajailani.projekan.ui.view.activity.ProjectDetailsActivity
import com.ajailani.projekan.ui.viewmodel.AddTaskViewModel
import com.ajailani.projekan.ui.viewmodel.MoreViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MoreFragment : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var binding: FragmentMoreBinding
    private val moreViewModel: MoreViewModel by activityViewModels()
    private val addTaskViewModel: AddTaskViewModel by activityViewModels()

    private var mTag = ""
    private var mProject = Project()
    private var mTask = Task()

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
            mTag = it
        })

        //Observe project
        moreViewModel.project.observe(viewLifecycleOwner, {
            mProject = it
        })

        //Observe task
        moreViewModel.task.observe(viewLifecycleOwner, {
            mTask = it
        })

        binding.edit.setOnClickListener(this)
        binding.delete.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.edit -> {
                if (mTag == "Project") {
                    val addProjectIntent = Intent(context, AddProjectActivity::class.java)
                    addProjectIntent.putExtra("tag", "Edit")
                    addProjectIntent.putExtra("project", mProject)
                    startActivity(addProjectIntent)
                } else if (mTag == "Task") {
                    //Pass the task in order to AddTaskFragment knows what task will be executed
                    addTaskViewModel.setTag("Edit")
                    addTaskViewModel.setTask(mTask)

                    AddTaskFragment().show(
                        (activity as ProjectDetailsActivity)
                            .supportFragmentManager, AddTaskFragment.TAG
                    )
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

                if (mTag == "Project") {
                    (activity as ProjectDetailsActivity).binding.deleteProjectMsg.visibility =
                        View.VISIBLE

                    moreViewModel.deleteProject(mProject)
                        .observe(viewLifecycleOwner, { isProjectDeleted ->
                            if (isProjectDeleted) {
                                Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT)
                                    .show()

                                val homeIntent = Intent(context, MainActivity::class.java)
                                startActivity(homeIntent)
                                activity?.finish()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Unsuccessfully deleted",
                                    Toast.LENGTH_SHORT
                                ).show()

                                this.isCancelable = true

                                (activity as ProjectDetailsActivity).binding.deleteProjectMsg.visibility =
                                    View.GONE

                                binding.apply {
                                    progressBar.visibility = View.GONE
                                    edit.isEnabled = true
                                    delete.isEnabled = true
                                }
                            }
                        })
                } else if (mTag == "Task") {
                    moreViewModel.deleteTask(mProject.onPage, mProject.itemNum, mTask)
                        .observe(viewLifecycleOwner, { isTaskDeleted ->
                            if (isTaskDeleted) {
                                Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT)
                                    .show()

                                moreViewModel.setDeleteTask(true)
                                this.dismiss()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Unsuccessfully deleted",
                                    Toast.LENGTH_SHORT
                                ).show()

                                this.isCancelable = true

                                binding.apply {
                                    progressBar.visibility = View.GONE
                                    edit.isEnabled = true
                                    delete.isEnabled = true
                                }
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