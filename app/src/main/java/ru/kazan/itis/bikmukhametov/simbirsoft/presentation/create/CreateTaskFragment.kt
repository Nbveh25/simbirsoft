package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.kazan.itis.bikmukhametov.simbirsoft.presentation.theme.SimbirSoftTheme

class CreateTaskFragment : Fragment() {
    private val viewModel: CreateTaskViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                SimbirSoftTheme {
                    CreateTaskScreen(
                        viewModel = viewModel,
                        onNavigateBack = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }
}
