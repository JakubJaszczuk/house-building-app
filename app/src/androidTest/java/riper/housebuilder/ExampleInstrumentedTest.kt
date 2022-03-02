package riper.housebuilder

import android.widget.DatePicker
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Matchers
import org.hamcrest.core.IsNot.not
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AddIncomeInstrumentedTest {

    @get:Rule
    val activityRule = ActivityTestRule(AddIncomeActivity::class.java)

    @Test
    fun simple_input_good() {
        onView(withId(R.id.input_name)).perform(typeText("Przychody")).
            check(matches(withText("Przychody")))
    }

    // Wyłacz wpisywanie i sprawdź stan pola tekstowego
    @Test
    fun disable_date_input() {
        onView(withId(R.id.switch_monthly)).perform(click())
        onView(withId(R.id.input_date_begin)).check(matches(not(isEnabled())))
    }
}

@RunWith(AndroidJUnit4::class)
class AddProjectInstrumentedTest {

    @get:Rule
    val activityRule = ActivityTestRule(AddProjectActivity::class.java)

    @Test
    fun add_new_good() {
        onView(withId(R.id.input_name)).perform(typeText("Test dodania projektu"))
        onView(withId(R.id.input_description)).perform(typeText("Testy :D"))
        onView(withId(R.id.input_date_end)).perform(closeSoftKeyboard(), scrollTo(), click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(PickerActions.setDate(2022, 4, 12))
        onView(withText("OK")).perform(click());
        onView(withId(R.id.button_accept)).perform(click())
        assertTrue(activityRule.activity.isFinishing)
    }

    @Test
    fun add_new_error() {
        onView(withId(R.id.input_description)).perform(typeText("Testy :D"))
        onView(withId(R.id.button_accept)).perform(click())
        onView(withText(R.string.add_project_fail)).inRoot(withDecorView(not
            (activityRule.activity.window.decorView))).check(matches(isDisplayed()))
    }
}
