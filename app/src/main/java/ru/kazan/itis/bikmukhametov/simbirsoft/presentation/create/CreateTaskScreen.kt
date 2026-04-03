package ru.kazan.itis.bikmukhametov.simbirsoft.presentation.create

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import ru.kazan.itis.bikmukhametov.simbirsoft.R

private const val DEFAULT_END_OFFSET_MS = 3_600_000L

@Composable
fun CreateTaskScreen(
    viewModel: CreateTaskViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var startAtMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var endAtMillis by remember {
        mutableLongStateOf(System.currentTimeMillis() + DEFAULT_END_OFFSET_MS)
    }

    val dateTimeFormatter = remember {
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    }

    CreateTaskEffectHandlers(
        viewModel = viewModel,
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        onNavigateBack = onNavigateBack
    )

    CreateTaskScaffold(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        context = context,
        viewModel = viewModel,
        title = title,
        onTitleChange = { title = it },
        description = description,
        onDescriptionChange = { description = it },
        startAtMillis = startAtMillis,
        onStartMillisChange = { startAtMillis = it },
        endAtMillis = endAtMillis,
        onEndMillisChange = { endAtMillis = it },
        dateTimeFormatter = dateTimeFormatter,
        isSaving = uiState.isSaving
    )
}

@Composable
private fun CreateTaskScaffold(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    context: Context,
    viewModel: CreateTaskViewModel,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    startAtMillis: Long,
    onStartMillisChange: (Long) -> Unit,
    endAtMillis: Long,
    onEndMillisChange: (Long) -> Unit,
    dateTimeFormatter: SimpleDateFormat,
    isSaving: Boolean
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            CreateTaskFormFields(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                title = title,
                onTitleChange = onTitleChange,
                description = description,
                onDescriptionChange = onDescriptionChange,
                startAtMillis = startAtMillis,
                onStartMillisChange = onStartMillisChange,
                endAtMillis = endAtMillis,
                onEndMillisChange = onEndMillisChange,
                dateTimeFormatter = dateTimeFormatter,
                isSaving = isSaving,
                onSaveClick = {
                    if (title.isBlank()) {
                        Toast.makeText(
                            context,
                            R.string.error_empty_title,
                            Toast.LENGTH_SHORT
                        ).show()
                        return@CreateTaskFormFields
                    }
                    if (endAtMillis <= startAtMillis) {
                        Toast.makeText(
                            context,
                            R.string.error_invalid_time_range,
                            Toast.LENGTH_SHORT
                        ).show()
                        return@CreateTaskFormFields
                    }
                    viewModel.onIntent(
                        CreateTaskIntent.Submit(
                            name = title,
                            description = description,
                            startAtMillis = startAtMillis,
                            endAtMillis = endAtMillis
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun CreateTaskEffectHandlers(
    viewModel: CreateTaskViewModel,
    snackbarHostState: SnackbarHostState,
    uiState: CreateTaskUiState,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CreateTaskEffect.TaskCreated -> {
                    Toast.makeText(context, R.string.task_created, Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                }
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val msg = uiState.errorMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
        }
    }
}

@Composable
private fun CreateTaskFormFields(
    modifier: Modifier,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    startAtMillis: Long,
    onStartMillisChange: (Long) -> Unit,
    endAtMillis: Long,
    onEndMillisChange: (Long) -> Unit,
    dateTimeFormatter: SimpleDateFormat,
    isSaving: Boolean,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CreateSectionHeader(text = stringResource(R.string.create_section_main))
        CreateMainInfoCard(
            title = title,
            onTitleChange = onTitleChange,
            description = description,
            onDescriptionChange = onDescriptionChange
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        CreateSectionHeader(text = stringResource(R.string.create_section_time))
        CreateTimeCard(
            startAtMillis = startAtMillis,
            onStartMillisChange = onStartMillisChange,
            endAtMillis = endAtMillis,
            onEndMillisChange = onEndMillisChange,
            dateTimeFormatter = dateTimeFormatter
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSaveClick,
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = stringResource(R.string.save_task),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun CreateMainInfoCard(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.task_name_hint)) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp),
                label = { Text(stringResource(R.string.task_description_hint)) },
                minLines = 4,
                shape = MaterialTheme.shapes.medium
            )
        }
    }
}

@Composable
private fun CreateTimeCard(
    startAtMillis: Long,
    onStartMillisChange: (Long) -> Unit,
    endAtMillis: Long,
    onEndMillisChange: (Long) -> Unit,
    dateTimeFormatter: SimpleDateFormat
) {
    val context = LocalContext.current

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(
                    R.string.start_datetime,
                    dateTimeFormatter.format(startAtMillis)
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            FilledTonalButton(
                onClick = {
                    context.showDateTimePicker(startAtMillis, onStartMillisChange)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(stringResource(R.string.choose_start))
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(
                    R.string.end_datetime,
                    dateTimeFormatter.format(endAtMillis)
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            FilledTonalButton(
                onClick = {
                    context.showDateTimePicker(endAtMillis, onEndMillisChange)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(stringResource(R.string.choose_end))
            }
        }
    }
}

@Composable
private fun CreateSectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )
}

private fun Context.showDateTimePicker(
    initialMillis: Long,
    onPicked: (Long) -> Unit
) {
    val calendar = Calendar.getInstance().apply { timeInMillis = initialMillis }
    DatePickerDialog(
        this,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    onPicked(calendar.timeInMillis)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}
