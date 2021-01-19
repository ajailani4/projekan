package com.ajailani.projekan.ui.view.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.children
import com.ajailani.projekan.R
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.databinding.ActivityAddProjectBinding
import com.ajailani.projekan.ui.viewmodel.AddProjectViewModel
import com.ajailani.projekan.utils.CategoryList
import com.ajailani.projekan.utils.PlatformList
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.text.DateFormatSymbols
import java.util.*

@AndroidEntryPoint
class AddProjectActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddProjectBinding
    private val addProjectViewModel: AddProjectViewModel by viewModels()

    private val REQUEST_CODE_GALLERY = 1
    private var curImageBitmap: ByteArray? = null

    private var platform = ""
    private var category = ""
    private var deadline = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Project"
        supportActionBar?.elevation = 0F

        setupPlatformChipView()
        setupCategoryChipView()
        getCurImageBitmap()

        //When buttons are clicked
        binding.inputDeadline.setOnClickListener(this)
        binding.enterDateIv.setOnClickListener(this)
        binding.inputProjectIcon.setOnClickListener(this)
        binding.doneBtn.setOnClickListener(this)
    }

    private fun setupPlatformChipView() {
        //Setup chips
        PlatformList.list.forEach { chipText ->
            val chip = layoutInflater.inflate(
                R.layout.chip_layout, binding.platormChipGroup, false
            ) as Chip
            chip.text = chipText

            binding.platormChipGroup.addView(chip)
        }

        //Handling checked chips
        binding.platormChipGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.platormChipGroup.children.forEach {
                val chip = it as Chip

                if(chip.id == checkedId) {
                    chip.chipStrokeWidth = 0F
                    platform = chip.text.toString()
                } else chip.chipStrokeWidth = 3.0F
            }
        }
    }

    private fun setupCategoryChipView() {
        //Setup chips
        CategoryList.list.forEach { chipText ->
            val chip = layoutInflater.inflate(
                R.layout.chip_layout, binding.platormChipGroup, false
            ) as Chip
            chip.text = chipText

            binding.categoryChipGroup.addView(chip)
        }

        //Handling checked chips
        binding.categoryChipGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.categoryChipGroup.children.forEach {
                val chip = it as Chip

                if(chip.id == checkedId) {
                    chip.chipStrokeWidth = 0F
                    category = chip.text.toString()
                } else chip.chipStrokeWidth = 3.0F
            }
        }
    }

    private fun getCurImageBitmap() {
        val vto = binding.projectIconIv.viewTreeObserver

        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                binding.projectIconIv.viewTreeObserver.removeOnPreDrawListener(this)
                curImageBitmap = getImageBytes()
                return true
            }
        })
    }

    private fun getImageBytes(): ByteArray {
        //Draw view from ImageView to Bitmap
        val bitmap = getBitmapFromView(binding.projectIconIv)
        val outputStream = ByteArrayOutputStream()

        //Compress the Bitmap to JPG with 100% image quality
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        return outputStream.toByteArray()
    }

    private fun getBitmapFromView(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap
    }

    private fun setupAddProject(project: Project) {
        var iconUrl = ""

        if(!getImageBytes().contentEquals(curImageBitmap)) {
            addProjectViewModel.uploadProjectIcon(getImageBytes())?.observe(this, {
                iconUrl = it
                addProject(project, iconUrl)
            })
        } else {
            addProject(project, iconUrl)
        }
    }

    private fun addProject(project: Project, iconUrl: String) {
        addProjectViewModel.addProject(project, iconUrl)?.observe(this, { isProjectAdded ->
            if(isProjectAdded) {
                binding.progressBar.root.visibility = View.VISIBLE
                Toast.makeText(this, "Successfully added", Toast.LENGTH_SHORT).show()

                val homeIntent = Intent(applicationContext, MainActivity::class.java)
                startActivity(homeIntent)
                finish()
            } else {
                binding.doneBtn.isEnabled = true
                Toast.makeText(this, "Unsuccessfully added", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE_GALLERY) {
            val uri = data?.data
            binding.projectIconIv.setImageURI(uri)
        }
    }

    override fun onClick(v: View?) {
        //Show DatePickerDialog to choose deadline date
        if(v == binding.inputDeadline || v == binding.enterDateIv) {
            val calendar = Calendar.getInstance()
            val calYear = calendar.get(Calendar.YEAR)
            val calMonth = calendar.get(Calendar.MONTH)
            val calDay = calendar.get(Calendar.DAY_OF_MONTH)

            if(Build.VERSION.SDK_INT >= 21) {
                DatePickerDialog(this, R.style.SpinnerDatePickerStyle, { _, year, month, dayOfMonth ->
                    val deadlineDate = "$dayOfMonth ${DateFormatSymbols().months[month].substring(0, 3)} $year"
                    deadline = deadlineDate

                    binding.inputDeadline.setText(deadlineDate)
                }, calYear, calMonth, calDay).show()
            } else {
                DatePickerDialog(this, { _, year, month, dayOfMonth ->
                    val deadlineDate = "$dayOfMonth ${DateFormatSymbols().months[month].substring(0, 3)} $year"
                    deadline = deadlineDate

                    binding.inputDeadline.setText(deadlineDate)
                }, calYear, calMonth, calDay).show()
            }
        } else if(v == binding.inputProjectIcon) {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY)
        } else if(v == binding.doneBtn) {
            val project = Project()

            binding.apply {
                project.title = inputTitle.text.toString()
                project.desc = inputDesc.text.toString()
                project.platform = platform
                project.category = category
                project.deadline = deadline
            }

            if(project.title.isNotEmpty() && project.desc.isNotEmpty() && project.platform != ""
                && project.category != "" && project.deadline != ""
            ) {
                binding.progressBar.root.visibility = View.VISIBLE
                binding.doneBtn.isEnabled = false

                //This means that we have to upload the icon first, then we put/patch the new project into database
                setupAddProject(project)
            } else {
                Toast.makeText(this, "Fill the form completely", Toast.LENGTH_SHORT).show()
            }
        }
    }
}