package riper.housebuilder

import android.content.Context
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate


enum class ExpenseType {
    ITEM_COUNT, LENGTH, SQUARE_FLOOR, SQUARE_WALL;

    fun getLocalizedTypeName(context: Context): String {
        val packageName = context.packageName
        val resId = context.resources.getIdentifier(name, "string", packageName)
        return context.getString(resId)
    }
}

@Entity(tableName = "Expenses")
data class Expense (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val unitPrice: BigDecimal,
    val type: ExpenseType
) {
    override fun toString(): String = name
}

@Entity(tableName = "Spendings",
    foreignKeys = [
        ForeignKey(entity = Project::class, parentColumns = ["id"], childColumns = ["project_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Expense::class, parentColumns = ["id"], childColumns = ["expense_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Room::class, parentColumns = ["id"], childColumns = ["room_id"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("project_id"), Index("expense_id"), Index("room_id")]
)
data class Spending (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val project_id: Int,
    val room_id: Int?,
    val expense_id: Int,
    val quantity: BigDecimal,
    val description: String?,
    var done: Boolean = false
) {
    override fun toString(): String = "$id $project_id $room_id $description"
    fun cost(expense: Expense) = expense.unitPrice * quantity
}

@Entity(tableName = "Incomes",
    foreignKeys = [ForeignKey(entity = Project::class, parentColumns = ["id"], childColumns = ["project_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("project_id")]
)
data class Income (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val value: BigDecimal,
    val monthly: Boolean,
    val date: LocalDate,
    val project_id: Int
) {
    override fun toString(): String = name
}
