package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.AppViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: AppViewModel, onNavigateToTab: (Int) -> Unit) {
    val totalRevenue by viewModel.totalRevenue.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val netProfit by viewModel.netProfit.collectAsState()
    val activeProjects by viewModel.activeProjectsCount.collectAsState()
    val completedProjects by viewModel.completedProjectsCount.collectAsState()
    val vipClients by viewModel.vipClientsCount.collectAsState()
    val reduceMotion by viewModel.reduceMotion.collectAsState()

    // Smart simulated notifications customized to Libyan context & Libyan Dinars (د.ل)
    val notifications = remember {
        mutableStateListOf(
            "إشعار ذكي: مجمع السراج السكني جاهز للتسليم النهائي، يرجى تجهيز فواتير الإغلاق لزيادة السيولة بنسبة 12٪.",
            "إشعار مالي: تم تسجيل دفعة جديدة بقيمة 150,000 د.ل من المؤسسة الوطنية للتنمية السكنية.",
            "إشعار مبيعات: العميل 'فندق تيبستي' مهتم بعرض السعر المقدم، يرجى المتابعة اليوم لكسب الصفقة.",
            "تحذير مخاطر: العميل 'مكتب طرابلس' تجاوز تاريخ استحقاق الدفعة الثالثة (15,000 د.ل)."
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner with Premium Obsidian-Gold 3D Style
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .premium3D(
                        shape = RoundedCornerShape(24.dp),
                        reduceMotion = reduceMotion
                    )
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "إثراء المصممين • هندسة الأرباح الراقية",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "لوحة القيادة المالية والتشغيلية الذكية",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Right
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "نسعى لتمكين الشركات الهندسية من زيادة الأرباح وتنظيم الإيرادات بأعلى دقة.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Right
                    )
                }
            }
        }

        // Section Title: Financial Overview (لوحة التحكم المالية)
        item {
            Text(
                text = "الملخص المالي العام",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Right
            )
        }

        // Live Financial Metrics Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Revenues
                FinancialCard(
                    title = "إجمالي الإيرادات",
                    amount = totalRevenue,
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    reduceMotion = reduceMotion
                )

                // Total Expenses
                FinancialCard(
                    title = "إجمالي المصروفات",
                    amount = totalExpenses,
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f),
                    reduceMotion = reduceMotion
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Net Profit
                FinancialCard(
                    title = "صافي الربح",
                    amount = netProfit,
                    icon = Icons.Filled.AccountBalanceWallet,
                    color = if (netProfit >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                    modifier = Modifier.weight(1f),
                    subtitle = "نسبة الهامش: ${if (totalRevenue > 0) String.format("%.1f", (netProfit / totalRevenue) * 100) else "0"}%",
                    reduceMotion = reduceMotion
                )

                // Active Projects Count
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .premium3DSilver(
                            shape = RoundedCornerShape(16.dp),
                            reduceMotion = reduceMotion
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.BusinessCenter,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "المشاريع النشطة",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "$activeProjects مشاريع",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$completedProjects مشروع منتهي",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Quick Stats row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickStatItem(
                    label = "عملاء كبار VIP",
                    value = "$vipClients عملاء",
                    icon = Icons.Filled.Star,
                    modifier = Modifier.weight(1f),
                    reduceMotion = reduceMotion
                )
                QuickStatItem(
                    label = "سجل العمليات",
                    value = "مؤمن بالكامل",
                    icon = Icons.Filled.Security,
                    modifier = Modifier.weight(1f),
                    reduceMotion = reduceMotion
                )
            }
        }

        // Section Title: Notifications
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "الإشعارات الذكية والتنبيهات",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Notification List
        if (notifications.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "لا توجد تنبيهات ذكية حالياً. نظامك آمن ويعمل بكفاءة.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            items(notifications) { notification ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (notification.contains("تحذير"))
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                        else
                            MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = { notifications.remove(notification) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "حذف الإشعار",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = notification,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Right,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 20.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (notification.contains("تحذير"))
                                        MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                    else if (notification.contains("مالي"))
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    else
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (notification.contains("تحذير"))
                                    Icons.Default.Warning
                                else if (notification.contains("مالي"))
                                    Icons.Default.AttachMoney
                                else
                                    Icons.Default.TipsAndUpdates,
                                contentDescription = null,
                                tint = if (notification.contains("تحذير"))
                                    MaterialTheme.colorScheme.error
                                else if (notification.contains("مالي"))
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Profit Boosting Direct Actions
        item {
            Text(
                text = "إجراءات فورية لزيادة الأرباح",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Right
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // AI Consult Suggestion
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab(3) }, // Navigate to AI
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Psychology,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "توقع الأرباح بالذكاء الاصطناعي",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Add Lead Suggestion
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab(2) }, // Navigate to Marketing
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Campaign,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "تحويل العملاء المحتملين",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FinancialCard(
    title: String,
    amount: Double,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    reduceMotion: Boolean = false
) {
    Box(
        modifier = modifier
            .premium3D(
                shape = RoundedCornerShape(16.dp),
                reduceMotion = reduceMotion,
                glowColor = color.copy(alpha = 0.12f)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Right
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = CurrencyUtils.formatLibyanDinar(amount),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Right
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun QuickStatItem(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    reduceMotion: Boolean = false
) {
    Box(
        modifier = modifier
            .premium3DSilver(
                shape = RoundedCornerShape(12.dp),
                reduceMotion = reduceMotion
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Right
                )
                Text(
                    text = value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Right
                )
            }
        }
    }
}
