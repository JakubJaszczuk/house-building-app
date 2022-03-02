package riper.housebuilder

import androidx.room.*
import java.math.BigDecimal

@Dao
interface SpendingDao {
    @Insert
    suspend fun add(spending: Spending): Long

    @Update
    suspend fun update(spending: Spending): Int

    @Delete
    suspend fun delete(spending: Spending): Int

    @Query("UPDATE Spendings SET done = :doneStatus WHERE id = :spending_id;")
    suspend fun updateDoneStatus(spending_id: Int, doneStatus: Boolean)

    @Query("DELETE FROM Spendings WHERE id = :spending_id;")
    suspend fun delete(spending_id: Int)

    @Query("SELECT * FROM Spendings;")
    suspend fun getAll(): Array<Spending>

    @Query("SELECT Spendings.id, Rooms.name AS roomName, Expenses.name AS expenseName, Spendings.quantity, Spendings.description, done FROM Spendings JOIN Expenses LEFT JOIN Rooms;")
    suspend fun getAllWithNames(): Array<SpendingWithNames>

    data class SpendingWithNames (
        val id: Int,
        val roomName: String?,
        val expenseName: String,
        val quantity: Int,
        var description: String?,
        val done: Boolean
    )

    data class SpendingWithNamesAndCost (
        val id: Int,
        val roomName: String?,
        val expenseName: String,
        val quantity: BigDecimal,
        val description: String?,
        var done: Boolean,
        val cost: BigDecimal
    )

    @Query("" +
            "SELECT Spendings.id, Rooms.name AS roomName, Expenses.name AS expenseName, quantity, description, done, " +
            "CASE " +
            "   WHEN type == 'ITEM_COUNT' OR Room_id IS NULL THEN quantity * unitPrice " +
            "   ELSE CASE type " +
            "       WHEN 'LENGTH' THEN Rooms.perimeter * unitPrice " +
            "       WHEN 'SQUARE_FLOOR' THEN Rooms.floorArea * unitPrice " +
            "       WHEN 'SQUARE_WALL' THEN Rooms.wallArea * unitPrice " +
            "       ELSE -1 " +
            "   END " +
            "END AS cost " +
            "FROM Spendings JOIN Expenses ON Spendings.expense_id == Expenses.id " +
            "LEFT JOIN ROOMS ON Spendings.room_id == Rooms.id;")
    suspend fun getAllWithNamesAndCost(): Array<SpendingWithNamesAndCost>

    @Query("""
            SELECT s.id, s.roomName, s.expenseName, s.quantity, s.quantity * s.unitPrice AS cost, s.description, s.done FROM
            (SELECT Spendings.id, Rooms.name AS roomName, Expenses.name AS expenseName,
            CASE
               WHEN type == 'ITEM_COUNT' OR Spendings.room_id IS NULL THEN quantity
               ELSE CASE type
                   WHEN 'LENGTH' THEN Rooms.perimeter
                   WHEN 'SQUARE_FLOOR' THEN Rooms.floorArea
                   WHEN 'SQUARE_WALL' THEN Rooms.wallArea
                   ELSE -1
               END 
            END AS quantity, unitPrice, Spendings.description, done
            FROM Spendings JOIN Expenses ON Spendings.expense_id == Expenses.id
            LEFT JOIN ROOMS ON Spendings.room_id == Rooms.id
            WHERE :project_id == Spendings.project_id) AS s;""")
    suspend fun getByProjectWithNamesAndCosts(project_id: Int): Array<SpendingWithNamesAndCost>

    @Query("""
            SELECT Spendings.id, Rooms.name AS roomName, Expenses.name AS expenseName, quantity, description, done, quantity * unitPrice AS cost 
            FROM Spendings JOIN Expenses ON Spendings.expense_id == Expenses.id
            LEFT JOIN ROOMS ON Spendings.room_id == Rooms.id
            WHERE :project_id == Spendings.project_id;""")
    suspend fun getByProjectWithNamesAndCost(project_id: Int): Array<SpendingWithNamesAndCost>

    @Query("""
            SELECT SUM(quantity * unitPrice) 
            FROM Spendings JOIN Expenses ON Spendings.expense_id == Expenses.id
            LEFT JOIN ROOMS ON Spendings.room_id == Rooms.id
            WHERE :room_id == Spendings.room_id;
            """)
    suspend fun getCostByRoom(room_id: Int): BigDecimal

    @Query("""
            SELECT quantity * unitPrice 
            FROM Spendings JOIN Expenses ON Spendings.expense_id == Expenses.id
            LEFT JOIN Projects ON Spendings.project_id == Projects.id
            WHERE :project_id == Spendings.project_id;
            """)
    suspend fun getCostByProject(project_id: Int): BigDecimal

}
