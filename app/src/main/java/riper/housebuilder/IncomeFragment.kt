package riper.housebuilder

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_income.*
import kotlinx.android.synthetic.main.income_list_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter

class IncomeListAdapter(private val data: ArrayList<Income>, private val context: Context) : RecyclerView.Adapter<IncomeListAdapter.ViewHolder>() {

    private val formatter = DecimalFormat("0.00")

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textName: TextView = itemView.text_name
        var textValue: TextView = itemView.text_value
        var textDate: TextView = itemView.text_date
        var buttonMore: ImageButton = itemView.button_more
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.income_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd LLLL yyyy")
        viewHolder.textName.text = data[position].name
        viewHolder.textValue.text = context.getString(R.string.value_price, formatter.format(data[position].value))
        if(!data[position].monthly) {
            viewHolder.textDate.text = data[position].date.format(dateFormatter)
        }
        viewHolder.buttonMore.setOnClickListener { view ->
            val menu = PopupMenu(context, view)
            menu.apply {
                inflate(R.menu.menu_income)
                setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.action_income_delete -> {
                            val dialog = AlertDialog.Builder(context)
                            dialog.apply {
                                setTitle(R.string.are_you_sure)
                                setPositiveButton(R.string.delete) { _, _ ->
                                    runBlocking(Dispatchers.IO) {
                                        val count = AppDatabase.getInstance(context).incomeDao().delete(data[position])
                                        Log.d("IncomeListAdapter", "Deleted: $count")
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
                        else -> false
                    }
                }
                show()
            }
        }
    }

    override fun getItemCount() = data.size
}


class IncomeFragment : Fragment() {

    private val data: ArrayList<Income> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_income, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        income_list.layoutManager = LinearLayoutManager(context)
        income_list.setHasFixedSize(true)
        income_list.adapter = IncomeListAdapter(data, context!!)
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            val id = arguments?.getInt("id", -1) ?: -1
            data.clear()
            data.addAll(AppDatabase.getInstance(context!!).projectDao().getIncomes(id))
            income_list?.adapter?.notifyDataSetChanged()
        }
    }
}
