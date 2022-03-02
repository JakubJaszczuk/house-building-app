package riper.housebuilder

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import kotlinx.android.synthetic.main.activity_project_details.*
import kotlinx.android.synthetic.main.content_project_details.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.format.DateTimeFormatter

class ProjectDetailsActivity : AppCompatActivity() {

    private var projectId: Int = -1
    private val dao = AppDatabase.getInstance(this).projectDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        projectId = intent.getIntExtra("id", -1)
        val adapter = ProjectPagerAdapter(this, projectId, supportFragmentManager)
        pager_project.adapter = adapter
        pager_project.offscreenPageLimit = 1
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_project_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                recreate()
                true
            }
            R.id.action_edit -> {
                Log.e("ProjectDetailsActivity", "ID: $projectId")
                EditProjectActivity.start(this, projectId)
                true
            }
            R.id.action_delete -> {
                val dialog = AlertDialog.Builder(this)
                dialog.apply {
                    setTitle(R.string.are_you_sure)
                    setPositiveButton(R.string.delete) { _, _ ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            val count = dao.delete(projectId)
                            Log.d("ProjectDetailsActivity", "Deleted: $count")
                        }
                        Toast.makeText(this@ProjectDetailsActivity, R.string.deleted, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    setNegativeButton(R.string.cancel) { _, _ -> Unit }
                    show()
                }
                true
            }
            R.id.action_add_room -> {
                AddRoomActivity.start(this, projectId)
                true
            }
            R.id.action_add_income -> {
                AddIncomeActivity.start(this, projectId)
                true
            }
            R.id.action_add_spending -> {
                AddSpendingActivity.start(this, projectId)
                true
            }
            R.id.action_add_expense -> {
                ExpenseActivity.start(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun start(context: Context, id: Int): Boolean {
            val starter = Intent(context, ProjectDetailsActivity::class.java)
            starter.putExtra("id", id)
            context.startActivity(starter)
            return true
        }
    }
}
