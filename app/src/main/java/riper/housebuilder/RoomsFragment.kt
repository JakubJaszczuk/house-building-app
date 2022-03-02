package riper.housebuilder

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_rooms.*
import kotlinx.android.synthetic.main.room_list_item.view.*
import kotlinx.coroutines.*
import java.text.DecimalFormat
import kotlin.coroutines.CoroutineContext

class RoomsListAdapter(private val presenter: RoomsFragmentContract.Presenter, private val context: Context) : RecyclerView.Adapter<RoomsListAdapter.ViewHolder>() {

    //private val formatter = DecimalFormat("0.000")
    //private val formatterCost = DecimalFormat("0.00")

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var textName: TextView = itemView.text_name
        private var textArea: TextView = itemView.text_area
        private var textWalls: TextView = itemView.text_walls
        private var textPerimeter: TextView = itemView.text_perimeter
        private var textCost: TextView = itemView.text_cost
        var buttonMore: ImageButton = itemView.button_more

        fun show(data: RoomDao.RoomWithCost, context: Context) {
            val formatter = DecimalFormat("0.000")
            val formatterCost = DecimalFormat("0.00")
            textName.text = data.name
            textArea.text = context.getString(R.string.value_square_meter, formatter.format(data.floorArea))
            textWalls.text = context.getString(R.string.value_square_meter, formatter.format(data.wallArea))
            textPerimeter.text = context.getString(R.string.value_square_meter, formatter.format(data.perimeter))
            textCost.text = context.getString(R.string.all_cost, formatterCost.format(data.cost))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.room_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val data = presenter.getDataAtPosition(position)
        viewHolder.show(data, context)
        viewHolder.buttonMore.setOnClickListener {view ->
            val menu = PopupMenu(context, view)
            menu.inflate(R.menu.menu_room)
            menu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_room_edit -> {
                        EditRoomActivity.start(context, data.id)
                        true
                    }
                    R.id.action_room_delete -> {
                        val dialog = AlertDialog.Builder(context)
                        dialog.apply {
                            setTitle(R.string.are_you_sure)
                            setPositiveButton(R.string.delete) { _, _ ->
                                presenter.deleteRoom(data.id)
                                presenter.getDataSet().removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, itemCount)
                            }
                            setNegativeButton(R.string.cancel) { _, _ -> Unit }
                            show()
                        }
                        true
                    }
                    else -> false
                }
            }
            menu.show()
        }
    }

    override fun getItemCount() = presenter.getDataSetSize()
}


class RoomsFragment : Fragment(), RoomsFragmentContract.View {

    private lateinit var dao: RoomDao
    private lateinit var presenter: RoomsFragmentContract.Presenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dao = AppDatabase.getInstance(context).roomDao()
        presenter = RoomsFragmentPresenter(this, dao)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rooms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rooms_list.layoutManager = LinearLayoutManager(context)
        rooms_list.setHasFixedSize(true)
        rooms_list.adapter = RoomsListAdapter(presenter, context!!)
    }

    override fun onStart() {
        super.onStart()
        presenter.getData(arguments?.getInt("id", -1) ?: -1)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }

    override fun show() {
        rooms_list?.adapter?.notifyDataSetChanged()
    }
}

interface RoomsFragmentContract {
    interface Presenter {
        fun detach()
        fun getData(dataId: Int)
        fun getDataSet(): ArrayList<RoomDao.RoomWithCost>
        fun getDataAtPosition(position: Int): RoomDao.RoomWithCost
        fun getDataSetSize(): Int
        fun deleteRoom(dataId: Int)
    }

    interface View {
        fun show()
    }
}

class RoomsFragmentPresenter(private var view: RoomsFragmentContract.View?, private val dao: RoomDao) : CoroutineScope, RoomsFragmentContract.Presenter {

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO
    val data: ArrayList<RoomDao.RoomWithCost> = ArrayList()

    override fun detach() {
        job.cancel()
        view = null
    }

    override fun getData(dataId: Int) {
        launch(Dispatchers.Main) {
            data.clear()
            data.addAll(dao.getRoomsWithCost(dataId))
            view?.show()
        }
    }

    override fun getDataSet(): ArrayList<RoomDao.RoomWithCost> {
        return data
    }

    override fun getDataAtPosition(position: Int): RoomDao.RoomWithCost {
        return data[position]
    }

    override fun getDataSetSize(): Int {
        return data.size
    }

    override fun deleteRoom(dataId: Int) {
        launch(Dispatchers.IO) {
            dao.delete(dataId)
        }
    }
}
