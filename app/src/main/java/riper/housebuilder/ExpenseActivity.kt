package riper.housebuilder

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_expense.*
import kotlinx.android.synthetic.main.content_expense.*
import kotlinx.android.synthetic.main.expense_list_item.view.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat

class ExpenseListAdapter(private val data: ArrayList<Expense>, private val context: Context) : RecyclerView.Adapter<ExpenseListAdapter.ViewHolder>() {

    private val formatter = DecimalFormat("0.00")

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textName: TextView = itemView.text_name
        var textUnitPrice: TextView = itemView.text_unitPrice
        var textType: TextView = itemView.text_type
        var buttonMore: ImageButton = itemView.button_more
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textName.text = data[position].name
        viewHolder.textUnitPrice.text = formatter.format(data[position].unitPrice)
        viewHolder.textType.text = data[position].type.getLocalizedTypeName(context)
        viewHolder.buttonMore.setOnClickListener { view ->
            val menu = PopupMenu(context, view)
            menu.apply {
                inflate(R.menu.menu_expense_list_item)
                setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.action_delete_expense -> {
                            val dialog = AlertDialog.Builder(context)
                            dialog.apply {
                                setTitle(R.string.are_you_sure)
                                setPositiveButton(R.string.delete) { _, _ ->
                                    runBlocking {
                                        val count = AppDatabase.getInstance(context).expenseDao().delete(data[position].id)
                                        Log.d("ExpenseListAdapter", "Deleted: $count")
                                    }
                                    data.removeAt(position)
                                    notifyItemRemoved(position)
                                    notifyItemRangeChanged(position, data.size)
                                }
                                setNegativeButton(R.string.cancel) { _, _ -> Unit }
                                show()
                            }
                            true
                        }
                        R.id.action_edit_expense -> {
                            EditExpenseActivity.start(context, data[position].id)
                            true
                        }
                        else -> false
                    }
                }
                show()
            }
        }
    }

    override fun getItemCount() = data.size
}

class ExpenseActivity : AppCompatActivity() {

    private val expenses: ArrayList<Expense> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fab.setOnClickListener {
            AddExpenseActivity.start(this)
        }

        expense_list.layoutManager = LinearLayoutManager(this)
        expense_list.setHasFixedSize(true)
        expense_list.adapter = ExpenseListAdapter(expenses, this)
    }

    override fun onResume() {
        super.onResume()
        expenses.clear()
        lifecycleScope.launch {
            expenses.addAll(AppDatabase.getInstance(applicationContext).expenseDao().getAll())
            Log.e("ExpenseActivity", "${expenses.size}")
            expense_list.adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_expense, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_global_expense -> {
                AddExpenseActivity.start(this)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun start(context: Context): Boolean {
            val starter = Intent(context, ExpenseActivity::class.java)
            context.startActivity(starter)
            return true
        }
    }
}
