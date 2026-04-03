package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.kazan.itis.bikmukhametov.simbirsoft.R
import ru.kazan.itis.bikmukhametov.simbirsoft.databinding.FragmentTaskListBinding

class TaskListFragment : Fragment() {

    private val viewModel: TaskListViewModel by viewModel()

    private lateinit var adapter: HourSlotAdapter
    
    private var _binding: FragmentTaskListBinding? = null
    private val binding: FragmentTaskListBinding
        get() = _binding ?: error("Binding is only valid between onCreateView and onDestroyView")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = HourSlotAdapter(
            onTaskClick = { taskId ->
                viewModel.onIntent(TaskListIntent.TaskClicked(taskId))
            }
        )
        binding.slotsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.slotsRecycler.adapter = adapter

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selected = java.util.Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            viewModel.onIntent(TaskListIntent.DateSelected(selected.timeInMillis))
        }

        binding.addTaskButton.setOnClickListener {
            viewModel.onIntent(TaskListIntent.AddTaskClicked)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        adapter.submitItems(state.slots)
                        if (!state.errorMessage.isNullOrBlank()) {
                            Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
                launch {
                    viewModel.effect.collect { effect ->
                        when (effect) {
                            is TaskListEffect.NavigateToCreateTask -> {
                                findNavController().navigate(R.id.action_taskListFragment_to_createTaskFragment)
                            }
                            is TaskListEffect.NavigateToTaskDetail -> {
                                findNavController().navigate(
                                    R.id.action_taskListFragment_to_taskDetailFragment,
                                    Bundle().apply { putLong("taskId", effect.taskId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onIntent(TaskListIntent.Refresh)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
