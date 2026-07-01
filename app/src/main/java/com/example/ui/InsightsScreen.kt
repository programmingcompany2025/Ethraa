package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
fun InsightsScreen(viewModel: AppViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val tabs = listOf("التحليلات المالية والمؤشرات", "إدارة التسويق والحملات")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("insights_screen")
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
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
                0 -> AnalyticsTab(viewModel)
                1 -> MarketingTab(viewModel)
            }
        }
    }
}

// --- Sub-Tab 1: Analytics with Custom Drawing Charts ---
@Composable
fun AnalyticsTab(viewModel: AppViewModel) {
    val totalRevenue by viewModel.totalRevenue.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val netProfit by viewModel.netProfit.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val reduceMotion by viewModel.reduceMotion.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // KPI Summary Header
        item {
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
                    Text(
                        text = "مؤشر الأداء المالي الرئيسي (KPI)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Right
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val profitMargin = if (totalRevenue > 0) (netProfit / totalRevenue) * 100 else 0.0
                    Text(
                        text = "هامش صافي الربح الحالي: ${String.format("%.1f", profitMargin)}%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Right
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "التقييم: هامش ربح ممتاز لشركات الاستشارات والتصميم الهندسية (المعيار العالمي: 15-25٪).",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Right
                    )
                }
            }
        }

        // Custom drawn Chart using Compose Canvas
        item {
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
                    Text(
                        text = "التحليل المقارن: التدفقات النقدية والأرباح",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Draw the custom bar chart
                    val primaryColor = MaterialTheme.colorScheme.primary
                    val errorColor = MaterialTheme.colorScheme.error
                    val tertiaryColor = MaterialTheme.colorScheme.tertiary

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        val maxVal = maxOf(totalRevenue, totalExpenses, netProfit, 1.0)
                        val width = size.width
                        val height = size.height

                        // Calculate column positions
                        val colWidth = 60.dp.toPx()
                        val spacing = (width - (colWidth * 3)) / 4

                        // Col 1: Revenue
                        val revHeight = ((totalRevenue / maxVal) * height * 0.8f).toFloat()
                        drawRect(
                            color = primaryColor,
                            topLeft = Offset(spacing, height - revHeight),
                            size = Size(colWidth, revHeight)
                        )

                        // Col 2: Expense
                        val expHeight = ((totalExpenses / maxVal) * height * 0.8f).toFloat()
                        drawRect(
                            color = errorColor,
                            topLeft = Offset(spacing * 2 + colWidth, height - expHeight),
                            size = Size(colWidth, expHeight)
                        )

                        // Col 3: Profit
                        val profHeight = ((netProfit.coerceAtLeast(0.0) / maxVal) * height * 0.8f).toFloat()
                        drawRect(
                            color = tertiaryColor,
                            topLeft = Offset(spacing * 3 + colWidth * 2, height - profHeight),
                            size = Size(colWidth, profHeight)
                        )

                        // Baseline
                        drawLine(
                            color = Color.Gray,
                            start = Offset(0f, height),
                            end = Offset(width, height),
                            strokeWidth = 2f
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Chart Legends
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LegendItem(color = MaterialTheme.colorScheme.tertiary, text = "صافي الربح")
                        LegendItem(color = MaterialTheme.colorScheme.error, text = "المصروفات")
                        LegendItem(color = MaterialTheme.colorScheme.primary, text = "الإيرادات")
                    }
                }
            }
        }

        // Project Performance Matrix
        item {
            Text(
                text = "تحليل كفاءة وربحية المشاريع",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Right
            )
        }

        items(projects) { proj ->
            val expectedProfit = proj.budget - proj.cost
            val profitPercent = if (proj.budget > 0) (expectedProfit / proj.budget) * 100 else 0.0

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .premium3DSilver(
                        shape = RoundedCornerShape(12.dp),
                        reduceMotion = reduceMotion
                    )
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profit margin indicator
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (profitPercent >= 40) Color(0xFF10B981).copy(alpha = 0.1f) else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "هامش الربح: ${String.format("%.0f", profitPercent)}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (profitPercent >= 40) Color(0xFF10B981) else MaterialTheme.colorScheme.tertiary
                            )
                        }
                        Text(text = proj.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "الميزانية: ${CurrencyUtils.formatLibyanDinar(proj.budget)} • التكاليف: ${CurrencyUtils.formatLibyanDinar(proj.cost)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}


// --- Sub-Tab 2: Marketing, Leads & Campaigns ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketingTab(viewModel: AppViewModel) {
    val leads by viewModel.leads.collectAsState()
    val campaigns by viewModel.campaigns.collectAsState()
    val reduceMotion by viewModel.reduceMotion.collectAsState()
    var showLeadDialog by remember { mutableStateOf(false) }

    var leadName by remember { mutableStateOf("") }
    var leadPhone by remember { mutableStateOf("") }
    var leadEmail by remember { mutableStateOf("") }
    var leadValue by remember { mutableStateOf("") }
    var leadSource by remember { mutableStateOf("وسائل التواصل") }
    var leadStatus by remember { mutableStateOf("جديد") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Loyalty Program & Referral Incentives Box (برامج الولاء والإحالة)
            item {
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "برنامج الإحالات وكوبونات الولاء النشطة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "كوبون الولاء النشط: ETHRAA20 (خصم 20٪ للعملاء الدائمين على عقود الاستشارات الهندسية). يساعد على تسريع إغلاق العقود بنسبة 35٪.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                            textAlign = TextAlign.Right,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Campaigns Evaluation
            item {
                Text(
                    text = "تقييم الحملات وقياس العائد (ROI)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
            }

            items(campaigns) { camp ->
                val roi = if (camp.budget > 0) ((camp.salesGenerated - camp.budget) / camp.budget) * 100 else 0.0
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .premium3D(
                            shape = RoundedCornerShape(12.dp),
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
                            IconButton(onClick = { viewModel.deleteCampaign(camp) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = camp.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "ميزانية الحملة: ${CurrencyUtils.formatLibyanDinar(camp.budget)} • عائد مالي: ${CurrencyUtils.formatLibyanDinar(camp.salesGenerated)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "مؤشر العائد على الاستثمار ROI: +${String.format("%.0f", roi)}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }

            // Leads Pipeline
            item {
                Text(
                    text = "متابعة العملاء المحتملين والصفقات قيد التفاوض (Leads)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
            }

            items(leads) { lead ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .premium3DSilver(
                            shape = RoundedCornerShape(12.dp),
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
                            IconButton(onClick = { viewModel.deleteLead(lead) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = lead.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "هاتف: ${lead.phone} • المصدر: ${lead.source}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Status update button
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (lead.status != "مغلق رابح") {
                                    TextButton(
                                        onClick = {
                                            viewModel.updateLead(lead.copy(status = "مغلق رابح"))
                                            // Auto add client and invoice
                                            viewModel.addClient(
                                                name = lead.name,
                                                phone = lead.phone,
                                                email = lead.email,
                                                category = "جديد",
                                                rating = 5,
                                                dues = lead.estimatedValue,
                                                totalPaid = 0.0
                                            )
                                        }
                                    ) {
                                        Text("كسب الصفقة ✔", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Info tag
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "القيمة: ${CurrencyUtils.formatLibyanDinar(lead.estimatedValue)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = lead.status,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showLeadDialog = true },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .testTag("add_lead_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "إضافة عميل محتمل")
        }
    }

    if (showLeadDialog) {
        AlertDialog(
            onDismissRequest = { showLeadDialog = false },
            title = {
                Text(
                    text = "إضافة عميل محتمل جديد",
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
                        value = leadName,
                        onValueChange = { leadName = it },
                        label = { Text("اسم العميل المحتمل") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = leadPhone,
                        onValueChange = { leadPhone = it },
                        label = { Text("رقم الهاتف") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    OutlinedTextField(
                        value = leadEmail,
                        onValueChange = { leadEmail = it },
                        label = { Text("البريد الإلكتروني") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    OutlinedTextField(
                        value = leadValue,
                        onValueChange = { leadValue = it },
                        label = { Text("القيمة التقديرية للصفقة (د.ل)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // Source
                    Text(text = "مصدر العميل المحتمل", fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("وسائل التواصل", "إحالة", "حملة إعلانية").forEach { src ->
                            FilterChip(
                                selected = leadSource == src,
                                onClick = { leadSource = src },
                                label = { Text(src) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val valDouble = leadValue.toDoubleOrNull() ?: 0.0
                        if (leadName.isNotBlank() && leadPhone.isNotBlank()) {
                            viewModel.addLead(leadName, leadPhone, leadEmail, leadStatus, leadSource, valDouble, "")
                            leadName = ""; leadPhone = ""; leadEmail = ""; leadValue = ""
                            showLeadDialog = false
                        }
                    }
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeadDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
