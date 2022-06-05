package tech.dzolotov.counterappmvvm

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.counterapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject


@AndroidEntryPoint
class CounterActivity : AppCompatActivity() {

    private val viewModel: CounterViewModel by viewModels()

    @Inject
    @CoroutineDispatcherOverride
    lateinit var overrideDispatcher: CoroutineDispatcher

    private fun observe() {
        viewModel.getCounter().observe(this) {
            findViewById<TextView>(R.id.counter).text =
                it?.let { counter -> "Counter: $counter" } ?: "Click below for increment"
        }
        viewModel.getDescription().observe(this) {
            if (it != null) {
                val text = when (it) {
                    is DescriptionResult.Error -> "Error is occured"
                    is DescriptionResult.Loading -> "Loading"
                    is DescriptionResult.Success -> it.text
                }
                findViewById<TextView>(R.id.description).text = text
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (overrideDispatcher != Dispatchers.Main) {
            viewModel.overrideScope(CoroutineScope(overrideDispatcher))
        }
        observe()
        findViewById<Button>(R.id.increase_button).setOnClickListener {
            viewModel.increment()
        }
    }
}