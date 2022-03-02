package riper.housebuilder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import kotlinx.android.synthetic.main.activity_edit_expense.*
import kotlinx.android.synthetic.main.content_edit_expense.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.coroutines.CoroutineContext

interface EditExpenseContract {
    interface Presenter {
        fun detach()
        fun updateExpense(expense: Expense)
        fun getData(dataId: Int)
    }

    interface View {
        fun show(expense: Expense)
    }
}

class EditExpensePresenter(private var view: EditExpenseContract.View?, private val dao: ExpenseDao) : CoroutineScope, EditExpenseContract.Presenter {

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    override fun detach() {
        job.cancel()
        view = null
    }

    override fun updateExpense(expense: Expense) {
        launch(Dispatchers.IO) {
            dao.update(expense)
        }
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

class EditExpenseActivity : AppCompatActivity(), EditExpenseContract.View {

    private val formatter = DecimalFormat("0.00")
    private val dao = AppDatabase.getInstance(this).expenseDao()
    private var id = 0
    private var type: ExpenseType = ExpenseType.ITEM_COUNT
    private val presenter: EditExpenseContract.Presenter = EditExpensePresenter(this, dao)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_expense)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter.getData(intent.getIntExtra("id", -1))

        /*lifecycleScope.launch {
            val expense = db.expenseDao().getById(intent.getIntExtra("id", 0))
            if(expense == null) {
                finish()
            }
            else {
                input_name.setText(expense.name)
                input_unit_price.setText(formatter.format(expense.unitPrice))
                input_type.setText(expense.type.getLocalizedTypeName(this@EditExpenseActivity))
                id = expense.id
            }
        }
         */

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
                val expense = Expense(id, name, unitPrice!!, this.type)
                presenter.updateExpense(expense)
                //lifecycleScope.launch(Dispatchers.IO) {
                //    db.expenseDao().update(expense)
                //}
                Toast.makeText(it.context, R.string.edited, Toast.LENGTH_SHORT).show()
                finish()
            }
            else {
                Toast.makeText(it.context, R.string.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }

    override fun show(expense: Expense) {
        input_name.setText(expense.name)
        input_unit_price.setText(formatter.format(expense.unitPrice))
        input_type.setText(expense.type.getLocalizedTypeName(this))
        id = expense.id
    }

    companion object {
        fun start(context: Context, id: Int): Boolean {
            val starter = Intent(context, EditExpenseActivity::class.java)
            starter.putExtra("id", id)
            context.startActivity(starter)
            return true
        }
    }
}
