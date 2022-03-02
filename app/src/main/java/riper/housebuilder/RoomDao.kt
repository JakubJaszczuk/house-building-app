package riper.housebuilder

import androidx.room.*
import java.math.BigDecimal

@Dao
interface RoomDao {
    @Insert
    suspend fun add(room: Room): Long

    @Update
    suspend fun update(room: Room): Int

    @Delete
    suspend fun delete(room: Room): Int

    @Query("SELECT * FROM Rooms;")
    suspend fun getAll(): Array<Room>

    @Query("SELECT * FROM Rooms WHERE id = :id;")
    suspend fun getById(id: Int): Room?

    @Query("DELETE FROM Rooms WHERE id = :id;")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM Spendings WHERE Spendings.room_id == :roomId;")
    suspend fun getRelatedSpendings(roomId: Int): Array<Spending>

    data class RoomWithCost (
        val id: Int,
        val name: String,
        val floorArea: Double,
        val wallArea: Double,
        val perimeter: Double,
        val project_id: Int,
        val cost: BigDecimal
    )
    @Query("""
        SELECT Rooms.id, Rooms.name, floorArea, wallArea, perimeter, Rooms.project_id, SUM(quantity * unitPrice) AS cost
        FROM Rooms LEFT JOIN Spendings ON Spendings.room_id == Rooms.id
		LEFT JOIN Expenses ON Spendings.expense_id == Expenses.id
        WHERE Rooms.project_id == :project_id
        GROUP BY Rooms.id, Rooms.name, floorArea, wallArea, perimeter, Rooms.project_id
    """)
    suspend fun getRoomsWithCost(project_id: Int): Array<RoomWithCost>
    /*
    @Query("""
        UPDATE Spendings SET quantity = (
            SELECT CASE Expenses.type
                WHEN 'ITEM_COUNT' THEN Spendings.quantity
                WHEN 'LENGTH' THEN :perimeter
                WHEN 'SQUARE_FLOOR' THEN :floorArea
                WHEN 'SQUARE_WALL' THEN :wallArea
                ELSE -1
            END
            FROM Spendings JOIN Expenses ON Spendings.expense_id == Expenses.id
        )
        WHERE Spendings.room_id == :roomId;
    """)
    suspend fun updateRelatedSpendings(roomId: Int, perimeter: Double, floorArea: Double, wallArea: Double)
     */

    @Query("""
        UPDATE Spendings SET quantity = (
            SELECT CASE Expenses.type
                WHEN 'ITEM_COUNT' THEN Spendings.quantity
                WHEN 'LENGTH' THEN :perimeter
                WHEN 'SQUARE_FLOOR' THEN :floorArea
                WHEN 'SQUARE_WALL' THEN :wallArea
                ELSE -1
            END
            FROM Spendings JOIN Expenses ON Spendings.expense_id == Expenses.id
            WHERE Spendings.id == :spendingId
        )
        WHERE Spendings.id == :spendingId;
    """)
    suspend fun updateRelatedSpending(spendingId: Int, perimeter: Double, floorArea: Double, wallArea: Double)

    @Transaction
    suspend fun updateRoomWithSpendings(room: Room) {
        update(room)
        val spendings = getRelatedSpendings(room.id)
        for(s in spendings) {
            updateRelatedSpending(s.id, room.perimeter, room.floorArea, room.wallArea)
        }
    }
}
