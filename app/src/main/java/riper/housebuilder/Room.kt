package riper.housebuilder

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Rooms",
    foreignKeys = [ForeignKey(entity = Project::class, parentColumns = ["id"], childColumns = ["project_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("project_id")]
)
data class Room(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val floorArea: Double,
    val wallArea: Double,
    val perimeter: Double,
    val project_id: Int
) {
    override fun toString() = name
    fun area() = floorArea * 2 + wallArea
    //fun perimeter() = perimeter
    fun volume() = 0
    //fun wallArea() = wallArea
}

/*
data class Room(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val width: Double = 1.0,
    val length: Double = 1.0,
    val height: Double = 1.0,
    val project_id: Int
) {
    fun area() = width * length
    fun perimeter() = width * 2 + length * 2
    fun volume() = width * length * height
    fun wallArea() = width * height * 2 + length * height * 2
}
*/
