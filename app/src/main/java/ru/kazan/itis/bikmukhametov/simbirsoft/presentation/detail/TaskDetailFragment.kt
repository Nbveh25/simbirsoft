package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.kazan.itis.bikmukhametov.simbirsoft.R
import ru.kazan.itis.bikmukhametov.simbirsoft.databinding.FragmentTaskDetailBinding

class TaskDetailFragment : Fragment() {
    private val viewModel: TaskDetailViewModel by viewModel()
    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    private var _binding: FragmentTaskDetailBinding? = null
    private val binding: FragmentTaskDetailBinding
        get() = _binding ?: error("Binding is only valid between onCreateView and onDestroyView")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val taskId = arguments?.getLong(ARG_TASK_ID) ?: INVALID_TASK_ID
        if (taskId == INVALID_TASK_ID) {
            findNavController().popBackStack()
            return
        }

        viewModel.onIntent(TaskDetailIntent.Load(taskId))

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        if (!state.errorMessage.isNullOrBlank()) {
                            Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_SHORT).show()
                        }
                        val task = state.task ?: return@collect
                        binding.detailTitleText.text = task.name
                        val zoneId = ZoneId.systemDefault()
                        val start = Instant.ofEpochMilli(task.dateStart).atZone(zoneId).format(formatter)
                        val end = Instant.ofEpochMilli(task.dateFinish).atZone(zoneId).format(formatter)
                        binding.detailDateText.text = getString(R.string.task_datetime_range, start, end)
                        binding.detailDescriptionText.text = task.description
                    }
                }
                launch {
                    viewModel.effect.collect { effect ->
                        when (effect) {
                            TaskDetailEffect.CloseScreen -> findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        private const val ARG_TASK_ID = "taskId"
        private const val INVALID_TASK_ID = -1L
    }

}
