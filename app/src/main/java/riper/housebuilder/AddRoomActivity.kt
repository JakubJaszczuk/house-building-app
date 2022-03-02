package riper.housebuilder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import kotlinx.android.synthetic.main.activity_add_room.*
import kotlinx.android.synthetic.main.content_add_room.*
import kotlinx.coroutines.*

class AddRoomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_room)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        button_cancel.setOnClickListener {
            finish()
        }

        button_accept.setOnClickListener {
            val room = Room(0, input_name.text.toString(),
                input_width.text.toString().replace(',', '.').toDoubleOrNull() ?: -1.0,
                input_length.text.toString().replace(',', '.').toDoubleOrNull() ?: -1.0,
                input_height.text.toString().replace(',', '.').toDoubleOrNull() ?: -1.0,
                intent.getIntExtra("id", -1))
            val validator = RoomValidator(room)
            if(validator.validate()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    AppDatabase.getInstance(this@AddRoomActivity).roomDao().add(room)
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
            val starter = Intent(context, AddRoomActivity::class.java)
            starter.putExtra("id", id)
            context.startActivity(starter)
            return true
        }
    }
}
