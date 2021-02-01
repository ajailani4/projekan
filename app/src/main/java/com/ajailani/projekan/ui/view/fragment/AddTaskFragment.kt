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

    private var projectAddTask = Project()

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

        //Observe project
        addTaskViewModel.project.observe(viewLifecycleOwner, {
            projectAddTask = it
        })

        //When doneBtn is clicked
        binding.doneBtn.setOnClickListener {
            val task = Task()
            task.title = binding.inputTitle.text.toString()

            if (task.title.isNotEmpty()) {
                this.isCancelable = false
                binding.doneBtn.isEnabled = false
                binding.progressBar.visibility = View.VISIBLE

                addTask(task, projectAddTask.onPage, projectAddTask.itemNum)
            }
        }
    }

    private fun addTask(task: Task, page: Int, itemNum: Int) {
        addTaskViewModel.addTask(task, page, itemNum).observe(this, { isTaskAdded ->
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
    }
}