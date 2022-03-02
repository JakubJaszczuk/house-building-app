package riper.housebuilder

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Project::class, Room::class, Income::class, Spending::class, Expense::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun projectDao(): ProjectDao
    abstract fun roomDao(): RoomDao
    abstract fun incomeDao(): IncomeDao
    abstract fun spendingDao(): SpendingDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_data.db").build()
                INSTANCE = instance
                return instance
            }
        }
    }

}
