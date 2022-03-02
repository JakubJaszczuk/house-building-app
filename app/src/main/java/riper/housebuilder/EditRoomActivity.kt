package riper.housebuilder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import kotlinx.android.synthetic.main.activity_edit_room.*
import kotlinx.android.synthetic.main.content_edit_room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class EditRoomActivity : AppCompatActivity() {

    private lateinit var currentRoom: Room
    private val dao = AppDatabase.getInstance(this).roomDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val future = lifecycleScope.async(Dispatchers.IO) {
            dao.getById(intent.getIntExtra("id", 0))
        }

        setContentView(R.layout.activity_edit_room)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lifecycleScope.launch {
            val tmp = future.await()
            if(tmp == null) {
                finish()
            }
            else {
                currentRoom = tmp
                val formatter = DecimalFormat("0.000")
                input_name.setText(currentRoom.name)
                input_width.setText(formatter.format(currentRoom.floorArea))
                input_length.setText(formatter.format(currentRoom.wallArea))
                input_height.setText(formatter.format(currentRoom.perimeter))
            }
        }

        button_cancel.setOnClickListener {
            finish()
        }

        button_accept.setOnClickListener {
            val room = Room(currentRoom.id, input_name.text.toString(),
                input_width.text.toString().replace(',', '.').toDoubleOrNull() ?: -1.0,
                input_length.text.toString().replace(',', '.').toDoubleOrNull() ?: -1.0,
                input_height.text.toString().replace(',', '.').toDoubleOrNull() ?: -1.0,
                currentRoom.project_id)
            val validator = RoomValidator(room)
            if(validator.validate()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    dao.updateRoomWithSpendings(room)
                }
                Toast.makeText(it.context, R.string.edited, Toast.LENGTH_SHORT).show()
                finish()
            }
            else {
                Toast.makeText(it.context, R.string.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun start(context: Context, id: Int): Boolean {
            val starter = Intent(context, EditRoomActivity::class.java)
            starter.putExtra("id", id)
            context.startActivity(starter)
            return true
        }
    }
}
