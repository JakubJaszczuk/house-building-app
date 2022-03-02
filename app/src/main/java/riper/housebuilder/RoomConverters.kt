package riper.housebuilder

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.time.LocalDate

class RoomConverters {
    @TypeConverter
    fun localDateToString(value: LocalDate): String {
        return value.toString()
    }

    @TypeConverter
    fun stringToLocalDate(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun bigDecimal(value: BigDecimal): String {
        return value.toPlainString()
    }

//    @TypeConverter
//    fun bigDecimalN(value: BigDecimal?): String {
//        return value?.toPlainString() ?: "0"
//    }

    @TypeConverter
    fun bigDecimal(value: String?): BigDecimal {
        return if(value == null) BigDecimal.ZERO else BigDecimal(value)
    }

    @TypeConverter
    fun expenseType(value: ExpenseType): String {
        return value.name
    }

    @TypeConverter
    fun expenseType(value: String): ExpenseType {
        return ExpenseType.valueOf(value)
    }
}
