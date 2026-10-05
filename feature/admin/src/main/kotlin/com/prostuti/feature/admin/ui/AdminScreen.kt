package com.prostuti.feature.admin.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prostuti.core.designsystem.ProstutiBadge
import com.prostuti.core.designsystem.ProstutiButton
import com.prostuti.core.designsystem.ProstutiCard
import com.prostuti.core.designsystem.ProstutiTextField
import com.prostuti.core.model.*
import com.prostuti.feature.admin.presentation.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val userMessage = uiState.userMessage
    LaunchedEffect(userMessage) {
        if (!userMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(userMessage)
            viewModel.onEvent(AdminUiEvent.DismissUserMessage)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "অ্যাডমিন প্যানেল",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "সিস্টেম ও কনটেন্ট ম্যানেজমেন্ট",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান",
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            when (uiState.selectedTab) {
                                AdminTab.CSV_IMPORT -> Unit
                                AdminTab.QUESTIONS -> viewModel.onEvent(AdminUiEvent.RefreshQuestions)
                                AdminTab.USERS -> viewModel.onEvent(AdminUiEvent.RefreshUsers)
                                AdminTab.MODEL_TESTS -> viewModel.onEvent(AdminUiEvent.RefreshModelTests)
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "রিফ্রেশ করুন")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Tab Header
            PrimaryTabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                AdminTab.entries.forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.onEvent(AdminUiEvent.SelectTab(tab)) },
                        text = {
                            Text(
                                text = tab.title,
                                fontWeight = if (uiState.selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    AdminTab.CSV_IMPORT -> Icons.Default.CloudUpload
                                    AdminTab.QUESTIONS -> Icons.Default.Quiz
                                    AdminTab.USERS -> Icons.Default.People
                                    AdminTab.MODEL_TESTS -> Icons.Default.Timer
                                },
                                contentDescription = tab.title,
                            )
                        }
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                when (uiState.selectedTab) {
                    AdminTab.CSV_IMPORT -> CsvImportTab(
                        uiState = uiState,
                        onEvent = viewModel::onEvent,
                    )
                    AdminTab.QUESTIONS -> QuestionsTab(
                        uiState = uiState,
                        onEvent = viewModel::onEvent,
                    )
                    AdminTab.USERS -> UsersTab(
                        uiState = uiState,
                        onEvent = viewModel::onEvent,
                    )
                    AdminTab.MODEL_TESTS -> ModelTestsTab(
                        uiState = uiState,
                        onEvent = viewModel::onEvent,
                    )
                }
            }
        }
    }

    // Question Edit Dialog
    val questionToEdit = uiState.questionToEdit
    if (questionToEdit != null) {
        EditQuestionDialog(
            question = questionToEdit,
            isSaving = uiState.isSavingQuestion,
            onDismiss = { viewModel.onEvent(AdminUiEvent.DismissEditQuestionDialog) },
            onSave = { updatedReq ->
                viewModel.onEvent(AdminUiEvent.SubmitEditQuestion(questionToEdit.id, updatedReq))
            },
        )
    }

    // Question Delete Dialog
    val questionToDelete = uiState.questionToDelete
    if (questionToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(AdminUiEvent.DismissDeleteQuestionDialog) },
            title = { Text("প্রশ্ন মুছে ফেলবেন?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "আপনি কি নিশ্চিত যে এই প্রশ্নটি সম্পূর্ণ মুছে ফেলতে চান? " +
                            "যদি কোনো পরীক্ষার্থী এটি ইতোমধ্যেই সলভ করে থাকে তবে এটি মুছতে দেওয়া হবে না।",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onEvent(AdminUiEvent.ConfirmDeleteQuestion) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    enabled = !uiState.isDeletingQuestion,
                ) {
                    if (uiState.isDeletingQuestion) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onError,
                        )
                    } else {
                        Text("মুছে ফেলুন")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(AdminUiEvent.DismissDeleteQuestionDialog) }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Model Test Create / Edit Dialog
    if (uiState.showModelTestDialog) {
        ModelTestFormDialog(
            editingModelTest = uiState.editingModelTest,
            isSaving = uiState.isSavingModelTest,
            onDismiss = { viewModel.onEvent(AdminUiEvent.DismissModelTestDialog) },
            onSaveCreate = { req -> viewModel.onEvent(AdminUiEvent.CreateModelTest(req)) },
            onSaveUpdate = { id, req -> viewModel.onEvent(AdminUiEvent.UpdateModelTest(id, req)) },
        )
    }

    // Model Test Delete Confirmation Dialog
    val modelTestToDelete = uiState.modelTestToDelete
    if (modelTestToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(AdminUiEvent.DismissDeleteModelTestDialog) },
            title = { Text("মডেল টেস্ট মুছে ফেলবেন?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "আপনি কি নিশ্চিত যে \"${modelTestToDelete.title}\" মডেল টেস্টটি সম্পূর্ণ মুছে ফেলতে চান?",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onEvent(AdminUiEvent.ConfirmDeleteModelTest) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    enabled = !uiState.isDeletingModelTest,
                ) {
                    if (uiState.isDeletingModelTest) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onError,
                        )
                    } else {
                        Text("মুছে ফেলুন")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(AdminUiEvent.DismissDeleteModelTestDialog) }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TAB 1: CSV IMPORT
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CsvImportTab(
    uiState: AdminUiState,
    onEvent: (AdminUiEvent) -> Unit,
) {
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            val name = getFileName(context, uri) ?: "questions.csv"
            if (bytes != null) {
                onEvent(AdminUiEvent.SelectCsvFile(fileName = name, bytes = bytes))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Mode Selector Card
        ProstutiCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "১. প্রশ্ন ইমপোর্ট টাইপ নির্বাচন করুন",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    FilterChip(
                        selected = uiState.targetQuestionType == QuestionType.BANK,
                        onClick = { onEvent(AdminUiEvent.SelectTargetQuestionType(QuestionType.BANK)) },
                        label = { Text("বিসিএস প্রশ্ন ব্যাংক") },
                        leadingIcon = {
                            if (uiState.targetQuestionType == QuestionType.BANK) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )

                    FilterChip(
                        selected = uiState.targetQuestionType == QuestionType.PRACTICE,
                        onClick = { onEvent(AdminUiEvent.SelectTargetQuestionType(QuestionType.PRACTICE)) },
                        label = { Text("অনুশীলন প্রশ্ন") },
                        leadingIcon = {
                            if (uiState.targetQuestionType == QuestionType.PRACTICE) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }

                // Information about format
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = if (uiState.targetQuestionType == QuestionType.BANK)
                                "প্রয়োজনীয় কলাম: exam_session, subject, topic, question_text, option_a, option_b, option_c, option_d, correct_option, explanation, difficulty"
                            else
                                "প্রয়োজনীয় কলাম: subject, topic, question_text, option_a, option_b, option_c, option_d, correct_option, explanation, difficulty",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp,
                        )
                    }
                }
            }
        }

        // File Picker Card
        ProstutiCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "২. CSV ফাইল নির্বাচন করুন",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                val fileName = uiState.selectedFileName
                val fileBytes = uiState.selectedFileBytes

                if (fileName != null && fileBytes != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Column {
                                    Text(
                                        text = fileName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = "${fileBytes.size / 1024} KB",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            IconButton(onClick = { onEvent(AdminUiEvent.ClearSelectedCsvFile) }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "বাতিল")
                            }
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("*/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(imageVector = Icons.Default.UploadFile, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("ফাইল বাছাই করুন (.csv)")
                    }
                }

                val isUploading = uiState.importStatus is CsvImportStatus.Uploading
                ProstutiButton(
                    text = if (isUploading) "আপলোড ও প্রক্রিয়াধীন..." else "ইমপোর্ট করুন",
                    onClick = { onEvent(AdminUiEvent.TriggerCsvImport) },
                    enabled = fileName != null && !isUploading,
                    loading = isUploading,
                )
            }
        }

        // Import Result Card
        when (val status = uiState.importStatus) {
            is CsvImportStatus.Success -> {
                val summary = status.summary
                ProstutiCard(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "ইমপোর্ট ফলাফল",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            IconButton(onClick = { onEvent(AdminUiEvent.DismissImportResult) }) {
                                Icon(Icons.Default.Close, contentDescription = "বন্ধ করুন")
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ProstutiBadge(
                                text = "সফল: ${summary.imported}",
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            if (summary.rejected.isNotEmpty()) {
                                ProstutiBadge(
                                    text = "বাতিল: ${summary.rejected.size}",
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                )
                            }
                        }

                        if (summary.rejected.isNotEmpty()) {
                            Text(
                                text = "বাতিলকৃত লাইনসমূহ ও ত্রুটি:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error,
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surface,
                                        RoundedCornerShape(8.dp),
                                    )
                                    .padding(8.dp),
                            ) {
                                summary.rejected.take(10).forEach { err ->
                                    Text(
                                        text = "• সারি ${err.row}: ${err.message}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error,
                                    )
                                }
                                if (summary.rejected.size > 10) {
                                    Text(
                                        text = "...আরও ${summary.rejected.size - 10}টি সারি বাতিল হয়েছে",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            is CsvImportStatus.Error -> {
                ProstutiCard(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                            )
                            Text(
                                text = status.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                        IconButton(onClick = { onEvent(AdminUiEvent.DismissImportResult) }) {
                            Icon(Icons.Default.Close, contentDescription = "বন্ধ করুন")
                        }
                    }
                }
            }
            else -> Unit
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TAB 2: QUESTIONS MANAGEMENT
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuestionsTab(
    uiState: AdminUiState,
    onEvent: (AdminUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Filters section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Type filters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = uiState.filterType == null,
                    onClick = { onEvent(AdminUiEvent.SetFilterType(null)) },
                    label = { Text("সকল টাইপ") },
                )
                FilterChip(
                    selected = uiState.filterType == QuestionType.BANK,
                    onClick = { onEvent(AdminUiEvent.SetFilterType(QuestionType.BANK)) },
                    label = { Text("BANK") },
                )
                FilterChip(
                    selected = uiState.filterType == QuestionType.PRACTICE,
                    onClick = { onEvent(AdminUiEvent.SetFilterType(QuestionType.PRACTICE)) },
                    label = { Text("PRACTICE") },
                )
            }

            // Subject scrollable chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                item {
                    FilterChip(
                        selected = uiState.filterSubject == null,
                        onClick = { onEvent(AdminUiEvent.SetFilterSubject(null)) },
                        label = { Text("সকল বিষয়") },
                    )
                }
                items(Subject.entries) { subject ->
                    FilterChip(
                        selected = uiState.filterSubject == subject,
                        onClick = { onEvent(AdminUiEvent.SetFilterSubject(subject)) },
                        label = { Text(subject.name) },
                    )
                }
            }
        }

        HorizontalDivider()

        // Content
        when (val status = uiState.questionsStatus) {
            is AdminQuestionsStatus.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            is AdminQuestionsStatus.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = status.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Button(onClick = { onEvent(AdminUiEvent.RefreshQuestions) }) {
                            Text("পুনরায় চেষ্টা করুন")
                        }
                    }
                }
            }
            is AdminQuestionsStatus.Success -> {
                val totalPages = (status.total + status.pageSize - 1) / status.pageSize

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                ) {
                    // Header counter
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "মোট প্রশ্ন: ${status.total}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "পৃষ্ঠা ${status.page + 1} / ${maxOf(1, totalPages)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    if (status.items.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "কোনো প্রশ্ন পাওয়া যায়নি",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(status.items, key = { it.id }) { question ->
                                AdminQuestionCard(
                                    question = question,
                                    onEdit = { onEvent(AdminUiEvent.OpenEditQuestionDialog(question)) },
                                    onDelete = { onEvent(AdminUiEvent.RequestDeleteQuestion(question)) },
                                )
                            }
                        }
                    }

                    // Pagination footer
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OutlinedButton(
                                onClick = { onEvent(AdminUiEvent.ChangeQuestionsPage(status.page - 1)) },
                                enabled = status.page > 0,
                            ) {
                                Text("আগের পৃষ্ঠা")
                            }

                            Text(
                                text = "${status.page + 1}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                            )

                            OutlinedButton(
                                onClick = { onEvent(AdminUiEvent.ChangeQuestionsPage(status.page + 1)) },
                                enabled = (status.page + 1) < totalPages,
                            ) {
                                Text("পরের পৃষ্ঠা")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminQuestionCard(
    question: AdminQuestionDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    ProstutiCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ProstutiBadge(
                        text = question.type.name,
                        containerColor = if (question.type == QuestionType.BANK)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer,
                    )
                    ProstutiBadge(
                        text = question.subject.name,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    val examSession = question.examSession
                    if (!examSession.isNullOrBlank()) {
                        ProstutiBadge(
                            text = examSession,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "সম্পাদনা",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "মুছে ফেলুন",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            // Question Text
            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )

            // Options grid
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                AdminOptionRow(label = "A", text = question.optionA, isCorrect = question.correctOption == Option.A)
                AdminOptionRow(label = "B", text = question.optionB, isCorrect = question.correctOption == Option.B)
                AdminOptionRow(label = "C", text = question.optionC, isCorrect = question.correctOption == Option.C)
                AdminOptionRow(label = "D", text = question.optionD, isCorrect = question.correctOption == Option.D)
            }

            val explanation = question.explanation
            if (!explanation.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "ব্যাখ্যা: $explanation",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminOptionRow(label: String, text: String, isCorrect: Boolean) {
    Surface(
        color = if (isCorrect) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent,
        shape = RoundedCornerShape(6.dp),
        border = if (isCorrect) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "$label.",
                fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = text,
                fontWeight = if (isCorrect) FontWeight.SemiBold else FontWeight.Normal,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            if (isCorrect) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "সঠিক",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TAB 3: USERS MANAGEMENT
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UsersTab(
    uiState: AdminUiState,
    onEvent: (AdminUiEvent) -> Unit,
) {
    when (val status = uiState.usersStatus) {
        is AdminUsersStatus.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is AdminUsersStatus.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = status.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Button(onClick = { onEvent(AdminUiEvent.RefreshUsers) }) {
                        Text("পুনরায় চেষ্টা করুন")
                    }
                }
            }
        }
        is AdminUsersStatus.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(status.users, key = { it.id }) { user ->
                    val isUpdatingThisUser = uiState.updatingUserRoleId == user.id
                    UserCard(
                        user = user,
                        isUpdating = isUpdatingThisUser,
                        onToggleRole = { onEvent(AdminUiEvent.ToggleUserRole(user.id, user.role)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun UserCard(
    user: AdminUserDto,
    isUpdating: Boolean,
    onToggleRole: () -> Unit,
) {
    ProstutiCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
            ) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (user.role == Role.ADMIN) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = user.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (user.role == Role.ADMIN) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    ProstutiBadge(
                        text = user.role.name,
                        containerColor = if (user.role == Role.ADMIN)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = if (user.role == Role.ADMIN)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }

            // Role toggle button
            if (isUpdating) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp),
                )
            } else {
                OutlinedButton(
                    onClick = onToggleRole,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = if (user.role == Role.ADMIN) "Make Student" else "Make Admin",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// EDIT QUESTION DIALOG
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditQuestionDialog(
    question: AdminQuestionDto,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (UpdateQuestionRequest) -> Unit,
) {
    var selectedSubject by remember { mutableStateOf(question.subject) }
    var topic by remember { mutableStateOf(question.topic ?: "") }
    var questionText by remember { mutableStateOf(question.questionText) }
    var optionA by remember { mutableStateOf(question.optionA) }
    var optionB by remember { mutableStateOf(question.optionB) }
    var optionC by remember { mutableStateOf(question.optionC) }
    var optionD by remember { mutableStateOf(question.optionD) }
    var correctOption by remember { mutableStateOf(question.correctOption) }
    var explanation by remember { mutableStateOf(question.explanation ?: "") }
    var difficulty by remember { mutableStateOf(question.difficulty ?: Difficulty.MEDIUM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("প্রশ্ন সম্পাদনা করুন", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // Subject Dropdown
                Text("বিষয়:", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(Subject.entries) { sub ->
                        FilterChip(
                            selected = selectedSubject == sub,
                            onClick = { selectedSubject = sub },
                            label = { Text(sub.name, style = MaterialTheme.typography.labelSmall) },
                        )
                    }
                }

                ProstutiTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = "টপিক (ঐচ্ছিক)",
                )

                ProstutiTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = "প্রশ্নের টেক্সট",
                    singleLine = false,
                )

                ProstutiTextField(
                    value = optionA,
                    onValueChange = { optionA = it },
                    label = "অপশন A",
                )
                ProstutiTextField(
                    value = optionB,
                    onValueChange = { optionB = it },
                    label = "অপশন B",
                )
                ProstutiTextField(
                    value = optionC,
                    onValueChange = { optionC = it },
                    label = "অপশন C",
                )
                ProstutiTextField(
                    value = optionD,
                    onValueChange = { optionD = it },
                    label = "অপশন D",
                )

                // Correct Option selector
                Text("সঠিক অপশন:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Option.entries.forEach { opt ->
                        FilterChip(
                            selected = correctOption == opt,
                            onClick = { correctOption = opt },
                            label = { Text(opt.name) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                ProstutiTextField(
                    value = explanation,
                    onValueChange = { explanation = it },
                    label = "ব্যাখ্যা (ঐচ্ছিক)",
                    singleLine = false,
                )

                // Difficulty selector
                Text("কঠিনতার মাত্রা:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Difficulty.entries.forEach { diff ->
                        FilterChip(
                            selected = difficulty == diff,
                            onClick = { difficulty = diff },
                            label = { Text(diff.name) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        UpdateQuestionRequest(
                            subject = selectedSubject,
                            topic = topic.ifBlank { null },
                            questionText = questionText,
                            optionA = optionA,
                            optionB = optionB,
                            optionC = optionC,
                            optionD = optionD,
                            correctOption = correctOption,
                            explanation = explanation.ifBlank { null },
                            difficulty = difficulty,
                        )
                    )
                },
                enabled = !isSaving && questionText.isNotBlank() && optionA.isNotBlank() &&
                        optionB.isNotBlank() && optionC.isNotBlank() && optionD.isNotBlank(),
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp),
                    )
                } else {
                    Text("সংরক্ষণ করুন")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        },
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// TAB 4: MODEL TESTS
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ModelTestsTab(
    uiState: AdminUiState,
    onEvent: (AdminUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        // Top Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "বিসিএস মডেল টেস্ট ব্যবস্থাপনা",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "লাইভ, আসন্ন ও আর্কাইভ মডেল টেস্ট কনফিগারেশন",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Button(
                onClick = { onEvent(AdminUiEvent.OpenCreateModelTestDialog) },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("নতুন টেস্ট", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(14.dp))

        if (uiState.isLoadingModelTests) {
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.modelTests.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
                    Text(
                        text = "কোনো মডেল টেস্ট পাওয়া যায়নি",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(uiState.modelTests) { test ->
                    ProstutiCard {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = test.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                )
                                Spacer(Modifier.width(8.dp))
                                val (statusText, statusBg, statusFg) = when (test.status) {
                                    ModelTestStatus.LIVE -> Triple("চলমান", Color(0xFFDC2626), Color.White)
                                    ModelTestStatus.UPCOMING -> Triple("আসন্ন", Color(0xFF0284C7), Color.White)
                                    ModelTestStatus.EXPIRED -> Triple("সমাপ্ত", Color(0xFF6B7280), Color.White)
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = statusBg,
                                ) {
                                    Text(
                                        text = statusText,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = statusFg,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    )
                                }
                            }

                            val desc = test.description
                            if (!desc.isNullOrBlank()) {
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }

                            // Metadata pills
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                ) {
                                    Text(
                                        text = "🏷 ${test.examSession}",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                ) {
                                    Text(
                                        text = "⏱ ${test.durationMinutes} মি.",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                ) {
                                    Text(
                                        text = "❓ ${test.totalQuestions} প্রশ্ন",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    )
                                }
                            }

                            // Actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                IconButton(onClick = { onEvent(AdminUiEvent.OpenEditModelTestDialog(test)) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "সম্পাদনা", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { onEvent(AdminUiEvent.RequestDeleteModelTest(test)) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "মুছুন", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelTestFormDialog(
    editingModelTest: ModelTestDto?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSaveCreate: (CreateModelTestRequest) -> Unit,
    onSaveUpdate: (String, UpdateModelTestRequest) -> Unit,
) {
    var title by remember { mutableStateOf(editingModelTest?.title ?: "") }
    var description by remember { mutableStateOf(editingModelTest?.description ?: "") }
    var examSession by remember { mutableStateOf(editingModelTest?.examSession ?: "") }
    var durationMinutesText by remember { mutableStateOf(editingModelTest?.durationMinutes?.toString() ?: "120") }
    var totalQuestionsText by remember { mutableStateOf(editingModelTest?.totalQuestions?.toString() ?: "200") }
    var totalMarksText by remember { mutableStateOf(editingModelTest?.totalMarks?.toString() ?: "200.0") }
    var startTime by remember { mutableStateOf(editingModelTest?.startTime ?: java.time.Instant.now().toString()) }
    var endTime by remember { mutableStateOf(editingModelTest?.endTime ?: java.time.Instant.now().plus(java.time.Duration.ofDays(15)).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (editingModelTest != null) "মডেল টেস্ট সম্পাদনা করুন" else "নতুন লাইভ মডেল টেস্ট তৈরি করুন",
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("মডেল টেস্ট শিরোনাম *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("সংক্ষিপ্ত বিবরণ") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = examSession,
                    onValueChange = { examSession = it },
                    label = { Text("পরীক্ষার সেশন ট্যাগ (examSession) *") },
                    placeholder = { Text("e.g. 47th BCS Special Model Test") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = durationMinutesText,
                        onValueChange = { durationMinutesText = it },
                        label = { Text("সময় (মিনিট)") },
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = totalQuestionsText,
                        onValueChange = { totalQuestionsText = it },
                        label = { Text("প্রশ্ন সংখ্যা") },
                        modifier = Modifier.weight(1f),
                    )
                }
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("শুরুর সময় (ISO format) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("শেষের সময় (ISO format) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val duration = durationMinutesText.toIntOrNull() ?: 120
                    val totalQ = totalQuestionsText.toIntOrNull() ?: 200
                    val marks = totalMarksText.toDoubleOrNull() ?: 200.0
                    if (editingModelTest != null) {
                        onSaveUpdate(
                            editingModelTest.id,
                            UpdateModelTestRequest(
                                title = title.trim(),
                                description = description.trim().ifBlank { null },
                                examSession = examSession.trim(),
                                durationMinutes = duration,
                                totalMarks = marks,
                                totalQuestions = totalQ,
                                startTime = startTime.trim(),
                                endTime = endTime.trim(),
                            )
                        )
                    } else {
                        onSaveCreate(
                            CreateModelTestRequest(
                                title = title.trim(),
                                description = description.trim().ifBlank { null },
                                examSession = examSession.trim(),
                                durationMinutes = duration,
                                totalMarks = marks,
                                totalQuestions = totalQ,
                                startTime = startTime.trim(),
                                endTime = endTime.trim(),
                            )
                        )
                    }
                },
                enabled = !isSaving && title.isNotBlank() && examSession.isNotBlank() && startTime.isNotBlank() && endTime.isNotBlank(),
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp),
                    )
                } else {
                    Text("সংরক্ষণ করুন")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// HELPER FUNCTIONS
// ─────────────────────────────────────────────────────────────────────────────

private fun getFileName(context: Context, uri: Uri): String? {
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    return cursor.getString(index)
                }
            }
        }
    }
    return uri.path?.let { path ->
        val cut = path.lastIndexOf('/')
        if (cut != -1) path.substring(cut + 1) else path
    }
}
