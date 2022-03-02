package riper.housebuilder

import android.util.Log
import com.nhaarman.mockitokotlin2.doAnswer
import java.time.LocalDate
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.mockito.ArgumentMatchers.any


class HouseBuilderTest {

    private val mainThreadSurrogate = newSingleThreadContext("Main coroutine thread mock")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun projectValidator_project_good() {
        val validator = ProjectValidator2(Project(0, "Domek mój", "Moja własna działka"))
        val result = validator.validate()
        assertFalse(result.isError)
    }

    @Test
    fun projectValidator_project_empty_name() {
        val validator = ProjectValidator2(Project(0, ""))
        val result = validator.validate()
        assertTrue(result.isError)
    }

    @Test
    fun projectValidator_project_begin_greater_than_end() {
        val validator = ProjectValidator2(Project(0, "Domek mój", "Moja własna działka", LocalDate.of(2022, 3, 11)))
        val result = validator.validate()
        assertTrue(result.isError)
    }

    @Test
    fun projectValidator_project_all_wrong() {
        val validator = ProjectValidator2(Project(0, "", "", LocalDate.of(2022, 3, 11)))
        val result = validator.validate()
        assertTrue(result.isError)
    }

    @Test
    fun room_total_area_good() {
        assertEquals(Room(1, "Kuchnia", 20.0, 54.0, 18.0, 1).area(), 94.0, 0.0001)
    }

    @Test
    fun room_total_area() {
        assertNotEquals(Room(1, "Kuchnia", 20.0, 54.0, 18.0, 1).area(), 4.0, 0.0001)
    }

    @Test
    fun `Room total area`() {
        assertNotEquals(Room(1, "Kuchnia", 20.0, 54.0, 18.0, 1).area(), 4.0, 0.0001)
    }






    /*
    @Test
    fun `Test EditProject - get data - goods`() {
        val daoMock = mock<ProjectDao> {
            on {runBlocking{ getById(any(Int::class.java)) }} doReturn Project(0, "Dom 1")
        }
        val view = object : EditProjectContract.View {
            var names = "brak danych"
            override fun show(project: Project) {
                print("ONMY")
                names = project.name
            }
        }
        val presenter = EditProjectPresenter(view, daoMock)
        presenter.getData(0)
        assertEquals("Dom 1", view.names)
    }
    */
}
