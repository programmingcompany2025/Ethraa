package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuditLog
import com.example.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    val auditLogs by viewModel.auditLogs.collectAsState()
    val reduceMotionEnabled by viewModel.reduceMotion.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0 = Settings & Tools, 1 = Audit Logs
    var showExportDialog by remember { mutableStateOf(false) }
    var exportFormat by remember { mutableStateOf("PDF") }

    // Settings local states
    var notificationsEnabled by remember { mutableStateOf(true) }
    var syncEnabled by remember { mutableStateOf(true) }
    var taxEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Toggle Tabs
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("إعدادات النظام وأدوات التصدير", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("سجل العمليات الرقابي", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
        }

        if (activeTab == 0) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Section 1: User & Permissions (إدارة المستخدمين والصلاحيات)
                item {
                    AdminSectionCard(title = "صلاحيات المستخدمين والتحكم بالأدوار") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            UserRoleRow(name = "المهندس أحمد الحربي (أنت)", role = "مدير هندسي كامل الصلاحيات")
                            UserRoleRow(name = "أ. سارة القحطاني", role = "مشرف مالي ومراجع مبيعات")
                            UserRoleRow(name = "المهندس خالد العتيبي", role = "مهندس تصاميم وإشراف تنفيذ")
                        }
                    }
                }

                // Section 2: Data & Export tools (تصدير التقارير إلى PDF, Excel, CSV)
                item {
                    AdminSectionCard(title = "مركز التقارير وتصدير البيانات الموحد") {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "قم بتصدير التقارير المالية والتشغيلية الشاملة بضغطة زر واحدة لمراجعتها مع المحاسبين أو مجلس الإدارة وزيادة الأرباح.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Right
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ExportFormatButton(format = "PDF", current = exportFormat, onSelect = { exportFormat = it })
                                ExportFormatButton(format = "Excel", current = exportFormat, onSelect = { exportFormat = it })
                                ExportFormatButton(format = "CSV", current = exportFormat, onSelect = { exportFormat = it })
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { showExportDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("توليد وتنزيل التقرير الشامل")
                            }
                        }
                    }
                }

                // Section 3: Backup & Restore (النسخ الاحتياطي واستعادة البيانات)
                item {
                    AdminSectionCard(title = "حماية البيانات واستقرار النظام") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Restore Button
                            OutlinedButton(
                                onClick = {
                                    val msg = viewModel.performRestore()
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Restore, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("استعادة البيانات", fontSize = 12.sp)
                            }

                            // Backup Button
                            Button(
                                onClick = {
                                    val msg = viewModel.performBackup()
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("نسخ احتياطي", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Section 4: System Configurations
                item {
                    AdminSectionCard(title = "الإعدادات العامة لمؤسسة التصميم") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            SettingToggleRow(
                                title = "تفعيل الإشعارات والتنبيهات التلقائية لتأخر الدفعات",
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it }
                            )
                            SettingToggleRow(
                                title = "المزامنة السحابية اللحظية للمعاملات المالية",
                                checked = syncEnabled,
                                onCheckedChange = { syncEnabled = it }
                            )
                            SettingToggleRow(
                                title = "استخدام ضريبة القيمة المضافة القياسية (15٪)",
                                checked = taxEnabled,
                                onCheckedChange = { taxEnabled = it }
                            )
                            SettingToggleRow(
                                title = "تقليل الحركة والمؤثرات البصرية ثلاثية الأبعاد",
                                checked = reduceMotionEnabled,
                                onCheckedChange = { viewModel.setReduceMotion(it) }
                            )
                        }
                    }
                }
            }
        } else {
            // Audit Logs History (سجل العمليات الرقابي)
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { viewModel.clearLogs() },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("مسح السجل")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "آخر الأنشطة والعمليات المالية والتشغيلية",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Right
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (auditLogs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "سجل العمليات فارغ حالياً.", fontSize = 13.sp, color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(auditLogs) { log ->
                            AuditLogRow(log = log)
                        }
                    }
                }
            }
        }
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text(
                    text = "اكتمل توليد التقرير بنجاح",
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
                    Text(
                        text = "تم تجميع وتوليد التقرير الشامل لشركة إثراء المصممين بصيغة $exportFormat بنجاح.\n\nالتقرير يشمل:\n• ملخص الأرباح والخسائر وحركة التدفق النقدي.\n• أداء المشاريع وتكلفة التنفيذ ونسب الإنجاز.\n• تقييم الحملات ومعدل العائد على الاستثمار (ROI).\n• ذمم ومستحقات العملاء وسجلات المبيعات.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Right,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExportDialog = false
                        Toast.makeText(context, "تم حفظ الملف بنجاح في مجلد التنزيلات", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("حفظ وتنزيل")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun AdminSectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun UserRoleRow(name: String, role: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = role,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun RowScope.ExportFormatButton(format: String, current: String, onSelect: (String) -> Unit) {
    val isSelected = format == current
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { onSelect(format) }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = format,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SettingToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
        )
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Right
        )
    }
}

@Composable
fun AuditLogRow(log: AuditLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = android.text.format.DateFormat.format("hh:mm a", log.timestamp).toString(),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = log.action,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = log.details,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
