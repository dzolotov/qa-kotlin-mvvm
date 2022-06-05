package tech.dzolotov.counterappmvvm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class CounterViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    lateinit var repository: IDescriptionRepository

    lateinit var viewModel: CounterViewModel

    @Before
    fun setup() {
        repository = mockk()
        viewModel = CounterViewModel(repository)
    }

    @Test
    fun checkIncrement() {
        assertNull(viewModel.getCounter().value)
        viewModel.increment()
        assertEquals(1, viewModel.getCounter().value)
        viewModel.increment()
        assertEquals(2, viewModel.getCounter().value)
    }

    @Test
    fun checkData() = runTest {
        coEvery { repository.getDescription() } returns "Test data from mock"
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        viewModel.getDescription()      //just for initiate loading
        testScheduler.apply {
            runCurrent()
            assert(viewModel.getDescription().value is DescriptionResult.Loading)
            advanceTimeBy(2000)
            runCurrent()
            val description = viewModel.getDescription().value
            assert(description is DescriptionResult.Success)
            assertEquals("Test data from mock", (description as DescriptionResult.Success).text)
        }
        Dispatchers.resetMain()
    }
}