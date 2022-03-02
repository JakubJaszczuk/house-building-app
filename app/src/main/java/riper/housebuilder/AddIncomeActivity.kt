package riper.housebuilder

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import kotlinx.android.synthetic.main.activity_add_income.*
import kotlinx.android.synthetic.main.content_add_income.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddIncomeActivity : AppCompatActivity() {

    private var date: LocalDate = LocalDate.now()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd LLLL yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_income)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        switch_monthly.setOnCheckedChangeListener { _, isChecked ->
            input_date_begin.isEnabled = !isChecked
        }

        input_date_begin.apply {
            setText(date.format(formatter))
            setOnClickListener {
                val picker = DatePickerDialog(this@AddIncomeActivity, DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    date = LocalDate.of(year, month + 1, day)
                    input_date_begin.setText(date.format(formatter))
                }, date.year, date.monthValue - 1, date.dayOfMonth)
                picker.show()
            }
        }

        button_cancel.setOnClickListener {
            finish()
        }

        button_accept.setOnClickListener {
            //val project = Project(0, input_name.text.toString(), input_description.text.toString(), beginDate, endDate)
            val income = Income(0, input_name.text.toString(), input_value.text.toString().replace(',', '.').toBigDecimalOrNull()?.setScale(2, RoundingMode.FLOOR) ?: BigDecimal.ZERO, switch_monthly.isChecked, date, intent.getIntExtra("id", -1))
            val validator = IncomeValidator(income)
            if(validator.validate()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    AppDatabase.getInstance(applicationContext).incomeDao().add(income)
                }
                Toast.makeText(it.context, R.string.added, Toast.LENGTH_SHORT).show()
                finish()
            }
            else {
                Toast.makeText(it.context, R.string.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun start(context: Context, id: Int): Boolean {
            val starter = Intent(context, AddIncomeActivity::class.java)
            starter.putExtra("id", id)
            context.startActivity(starter)
            return true
        }
    }
}
