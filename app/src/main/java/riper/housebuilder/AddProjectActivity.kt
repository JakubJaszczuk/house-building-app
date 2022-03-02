package riper.housebuilder

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import kotlinx.android.synthetic.main.activity_add_project.*
import kotlinx.android.synthetic.main.content_add_project.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddProjectActivity : AppCompatActivity() {

    private var beginDate: LocalDate = LocalDate.now()
    private var endDate: LocalDate = LocalDate.now().plusYears(1)
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd LLLL yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_project)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        input_date_begin.apply {
            setText(beginDate.format(formatter))
            setOnClickListener {
                val picker = DatePickerDialog(this@AddProjectActivity, DatePickerDialog.OnDateSetListener {_, year, month, day ->
                    beginDate = LocalDate.of(year, month + 1, day)
                    input_date_begin.setText(beginDate.format(formatter))
                }, beginDate.year, beginDate.monthValue - 1, beginDate.dayOfMonth)
                picker.show()
            }
        }

        input_date_end.apply {
            setText(endDate.format(formatter))
            setOnClickListener {
                val picker = DatePickerDialog(this@AddProjectActivity, DatePickerDialog.OnDateSetListener {_, year, month, day ->
                    endDate = LocalDate.of(year, month + 1, day)
                    input_date_end.setText(endDate.format(formatter))
                }, endDate.year, endDate.monthValue - 1, endDate.dayOfMonth)
                picker.show()
            }
        }

        button_cancel.setOnClickListener {
            finish()
        }

        button_accept.setOnClickListener {
            val project = Project(0, input_name.text.toString(), input_description.text.toString(), beginDate, endDate)
            val validator = ProjectValidator(project)
            if(validator.validate()) {
                // TODO
                lifecycleScope.launch(Dispatchers.IO) {
                    AppDatabase.getInstance(applicationContext).projectDao().add(project)
                }
                Toast.makeText(it.context, R.string.add_project_success, Toast.LENGTH_SHORT).show()
                finish()
            }
            else {
                Toast.makeText(it.context, R.string.add_project_fail, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun start(context: Context): Boolean {
            val starter = Intent(context, AddProjectActivity::class.java)
            context.startActivity(starter)
            return true
        }
    }
}
