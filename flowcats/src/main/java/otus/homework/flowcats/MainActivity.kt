package otus.homework.flowcats

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val diContainer = DiContainer()
    private val catsViewModel by viewModels<CatsViewModel> { CatsViewModelFactory(diContainer.repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.activity_main, null) as CatsView
        setContentView(view)

        observeFlow(this, catsViewModel.catsStateFlow, view)
    }

    private fun observeFlow(lifecycleOwner: LifecycleOwner, state: StateFlow<Result>, view: CatsView) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                state.collect { state ->
                    when (state) {
                        is Error -> Toast.makeText(
                            this@MainActivity,
                            state.message, Toast.LENGTH_LONG
                        ).show()

                        is Success -> {
                            view.populate(state.fact)
                        }
                    }
                }
            }
        }
    }
}