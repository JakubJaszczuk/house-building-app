package riper.housebuilder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import kotlinx.android.synthetic.main.activity_add_spending.*
import kotlinx.android.synthetic.main.content_add_spending.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

class AddSpendingActivity : AppCompatActivity() {

    private val db = AppDatabase.getInstance(this)
    private var expense: Expense? = null
    private lateinit var room: Room

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_spending)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        button_cancel.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            val expenses = db.expenseDao().getAll()
            val expenseAdapter = ArrayAdapter(this@AddSpendingActivity, R.layout.support_simple_spinner_dropdown_item, expenses)
            input_expense.setAdapter(expenseAdapter)
        }

        lifecycleScope.launch {
            val dummyRoom = Room(-1, "Brak (dotyczy całego domu)", 0.0, 0.0, 0.0, 0)
            room = dummyRoom
            input_room.setText(room.name)
            val rooms = arrayOf(dummyRoom) + db.projectDao().getRooms(intent.getIntExtra("id", 0))
            val roomsAdapter = ArrayAdapter(this@AddSpendingActivity, R.layout.support_simple_spinner_dropdown_item, rooms)
            input_room.setAdapter(roomsAdapter)
        }

        input_expense.setOnItemClickListener { parent, _, position, _ ->
            expense = parent.getItemAtPosition(position) as Expense
            disableQuantityInput()
        }

        input_expense.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus) {
                expense?.name.let { input_expense.setText(it) }
            }
        }

        input_room.setOnItemClickListener { parent, _, position, _ ->
            room = parent.getItemAtPosition(position) as Room
            disableQuantityInput()
        }

        button_accept.setOnClickListener {
            val id = intent.getIntExtra("id", 0)
            val desc = input_description.text.toString()
            var quantity = input_quantity.text.toString().replace(',', '.').toBigDecimalOrNull()
            if(expense?.type == ExpenseType.ITEM_COUNT) {
                quantity = quantity?.setScale(0, RoundingMode.FLOOR)
            }
            val validator = SpendingValidator(id, expense?.id, quantity)
            if(validator.validate()) {
                val roomId = if(room.id == -1) null else room.id
                val spending = Spending(0, id, roomId, expense!!.id, quantity!!, desc, false)
                lifecycleScope.launch(Dispatchers.IO) {
                    db.spendingDao().add(spending)
                }
                Toast.makeText(it.context, R.string.added, Toast.LENGTH_SHORT).show()
                finish()
            }
            else {
                Toast.makeText(it.context, R.string.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun disableQuantityInput() {
        if(expense?.type == ExpenseType.ITEM_COUNT){
            input_quantity.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        }
        else{
            input_quantity.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        if(expense?.type == ExpenseType.ITEM_COUNT || room.id == -1) {
            input_quantity.isEnabled = true
        }
        else {
            input_quantity.isEnabled = false
            val value = when(expense?.type) {
                ExpenseType.SQUARE_WALL -> room.wallArea
                ExpenseType.SQUARE_FLOOR -> room.floorArea
                ExpenseType.LENGTH -> room.perimeter
                else -> 0.0
            }
            input_quantity.setText(value.toString())
        }
    }

    companion object {
        fun start(context: Context, id: Int): Boolean {
            val starter = Intent(context, AddSpendingActivity::class.java)
            starter.putExtra("id", id)
            context.startActivity(starter)
            return true
        }
    }
}
