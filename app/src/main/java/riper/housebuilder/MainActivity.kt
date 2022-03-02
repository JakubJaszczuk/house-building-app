package riper.housebuilder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.*
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.coroutines.coroutineContext

class MainActivity : AppCompatActivity() {

    private val projects: ArrayList<Project> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            AddProjectActivity.start(this)
        }

        button_reset.setOnClickListener {
            runBlocking(Dispatchers.IO) {
                val db = AppDatabase.getInstance(applicationContext)
                db.clearAllTables()
                // Projects
                db.projectDao().add(Project(0, "Dom 2 piętra", "Projekt indywidualny"))
                db.projectDao().add(Project(0, "Dom na Rogalskiego", "Wszystko bedzie dobrze"))
                db.projectDao().add(Project(0, "Projekt dla Magdy"))
                db.projectDao().add(Project(0, "Altanka na działkę", "Dla Stefana"))
                db.projectDao().add(Project(0, "Projekt 5", "Testowy projekt, usunę go później"))
                val projs = db.projectDao().getAll()
                projects.clear()
                projects.addAll(projs)
                // Rooms
                db.roomDao().add(Room(0, "Kuchnia", 14.0, 30.0, 10.0, project_id = projs.first().id))
                db.roomDao().add(Room(0, "Salon", 40.0, 78.0, 26.0, projs.first().id))
                db.roomDao().add(Room(0, "Schowek", 2.0, 12.0, 6.0, projs.first().id))
                db.roomDao().add(Room(0, "Łazienka", 8.0, 12.0, 6.0, projs.first().id))
                db.roomDao().add(Room(0, "Sypialnia", 10.5, 12.0, 8.4, projs.first().id))
                val rooms = db.roomDao().getAll()
                // Income
                db.incomeDao().add(Income(0, "Pod choinkę", BigDecimal(23000), false, LocalDate.now().plusMonths(7), projs.first().id))
                db.incomeDao().add(Income(0, "Odsetki", BigDecimal(200), true, LocalDate.now(), projs.first().id))
                db.incomeDao().add(Income(0, "Moje", BigDecimal(5200), true, LocalDate.now(), projs.first().id))
                db.incomeDao().add(Income(0, "Karolina 15k", BigDecimal(12000), true, LocalDate.now(), projs.first().id))
                db.incomeDao().add(Income(0, "Nagroda za zlecenie", BigDecimal(3000), false, LocalDate.now().plusMonths(2), projs.first().id))
                // Expense
                db.expenseDao().add(Expense(0, "Fundament JANUSZBUD", BigDecimal(30.5), ExpenseType.SQUARE_FLOOR))
                db.expenseDao().add(Expense(0, "Robocizna", BigDecimal(3630.5), ExpenseType.SQUARE_FLOOR))
                db.expenseDao().add(Expense(0, "Świecznik Adrian", BigDecimal(7.99), ExpenseType.ITEM_COUNT))
                db.expenseDao().add(Expense(0, "Farba FARBEX", BigDecimal(21), ExpenseType.SQUARE_WALL))
                db.expenseDao().add(Expense(0, "Panel dąb", BigDecimal(55), ExpenseType.SQUARE_FLOOR))
                db.expenseDao().add(Expense(0, "Boazeria Bożena", BigDecimal(25), ExpenseType.SQUARE_WALL))
                db.expenseDao().add(Expense(0, "Projekt domu", BigDecimal(5700), ExpenseType.ITEM_COUNT))
                db.expenseDao().add(Expense(0, "Listwa podłogowa", BigDecimal(13), ExpenseType.LENGTH))
                val expenses = db.expenseDao().getAll()
                // Spending
                db.spendingDao().add(Spending(0, projs.first().id, null, expenses[0].id, BigDecimal(100), "Fundamenty", false))
                db.spendingDao().add(Spending(0, projs.first().id, rooms[0].id, expenses[2].id, BigDecimal(3), "Świeczniki dla Stacha", false))
                db.spendingDao().add(Spending(0, projs.first().id, rooms[4].id, expenses[2].id, BigDecimal(1), "Świeczniki do sypialni", true))
                db.spendingDao().add(Spending(0, projs.first().id, rooms[0].id, expenses[3].id, BigDecimal(20), null, true))
                db.spendingDao().add(Spending(0, projs.first().id, rooms[1].id, expenses[4].id, BigDecimal(10), "Panel dąb fajny jest ale można by dać panel ze sosny i bedzie taniej :D", false))
                db.spendingDao().add(Spending(0, projs.first().id, null, expenses[6].id, BigDecimal(1), "Projekt od architekta", false))
                db.spendingDao().add(Spending(0, projs.first().id, rooms[1].id, expenses[5].id, BigDecimal(1), "Boazerunie", false))

            }
            projects_list.adapter?.notifyDataSetChanged()
        }

        projects_list.layoutManager = LinearLayoutManager(this)
        projects_list.setHasFixedSize(true)
        projects_list.adapter = ProjectsListAdapter(projects, this)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            projects.clear()
            projects.addAll(AppDatabase.getInstance(applicationContext).projectDao().getAll())
            Log.e("MainActivity", "${projects.size}")
            projects_list.adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                AddProjectActivity.start(this)
                return true
            }
            R.id.action_about -> {
                AboutActivity.start(this)
                return true
            }
            R.id.action_add_expense -> {
                ExpenseActivity.start(this)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
