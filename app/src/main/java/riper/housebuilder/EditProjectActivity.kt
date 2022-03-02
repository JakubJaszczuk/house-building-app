package riper.housebuilder

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_edit_project.toolbar
import kotlinx.android.synthetic.main.content_add_project.*
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext

interface EditProjectContract {
    interface Presenter {
        fun detach()
        fun updateProject(project: Project): ValidationResult
        fun getData(dataId: Int)
    }

    interface View {
        fun show(project: Project)
    }
}

class EditProjectPresenter(private var view: EditProjectContract.View?, private val dao: ProjectDao) : CoroutineScope, EditProjectContract.Presenter {

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    override fun detach() {
        job.cancel()
        view = null
    }

    override fun updateProject(project: Project): ValidationResult {
        val validator = ProjectValidator2(project)
        val validationResult = validator.validate()
        if(!validationResult.isError) {
            launch(Dispatchers.IO) {
                dao.update(project)
            }
        }
        return validationResult
    }

    override fun getData(dataId: Int) {
        launch(Dispatchers.Main) {
            val data = dao.getById(dataId)
            if(data != null) {
                view?.show(data)
            }
        }
    }
}

class EditProjectActivity : AppCompatActivity(), EditProjectContract.View {

    private lateinit var currentProject: Project
    private lateinit var beginDate: LocalDate
    private lateinit var endDate: LocalDate
    private val dao = AppDatabase.getInstance(this).projectDao()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd LLLL yyyy")
    private val presenter: EditProjectContract.Presenter = EditProjectPresenter(this, dao)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_project)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter.getData(intent.getIntExtra("id", -1))

        input_date_begin.setOnClickListener {
            val picker = DatePickerDialog(
                this@EditProjectActivity,
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    beginDate = LocalDate.of(year, month + 1, day)
                    input_date_begin.setText(beginDate.format(formatter))
                },
                beginDate.year,
                beginDate.monthValue - 1,
                beginDate.dayOfMonth
            )
            picker.show()
        }

        input_date_end.setOnClickListener {
            val picker = DatePickerDialog(
                this@EditProjectActivity,
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    endDate = LocalDate.of(year, month + 1, day)
                    input_date_end.setText(endDate.format(formatter))
                },
                endDate.year,
                endDate.monthValue - 1,
                endDate.dayOfMonth
            )
            picker.show()
        }

        button_cancel.setOnClickListener {
            finish()
        }

        button_accept.setOnClickListener {
            val project = Project(currentProject.id, input_name.text.toString(), input_description.text.toString(), beginDate, endDate)
            val validationResult = presenter.updateProject(project)
            Toast.makeText(it.context, validationResult.messageResId, Toast.LENGTH_SHORT).show()
            if(!validationResult.isError) {
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }

    override fun show(project: Project) {
        currentProject = project
        beginDate = project.beginDate
        endDate = project.endDate
        input_name.setText(project.name)
        input_description.setText(project.description)
        input_date_begin.setText(beginDate.format(formatter))
        input_date_end.setText(endDate.format(formatter))
    }

    companion object {
        fun start(context: Context, id: Int): Boolean {
            val starter = Intent(context, EditProjectActivity::class.java)
            starter.putExtra("id", id)
            context.startActivity(starter)
            return true
        }
    }
}




/*
val future = lifecycleScope.async(Dispatchers.IO) {
    dao.getById(intent.getIntExtra("id", 0))
}
 */



/*
lifecycleScope.launch {
    val tmp = future.await()
    if(tmp == null) {
        finish()
    }
    else {
        currentProject = tmp
        beginDate = currentProject.beginDate
        endDate = currentProject.endDate
        input_name.setText(currentProject.name)
        input_description.setText(currentProject.description)
        input_date_begin.setText(beginDate.format(formatter))
        input_date_end.setText(endDate.format(formatter))
    }
}
*/


/*
button_accept.setOnClickListener {
    val project = Project(currentProject.id, input_name.text.toString(), input_description.text.toString(), beginDate, endDate)
    val validator = ProjectValidator(project)
    if(validator.validate()) {
        presenter.updateProject(project)
        /*
        lifecycleScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance(applicationContext).projectDao().update(project)
        }
        */
        Toast.makeText(it.context, R.string.edit_project_success, Toast.LENGTH_SHORT).show()
        finish()
    }
    else {
        Toast.makeText(it.context, R.string.error, Toast.LENGTH_SHORT).show()
    }
}
*/
