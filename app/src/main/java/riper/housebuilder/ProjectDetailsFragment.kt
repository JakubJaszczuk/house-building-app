package riper.housebuilder

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.fragment_project_details.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.text.DateFormat.getDateInstance
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ProjectDetailsFragment : Fragment() {

    private lateinit var currentProject: Project
    private var roomsCount: Int = 0
    private var cost: BigDecimal = BigDecimal.ZERO
    private var remainingCost: BigDecimal = BigDecimal.ZERO
    private var incomesToEnd: BigDecimal = BigDecimal.ZERO
    private var estimatedEndDate: LocalDate = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_project_details, container, false)
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(context!!).projectDao()
            val id = arguments?.getInt("id", -1) ?: -1
            val tmp = dao.getById(id)
            if(tmp == null) {
                Log.e("ProjectDetailsFragment", "!!!")
            }
            else {
                currentProject = tmp
                roomsCount = dao.getRoomsCount(id)
                cost = dao.getCostByProject(id) ?: BigDecimal.ZERO
                remainingCost = dao.getRemainingCostByProject(id)
                val incomes = dao.getIncomes(id)
                incomesToEnd = currentProject.sumIncomeToProjectEnd(incomes)
                estimatedEndDate = currentProject.sumIncomeToOverflow(remainingCost, incomes)
                withContext(Dispatchers.Main) {
                    updateUi()
                }
            }
        }
    }

    private fun updateUi() {
        val formatter = DecimalFormat("0.00")
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd LLLL yyyy")
        val oldDateFormatter: SimpleDateFormat = SimpleDateFormat("dd MMMM yyyy")
        val inny_formatter = getDateInstance()
        project_name?.setText(currentProject.name) ?: Log.e("ProjectDetailsFragment", "!!!")
        project_description?.setText(currentProject.description)
        project_date_begin?.setText(currentProject.beginDate.format(dateFormatter))
        project_date_end?.setText(currentProject.endDate.format(dateFormatter))
        project_rooms?.setText(roomsCount.toString())
        project_cost?.setText(formatter.format(cost))
        project_remaining_cost?.setText(formatter.format(remainingCost))
        project_income_to_end?.setText(formatter.format(incomesToEnd))
        project_estimated_end?.setText(estimatedEndDate.format(dateFormatter))
    }
    /*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e("ProjectDetailsFragment", "onViewCreated()")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onPause() {
        Log.e("ProjectDetailsFragment", "onPause()")
        super.onPause()
    }

    override fun onStop() {
        Log.e("ProjectDetailsFragment", "onStop()")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.e("ProjectDetailsFragment", "onDestroyView()")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.e("ProjectDetailsFragment", "onDestroy()")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.e("ProjectDetailsFragment", "onDetach()")
        super.onDetach()
    }
     */
}
