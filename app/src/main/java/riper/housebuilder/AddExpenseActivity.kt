package riper.housebuilder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import kotlinx.android.synthetic.main.activity_add_expense.*
import kotlinx.android.synthetic.main.content_add_expense.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal

class AddExpenseActivity : AppCompatActivity() {

    private val db = AppDatabase.getInstance(this)
    private var type: ExpenseType = ExpenseType.ITEM_COUNT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val expenses = ExpenseType.values()
        val expensesNames = Array(expenses.size) {i -> expenses[i].getLocalizedTypeName(this)}
        val typeAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, expensesNames)
        input_type.setAdapter(typeAdapter)

        input_type.setOnItemClickListener { _, _, position, _ ->
            type = expenses[position]
        }

        button_cancel.setOnClickListener {
            finish()
        }

        button_accept.setOnClickListener {
            val name = input_name.text.toString()
            //val type = input_type.text.toString()
            val unitPrice = input_unit_price.text.toString().replace(',', '.').toBigDecimalOrNull()
            val validator = ExpenseValidator(name, unitPrice)
            if(validator.validate()) {
                val expense = Expense(0, name, unitPrice!!, this.type)
                lifecycleScope.launch(Dispatchers.IO) {
                    db.expenseDao().add(expense)
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
        fun start(context: Context): Boolean {
            val starter = Intent(context, AddExpenseActivity::class.java)
            context.startActivity(starter)
            return true
        }
    }

}
