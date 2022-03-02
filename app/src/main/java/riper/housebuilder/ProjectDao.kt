package riper.housebuilder

import androidx.room.*
import java.math.BigDecimal

@Dao
interface ProjectDao {
    @Insert
    suspend fun add(project: Project): Long

    @Update
    suspend fun update(project: Project): Int

    @Delete
    suspend fun delete(project: Project): Int

    @Query("DELETE FROM Projects WHERE id = :project_id")
    suspend fun delete(project_id: Int)

    @Query("SELECT * FROM Projects WHERE id = :id;")
    suspend fun getById(id: Int): Project?

    @Query("SELECT * FROM Projects;")
    suspend fun getAll(): Array<Project>

    @Query("SELECT * FROM Rooms WHERE Rooms.project_id == :project_id;")
    suspend fun getRooms(project_id: Int): Array<Room>

    @Query("SELECT Count(*) FROM Rooms WHERE Rooms.project_id == :project_id;")
    suspend fun getRoomsCount(project_id: Int): Int

    @Query("SELECT * FROM Incomes WHERE Incomes.project_id == :project_id;")
    suspend fun getIncomes(project_id: Int): Array<Income>

    @Query("SELECT * FROM Spendings WHERE Spendings.project_id == :project_id;")
    suspend fun getSpendings(project_id: Int): Array<Spending>

    @Query("""
            SELECT SUM(quantity * unitPrice) 
            FROM Spendings JOIN Expenses ON Spendings.expense_id == Expenses.id
            JOIN Projects ON Spendings.project_id == Projects.id
            WHERE :project_id == Spendings.project_id;
            """)
    suspend fun getCostByProject(project_id: Int): BigDecimal?

    @Query("""
            SELECT SUM(CASE Spendings.done WHEN 1 THEN 0 ELSE quantity * unitPrice END) AS cost
            FROM Spendings JOIN Expenses ON Spendings.expense_id == Expenses.id
            JOIN Projects ON Spendings.project_id == Projects.id
            WHERE :project_id == Spendings.project_id;
            """)
    suspend fun getRemainingCostByProject(project_id: Int): BigDecimal
}
