package riper.housebuilder

import java.math.BigDecimal

interface Validator {
    fun validate(): Boolean
}

class ProjectValidator(private val project: Project) : Validator {
    override fun validate(): Boolean {
        return project.name.isNotEmpty() && project.endDate > project.beginDate
    }
}

class ProjectValidator2(private val project: Project) {
    fun validate(): ValidationResult {
        return when {
            project.name.isEmpty() -> {
                ValidationResult(true, R.string.validation_name_error)
            }
            project.endDate < project.beginDate -> {
                ValidationResult(true, R.string.validation_date_error)
            }
            else -> ValidationResult(false, R.string.edited)
        }
    }
}

class RoomValidator(private val room: Room) : Validator {
    override fun validate(): Boolean {
        return room.name.isNotEmpty() && room.floorArea > 0 && room.wallArea > 0 && room.perimeter > 0 && room.project_id > 0
    }
}

class IncomeValidator(private val income: Income) : Validator {
    override fun validate(): Boolean {
        return income.name.isNotEmpty() && income.value > BigDecimal.ZERO
    }
}

class SpendingValidator(private val projId: Int, private val expenseId: Int?, private val quantity: BigDecimal?) : Validator {
    override fun validate(): Boolean {
        return if(expenseId == null || quantity == null) false
        else projId > 0 && expenseId > 0 && quantity > BigDecimal.ZERO
    }
}

class ExpenseValidator(private val name: String?, private val unitPrice: BigDecimal?) : Validator {
    override fun validate(): Boolean {
        return if(name.isNullOrBlank() || unitPrice== null) false
        else unitPrice > BigDecimal.ZERO
    }
}

data class ValidationResult(val isError: Boolean, val messageResId: Int)
