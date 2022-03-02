package riper.housebuilder

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.lang.RuntimeException

class ProjectPagerAdapter(private val context: Context, private val projectId: Int, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = 4

    override fun getItem(position: Int): Fragment {
        val args = Bundle()
        args.putInt("id", projectId)
        return when(position) {
            0 -> {
                val fragment = ProjectDetailsFragment()
                fragment.arguments = args
                fragment
            }
            1 -> {
                val fragment = RoomsFragment()
                fragment.arguments = args
                fragment
            }
            2-> {
                val fragment = IncomeFragment()
                fragment.arguments = args
                fragment
            }
            3-> {
                val fragment = SpendingFragment()
                fragment.arguments = args
                fragment
            }
            else -> throw RuntimeException("Too many tabs")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> context.getString(R.string.project)
            1 -> context.getString(R.string.rooms_hint)
            2 -> context.getString(R.string.income)
            3 -> context.getString(R.string.spendings)
            else -> super.getPageTitle(position)
        }
    }
}
