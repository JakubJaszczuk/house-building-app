package riper.housebuilder

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
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
import kotlinx.android.synthetic.main.fragment_spending.*
import kotlinx.android.synthetic.main.income_list_item.view.*
import kotlinx.android.synthetic.main.income_list_item.view.button_more
import kotlinx.android.synthetic.main.income_list_item.view.text_name
import kotlinx.android.synthetic.main.spending_list_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat

class SpendingListAdapter(private val data: ArrayList<SpendingDao.SpendingWithNamesAndCost>, private val context: Context) : RecyclerView.Adapter<SpendingListAdapter.ViewHolder>() {

    private val formatterCost = DecimalFormat("0.00")

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textName: TextView = itemView.text_name
        var textRoom: TextView = itemView.text_room
        var textQuantity: TextView = itemView.text_quantity
        var textDescription: TextView = itemView.text_description
        var textPrice: TextView = itemView.text_total_price
        var buttonMore: ImageButton = itemView.button_more
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.spending_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if(data[position].done){
            viewHolder.textName.text = context.getString(R.string.done_what, data[position].expenseName)
            viewHolder.textName.setTextColor(Color.rgb(60, 140, 60))
        }
        else{
            viewHolder.textName.text = data[position].expenseName
            viewHolder.textName.setTextColor(Color.BLACK)
        }
        if(data[position].roomName == null){
            viewHolder.textRoom.text = context.getString(R.string.house)
        }
        else{
            viewHolder.textRoom.text = data[position].roomName
        }
        viewHolder.textQuantity.text = data[position].quantity.toString()
        viewHolder.textDescription.text = data[position].description
        viewHolder.textPrice.text = context.getString(R.string.value_price, formatterCost.format(data[position].cost))
        viewHolder.buttonMore.setOnClickListener { view ->
            val menu = PopupMenu(context, view)
            menu.apply {
                inflate(R.menu.menu_spending)
                setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.action_spending_delete -> {
                            val dialog = AlertDialog.Builder(context)
                            dialog.apply {
                                setTitle(R.string.are_you_sure)
                                setPositiveButton(R.string.delete) { _, _ ->
                                    runBlocking {
                                        val count = AppDatabase.getInstance(context).spendingDao().delete(data[position].id)
                                        Log.d("SpendingListAdapter", "Deleted: $count")
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
                        R.id.action_spending_done -> {
                            data[position].done = !data[position].done
                            notifyItemChanged(position)
                            runBlocking {
                                AppDatabase.getInstance(context).spendingDao().updateDoneStatus(data[position].id, data[position].done)
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


class SpendingFragment : Fragment() {

    private val data: ArrayList<SpendingDao.SpendingWithNamesAndCost> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_spending, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spending_list.layoutManager = LinearLayoutManager(context)
        spending_list.setHasFixedSize(true)
        spending_list.adapter = SpendingListAdapter(data, context!!)
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            val id = arguments?.getInt("id", -1) ?: -1
            data.clear()
            data.addAll(AppDatabase.getInstance(context!!).spendingDao().getByProjectWithNamesAndCost(id))
            spending_list?.adapter?.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("SpendingFragment", "onResume()")
    }
}
