package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OperationsScreen(viewModel: AppViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 5 })
    val tabs = listOf("العملاء", "المشاريع", "المبيعات", "الإيرادات", "المصروفات")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("operations_screen")
    ) {
        // Scrollable Tab Row at the top
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> ClientsTab(viewModel)
                1 -> ProjectsTab(viewModel)
                2 -> SalesTab(viewModel)
                3 -> RevenuesTab(viewModel)
                4 -> ExpensesTab(viewModel)
            }
        }
    }
}

// --- Tab 1: Clients ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsTab(viewModel: AppViewModel) {
    val clients by viewModel.clients.collectAsState()
    val reduceMotion by viewModel.reduceMotion.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("VIP") }
    var rating by remember { mutableIntStateOf(5) }
    var dues by remember { mutableStateOf("") }
    var paid by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val totalDues by viewModel.totalDues.collectAsState()
                    val totalPaid by viewModel.totalPaid.collectAsState()

                    MetricSummaryBox(
                        title = "مستحقات على العملاء",
                        value = totalDues,
                        color = MaterialTheme.colorScheme.error,
                        icon = Icons.AutoMirrored.Filled.TrendingDown,
                        modifier = Modifier.weight(1f)
                    )
                    MetricSummaryBox(
                        title = "المدفوعات المستلمة",
                        value = totalPaid,
                        color = MaterialTheme.colorScheme.primary,
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            items(clients) { client ->
                ClientCard(
                    client = client,
                    onDelete = { viewModel.deleteClient(client) },
                    onUpdateDues = { duesAdd ->
                        viewModel.updateClient(client.copy(dues = client.dues + duesAdd))
                    },
                    onRecordPayment = { pay ->
                        val newDues = (client.dues - pay).coerceAtLeast(0.0)
                        viewModel.updateClient(client.copy(dues = newDues, totalPaid = client.totalPaid + pay))
                        viewModel.addRevenue(
                            source = "دفعة من العميل: ${client.name}",
                            amount = pay,
                            category = "رسوم خدمات",
                            notes = "سند قبض سداد دفعة عميل"
                        )
                    },
                    reduceMotion = reduceMotion
                )
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .testTag("add_client_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة عميل")
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "إضافة عميل جديد",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم العميل أو الشركة") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("رقم الهاتف") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("البريد الإلكتروني") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = dues,
                            onValueChange = { dues = it },
                            label = { Text("المستحقات الحالية") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = paid,
                            onValueChange = { paid = it },
                            label = { Text("المدفوعات الحالية") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    // Category Selector
                    Text(text = "تصنيف العميل", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("VIP", "عادي", "حكومي").forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat) }
                            )
                        }
                    }

                    // Rating Selector (1 to 5 Stars)
                    Text(text = "تقييم العميل", fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { rating = star }) {
                                Icon(
                                    imageVector = if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = if (star <= rating) Color(0xFFF59E0B) else Color.Gray
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addClient(
                                name = name,
                                phone = phone,
                                email = email,
                                category = category,
                                rating = rating,
                                dues = dues.toDoubleOrNull() ?: 0.0,
                                totalPaid = paid.toDoubleOrNull() ?: 0.0
                            )
                            // Reset state
                            name = ""; phone = ""; email = ""; dues = ""; paid = ""; category = "VIP"; rating = 5
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun ClientCard(
    client: Client,
    onDelete: () -> Unit,
    onUpdateDues: (Double) -> Unit,
    onRecordPayment: (Double) -> Unit,
    reduceMotion: Boolean = false
) {
    var showActionDialog by remember { mutableStateOf(false) }
    var actionAmount by remember { mutableStateOf("") }
    var isPaymentAction by remember { mutableStateOf(true) } // true = record payment, false = add contract dues

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .premium3D(
                shape = RoundedCornerShape(16.dp),
                reduceMotion = reduceMotion
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = client.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "تصنيف: ${client.category}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        Text(text = " • ", fontSize = 11.sp)
                        Row {
                            repeat(client.rating) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(12.dp))

            // Contact Info
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(text = client.phone, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            }
            if (client.email.isNotBlank()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(text = client.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dues & Total Paid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = "المدفوعات الكلية", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text(text = CurrencyUtils.formatLibyanDinar(client.totalPaid), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "المستحقات المطلوبة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text(text = CurrencyUtils.formatLibyanDinar(client.dues), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (client.dues > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Client Management Action Buttons (عقود، مستحقات، مدفوعات)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Button 1: Record payment (سند قبض / تحصيل)
                OutlinedButton(
                    onClick = {
                        isPaymentAction = true
                        showActionDialog = true
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("سند قبض", fontSize = 11.sp)
                }

                // Button 2: Add contract dues (عقد جديد / إضافة مستحق)
                Button(
                    onClick = {
                        isPaymentAction = false
                        showActionDialog = true
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة عقد", fontSize = 11.sp)
                }
            }
        }
    }

    if (showActionDialog) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = {
                Text(
                    text = if (isPaymentAction) "تسجيل دفعة مستلمة (سند قبض)" else "ربط عقد جديد وإضافة مستحق",
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = if (isPaymentAction) "العميل: ${client.name}\nأدخل قيمة الدفعة المستلمة نقداً أو بحوالة بنكية لتحديث ذمته المالية وتسجيل إيراد تلقائي." else "أدخل إجمالي قيمة العقد الجديد المبرم مع العميل لربطه بملفه وإضافته لمستحقاته المعلقة.", fontSize = 12.sp, textAlign = TextAlign.Right)
                    OutlinedTextField(
                        value = actionAmount,
                        onValueChange = { actionAmount = it },
                        label = { Text("المبلغ (د.ل)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = actionAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0.0) {
                            if (isPaymentAction) {
                                onRecordPayment(amt)
                            } else {
                                onUpdateDues(amt)
                            }
                            actionAmount = ""
                            showActionDialog = false
                        }
                    }
                ) {
                    Text("تأكيد")
                }
            },
            dismissButton = {
                TextButton(onClick = { showActionDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}


// --- Tab 2: Projects ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsTab(viewModel: AppViewModel) {
    val projects by viewModel.projects.collectAsState()
    val clients by viewModel.clients.collectAsState()
    val reduceMotion by viewModel.reduceMotion.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var selectedClient by remember { mutableStateOf<Client?>(null) }
    var budget by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("نشط") }
    var completion by remember { mutableStateOf("0") }
    var stage by remember { mutableStateOf("التخطيط المبدئي") }
    var notes by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val active by viewModel.activeProjectsCount.collectAsState()
                    val completed by viewModel.completedProjectsCount.collectAsState()

                    MetricSummaryBox(
                        title = "مشاريع قيد الإنجاز",
                        value = active.toDouble(),
                        color = MaterialTheme.colorScheme.primary,
                        icon = Icons.Default.HourglassEmpty,
                        modifier = Modifier.weight(1f),
                        isCurrency = false
                    )
                    MetricSummaryBox(
                        title = "مشاريع مكتملة",
                        value = completed.toDouble(),
                        color = Color(0xFF10B981),
                        icon = Icons.Default.CheckCircleOutline,
                        modifier = Modifier.weight(1f),
                        isCurrency = false
                    )
                }
            }

            items(projects) { project ->
                ProjectCard(
                    project = project,
                    onDelete = { viewModel.deleteProject(project) },
                    onUpdateProgress = { compl, stg ->
                        viewModel.updateProject(project.copy(completionPercentage = compl, stages = stg))
                    },
                    onRecordCost = { addCost ->
                        viewModel.updateProject(project.copy(cost = project.cost + addCost))
                        viewModel.addExpense(
                            category = "تكلفة تنفيذ مشاريع",
                            amount = addCost,
                            notes = "تكاليف ومستلزمات تنفيذ لمشروع: ${project.name}"
                        )
                    },
                    reduceMotion = reduceMotion
                )
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .testTag("add_project_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "إنشاء مشروع")
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "إنشاء مشروع جديد",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم المشروع الهندسية") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Client Dropdown Selector (Simple)
                    Text(text = "ربط بالعميل المستفيد", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        clients.take(4).forEach { cl ->
                            FilterChip(
                                selected = selectedClient?.id == cl.id,
                                onClick = { selectedClient = cl },
                                label = { Text(cl.name.take(15)) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = budget,
                            onValueChange = { budget = it },
                            label = { Text("الميزانية الكلية") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = cost,
                            onValueChange = { cost = it },
                            label = { Text("التكلفة المبدئية") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    OutlinedTextField(
                        value = stage,
                        onValueChange = { stage = it },
                        label = { Text("مرحلة التنفيذ الحالية") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("وصف مختصر للمشروع") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clientObj = selectedClient
                        if (name.isNotBlank() && clientObj != null) {
                            viewModel.addProject(
                                name = name,
                                clientId = clientObj.id,
                                clientName = clientObj.name,
                                budget = budget.toDoubleOrNull() ?: 0.0,
                                cost = cost.toDoubleOrNull() ?: 0.0,
                                status = "نشط",
                                completion = completion.toIntOrNull() ?: 0,
                                stages = stage,
                                notes = notes
                            )
                            // Reset
                            name = ""; selectedClient = null; budget = ""; cost = ""; stage = "التخطيط المبدئي"; notes = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun ProjectCard(
    project: Project,
    onDelete: () -> Unit,
    onUpdateProgress: (Int, String) -> Unit,
    onRecordCost: (Double) -> Unit,
    reduceMotion: Boolean = false
) {
    var showProgressDialog by remember { mutableStateOf(false) }
    var showCostDialog by remember { mutableStateOf(false) }

    var progressVal by remember { mutableStateOf(project.completionPercentage.toString()) }
    var stageVal by remember { mutableStateOf(project.stages) }
    var costVal by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .premium3DSilver(
                shape = RoundedCornerShape(16.dp),
                reduceMotion = reduceMotion
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = project.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "العميل: ${project.clientName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Architecture, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(12.dp))

            // Completion % Bar and Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "نسبة الإنجاز: ${project.completionPercentage}%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "المرحلة الحالية: ${project.stages}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { project.completionPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Budget, Cost and Net Expected Profit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    val expectedProfit = project.budget - project.cost
                    Text(text = "الأرباح المتوقعة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text(
                        text = CurrencyUtils.formatLibyanDinar(expectedProfit),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (expectedProfit >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "تكاليف التنفيذ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text(text = CurrencyUtils.formatLibyanDinar(project.cost), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "ميزانية المشروع", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text(text = CurrencyUtils.formatLibyanDinar(project.budget), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            if (project.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = project.notes,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Buttons: update stage/progress, record cost
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showCostDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تسجيل مصروف", fontSize = 11.sp)
                }

                Button(
                    onClick = { showProgressDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تحديث الإنجاز", fontSize = 11.sp)
                }
            }
        }
    }

    if (showProgressDialog) {
        AlertDialog(
            onDismissRequest = { showProgressDialog = false },
            title = {
                Text(
                    text = "تحديث حالة ونسبة الإنجاز",
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = progressVal,
                        onValueChange = { progressVal = it },
                        label = { Text("نسبة الإنجاز الحالية (٪)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = stageVal,
                        onValueChange = { stageVal = it },
                        label = { Text("اسم المرحلة الحالية") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val prog = progressVal.toIntOrNull() ?: project.completionPercentage
                        onUpdateProgress(prog, stageVal)
                        showProgressDialog = false
                    }
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProgressDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (showCostDialog) {
        AlertDialog(
            onDismissRequest = { showCostDialog = false },
            title = {
                Text(
                    text = "تسجيل مصروف مباشر للمشروع",
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "تسجيل قيمة المواد، المعدات أو رواتب العمالة المباشرة المخصصة لتنفيذ هذا المشروع لحساب صافي الربحية بدقة.", fontSize = 12.sp, textAlign = TextAlign.Right)
                    OutlinedTextField(
                        value = costVal,
                        onValueChange = { costVal = it },
                        label = { Text("مبلغ التكلفة الإضافية (د.ل)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = costVal.toDoubleOrNull() ?: 0.0
                        if (amt > 0.0) {
                            onRecordCost(amt)
                            costVal = ""
                            showCostDialog = false
                        }
                    }
                ) {
                    Text("تأكيد")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCostDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}


// --- Tab 3: Sales (Invoices, Quotations, Contracts) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesTab(viewModel: AppViewModel) {
    val invoices by viewModel.invoices.collectAsState()
    val clients by viewModel.clients.collectAsState()
    val reduceMotion by viewModel.reduceMotion.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var invoiceNum by remember { mutableStateOf("") }
    var selectedClient by remember { mutableStateOf<Client?>(null) }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("فاتورة") } // عرض سعر, عقد, فاتورة, سند قبض, سند صرف
    var status by remember { mutableStateOf("غير مدفوع") }
    var notes by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val salesTarget = 1000000.0 // 1 Million Dinar Target
                    val actualSales = invoices.filter { it.type == "فاتورة" || it.type == "عقد" }.sumOf { it.amount }

                    MetricSummaryBox(
                        title = "أهداف المبيعات",
                        value = salesTarget,
                        color = MaterialTheme.colorScheme.tertiary,
                        icon = Icons.Default.Adjust,
                        modifier = Modifier.weight(1f)
                    )
                    MetricSummaryBox(
                        title = "المبيعات المحققة",
                        value = actualSales,
                        color = MaterialTheme.colorScheme.primary,
                        icon = Icons.Default.Campaign,
                        modifier = Modifier.weight(1f),
                        subtitle = "نسبة تحقيق الهدف: ${String.format("%.1f", (actualSales / salesTarget) * 100)}%"
                    )
                }
            }

            items(invoices) { invoice ->
                InvoiceCard(
                    invoice = invoice,
                    onDelete = { viewModel.deleteInvoice(invoice) },
                    onStatusChange = { newStatus ->
                        viewModel.updateInvoice(invoice.copy(status = newStatus))
                        if (newStatus == "مدفوع") {
                            viewModel.addRevenue(
                                source = "سداد مستند: ${invoice.invoiceNumber}",
                                amount = invoice.amount,
                                category = "رسوم خدمات",
                                notes = "سداد كلي للمستند المالي"
                            )
                        }
                    },
                    reduceMotion = reduceMotion
                )
            }
        }

        FloatingActionButton(
            onClick = {
                invoiceNum = "INV-2026-${(100..999).random()}"
                showAddDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .testTag("add_sales_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "إنشاء مستند")
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "إنشاء مستند مبيعات جديد",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = invoiceNum,
                        onValueChange = { invoiceNum = it },
                        label = { Text("رقم المستند / العقد") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Client Selector
                    Text(text = "ربط بالعميل", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        clients.take(3).forEach { cl ->
                            FilterChip(
                                selected = selectedClient?.id == cl.id,
                                onClick = { selectedClient = cl },
                                label = { Text(cl.name.take(15)) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("قيمة المستند الكلية") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // Document Type
                    Text(text = "نوع المستند", fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("فاتورة", "عقد", "عرض سعر").forEach { tp ->
                            FilterChip(
                                selected = type == tp,
                                onClick = { type = tp },
                                label = { Text(tp) }
                            )
                        }
                    }

                    // Status
                    Text(text = "حالة الدفع", fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("مدفوع", "غير مدفوع").forEach { st ->
                            FilterChip(
                                selected = status == st,
                                onClick = { status = st },
                                label = { Text(st) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("تفاصيل أو شروط المستند") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cl = selectedClient
                        if (invoiceNum.isNotBlank() && cl != null && amount.isNotBlank()) {
                            viewModel.addInvoice(
                                invoiceNumber = invoiceNum,
                                projectId = 0,
                                projectName = "",
                                clientId = cl.id,
                                clientName = cl.name,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                status = status,
                                type = type,
                                notes = notes
                            )
                            // If marked paid, automatically register revenue too
                            if (status == "مدفوع") {
                                viewModel.addRevenue(
                                    source = "مستند مبيعات تلقائي: $invoiceNum",
                                    amount = amount.toDoubleOrNull() ?: 0.0,
                                    category = "رسوم خدمات",
                                    notes = "سداد فوري عند إنشاء مستند"
                                )
                            }
                            // Reset
                            invoiceNum = ""; selectedClient = null; amount = ""; type = "فاتورة"; status = "غير مدفوع"; notes = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("حفظ المستند")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun InvoiceCard(
    invoice: Invoice,
    onDelete: () -> Unit,
    onStatusChange: (String) -> Unit,
    reduceMotion: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .premium3D(
                shape = RoundedCornerShape(16.dp),
                reduceMotion = reduceMotion
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = invoice.invoiceNumber, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "العميل: ${invoice.clientName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (invoice.type) {
                            "عقد" -> Icons.Default.BorderColor
                            "عرض سعر" -> Icons.Default.ContactPage
                            else -> Icons.Default.Receipt
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = "قيمة المستند", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text(text = CurrencyUtils.formatLibyanDinar(invoice.amount), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text(invoice.type) }
                        )

                        val isPaid = invoice.status == "مدفوع"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isPaid) Color(0xFF10B981).copy(alpha = 0.1f) else MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = invoice.status,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPaid) Color(0xFF10B981) else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            if (invoice.status == "غير مدفوع") {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onStatusChange("مدفوع") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تسجيل كـ مدفوع بالكامل", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}


// --- Tab 4: Revenues (الإيرادات) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevenuesTab(viewModel: AppViewModel) {
    val revenues by viewModel.revenues.collectAsState()
    val reduceMotion by viewModel.reduceMotion.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var source by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("رسوم تصميم") }
    var notes by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                val totalRev by viewModel.totalRevenue.collectAsState()
                MetricSummaryBox(
                    title = "إجمالي التدفقات النقدية الواردة (الإيرادات)",
                    value = totalRev,
                    color = MaterialTheme.colorScheme.primary,
                    icon = Icons.Default.ArrowCircleDown,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(revenues) { revenue ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .premium3D(
                            shape = RoundedCornerShape(12.dp),
                            reduceMotion = reduceMotion
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { viewModel.deleteRevenue(revenue) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = CurrencyUtils.formatLibyanDinar(revenue.amount), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(text = "البند: ${revenue.category}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = revenue.source, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            if (revenue.notes.isNotBlank()) {
                                Text(text = revenue.notes, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .testTag("add_revenue_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "تسجيل إيراد")
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "تسجيل إيراد مالي جديد",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = source,
                        onValueChange = { source = it },
                        label = { Text("مصدر الإيراد") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("المبلغ (د.ل)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // Categories
                    Text(text = "تصنيف الإيراد", fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("رسوم تصميم", "رسوم خدمات", "رسوم استشارات").forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظات") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amount.toDoubleOrNull() ?: 0.0
                        if (source.isNotBlank() && amt > 0.0) {
                            viewModel.addRevenue(source, amt, category, notes)
                            source = ""; amount = ""; notes = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}


// --- Tab 5: Expenses (المصروفات) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesTab(viewModel: AppViewModel) {
    val expenses by viewModel.expenses.collectAsState()
    val reduceMotion by viewModel.reduceMotion.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var category by remember { mutableStateOf("رواتب الموظفين") }
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                val totalExp by viewModel.totalExpenses.collectAsState()
                MetricSummaryBox(
                    title = "إجمالي التكاليف والمصروفات الخارجة",
                    value = totalExp,
                    color = MaterialTheme.colorScheme.error,
                    icon = Icons.Default.ArrowCircleUp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(expenses) { expense ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .premium3DSilver(
                            shape = RoundedCornerShape(12.dp),
                            reduceMotion = reduceMotion
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { viewModel.deleteExpense(expense) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = CurrencyUtils.formatLibyanDinar(expense.amount), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            Text(text = "التاريخ: ${android.text.format.DateFormat.format("yyyy-MM-dd", expense.date)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = expense.category, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            if (expense.notes.isNotBlank()) {
                                Text(text = expense.notes, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .testTag("add_expense_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "تسجيل مصروف")
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "تسجيل مصروف جديد",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("قيمة المصروف (د.ل)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // Categories
                    Text(text = "تصنيف المصروف", fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("رواتب الموظفين", "إيجارات", "المعدات والبرامج").forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("تفاصيل البند والملاحظات") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amount.toDoubleOrNull() ?: 0.0
                        if (amt > 0.0) {
                            viewModel.addExpense(category, amt, notes)
                            amount = ""; notes = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("حفظ المصروف")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}


// --- Common Metrics Display Box ---
@Composable
fun MetricSummaryBox(
    title: String,
    value: Double,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isCurrency: Boolean = true,
    subtitle: String? = null
) {
    Box(
        modifier = modifier
            .premium3DSilver(
                shape = RoundedCornerShape(12.dp),
                reduceMotion = false // subtle static depth
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isCurrency) CurrencyUtils.formatLibyanDinar(value) else value.toInt().toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            if (subtitle != null) {
                Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
