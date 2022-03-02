package riper.housebuilder

import androidx.room.*

@Dao
interface IncomeDao {
    @Insert
    suspend fun add(income: Income): Long

    @Update
    suspend fun update(income: Income): Int

    @Delete
    suspend fun delete(income: Income): Int

    @Query("SELECT * FROM Incomes;")
    suspend fun getAll(): Array<Income>
}
