package com.ajailani.projekan.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.ajailani.projekan.R
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.data.model.Task
import com.ajailani.projekan.databinding.FragmentAddTaskBinding
import com.ajailani.projekan.ui.viewmodel.AddTaskViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddTaskFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddTaskBinding
    private val addTaskViewModel: AddTaskViewModel by activityViewModels()

    private var mTag = ""
    private var mProject = Project()
    private var mTask = Task()

    companion object {
        const val TAG = "Add Task Fragment"
    }

    override fun getTheme() = R.style.CustomAddTaskDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTaskBinding.inflate(
            layoutInflater, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Observe tag
        addTaskViewModel.tag.observe(viewLifecycleOwner, {
            mTag = it
        })

        //Observe project
        addTaskViewModel.project.observe(viewLifecycleOwner, {
            mProject = it
        })

        //Observe task
        addTaskViewModel.task.observe(viewLifecycleOwner, {
            mTask = it

            //Fill the inputTitle
            if (mTag == "Edit") {
                binding.inputTitle.setText(mTask.title)
            }
        })

        //When doneBtn is clicked
        binding.doneBtn.setOnClickListener {
            val task = Task()
            task.title = binding.inputTitle.text.toString()

            if (task.title.isNotEmpty()) {
                this.isCancelable = false
                binding.doneBtn.isEnabled = false
                binding.progressBar.visibility = View.VISIBLE

                if (mTag == "Edit") {
                    task.id = mTask.id
                    task.status = mTask.status
                }

                addOrEditTask(mProject.onPage, mProject.itemNum, task)
            }
        }
    }

    private fun addOrEditTask(page: Int, itemNum: Int, task: Task) {
        if (mTag == "Add") {
            addTaskViewModel.addTask(page, itemNum, task)
                .observe(viewLifecycleOwner, { isTaskAdded ->
                    if (isTaskAdded) {
                        Toast.makeText(context, "Successfully added", Toast.LENGTH_SHORT).show()

                        addTaskViewModel.setAddTask(true)
                        this.dismiss()
                    } else {
                        Toast.makeText(context, "Unsuccessfully added", Toast.LENGTH_SHORT).show()

                        this.isCancelable = true
                        binding.doneBtn.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                    }
                })
        } else if (mTag == "Edit") {

            addTaskViewModel.updateTask(page, itemNum, task)
                .observe(viewLifecycleOwner, { isTaskUpdated ->
                    if (isTaskUpdated) {
                        Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show()

                        this.dismiss()
                    } else {
                        Toast.makeText(context, "Unsuccessfully updated", Toast.LENGTH_SHORT).show()

                        this.isCancelable = true
                        binding.doneBtn.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                    }
                })
        }
    }
}