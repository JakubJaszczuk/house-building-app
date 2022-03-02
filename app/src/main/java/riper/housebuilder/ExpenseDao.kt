package riper.housebuilder

import androidx.room.*

@Dao
interface ExpenseDao {
    @Insert
    suspend fun add(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense): Int

    @Delete
    suspend fun delete(expense: Expense): Int

    @Query("SELECT * FROM Expenses;")
    suspend fun getAll(): Array<Expense>

    @Query("SELECT * FROM Expenses WHERE id = :id;")
    suspend fun getById(id: Int): Expense?

    @Query("DELETE FROM Expenses WHERE id = :expense_id;")
    suspend fun delete(expense_id: Int)
}
