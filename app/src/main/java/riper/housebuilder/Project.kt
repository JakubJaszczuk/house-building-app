package riper.housebuilder

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

@Entity(tableName = "Projects")
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val description: String = "",
    val beginDate: LocalDate = LocalDate.now(),
    //val beginDate: Date = Date(),
    val endDate: LocalDate = LocalDate.now()
) {
    override fun toString() = name

    fun sumIncomeToProjectEnd(incomes: Array<Income>): BigDecimal {
        val monthly = incomes.fold(BigDecimal.ZERO) { acc, income -> acc + if (income.monthly) income.value else BigDecimal.ZERO }
        val once = incomes.fold(BigDecimal.ZERO) { acc, income -> acc + if (income.monthly || income.date > endDate) BigDecimal.ZERO else income.value }
        val months = ChronoUnit.MONTHS.between(beginDate, endDate)
        return monthly * BigDecimal(months) + once
    }

    fun isEnoughMoney(cost: BigDecimal, money: BigDecimal): Boolean {
        return money > cost
    }

    fun sumIncomeToOverflow(cost: BigDecimal, incomes: Array<Income>): LocalDate {
        val now = LocalDate.now()
        var currentMoney = BigDecimal.ZERO
        var months = 0L
        val monthly = incomes.fold(BigDecimal.ZERO) { acc, income -> acc + if (income.monthly) income.value else BigDecimal.ZERO }
        while (currentMoney <= cost && months < 12*20) {
            months += 1
            val once = incomes.fold(BigDecimal.ZERO) { acc, income -> acc + if (!income.monthly && income.date < now.plusMonths(months)) income.value else BigDecimal.ZERO }
            currentMoney = monthly * BigDecimal(months) + once
        }
        return now.plusMonths(months)
    }
}
