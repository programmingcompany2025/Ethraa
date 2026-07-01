package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.AppViewModel

@Composable
fun AiScreen(viewModel: AppViewModel) {
    val aiResponse by viewModel.aiResponse.collectAsState()
    val aiLoading by viewModel.aiLoading.collectAsState()

    // 10 Requested AI features
    val aiFeatures = listOf(
        "توقع الإيرادات للربع القادم" to "تحليل وتوقع إجمالي الإيرادات المستقبلية استناداً لعقود المشاريع الحالية والتدفقات المالية السابقة.",
        "توقع الأرباح التشغيلية" to "توقع صافي الأرباح القادمة مع تقديم توصيات لخفض الهدر التشغيلي.",
        "توقع المبيعات وحجم الصفقات" to "توقع حجم المبيعات المتوقع إغلاقها من خط أنابيب العملاء المحتملين Leads.",
        "اقتراح أسعار الخدمات الهندسية" to "اقتراح أسعار تنافسية ومربحة لخدمات التصميم، الإشراف، والاستشارات الهندسية.",
        "تحليل سلوكيات العملاء" to "تحليل مدى التزام العملاء بالدفع وتحديد فئات العملاء الأكثر ربحية لتركيز التسويق عليهم.",
        "تحليل مخاطر التدفقات النقدية" to "كشف وتوقع مخاطر نقص السيولة وإدارة الديون المعلقة وتأخر المستحقات.",
        "اقتراح الخدمات الأكثر طلباً" to "اقتراح وتصميم خدمات هندسية جديدة ذات ربحية عالية بناءً على توجهات السوق.",
        "توصيات زيادة الربحية" to "توصيات ذكية فورية لزيادة هوامش الأرباح وتقليل التكاليف التشغيلية بنسبة 15-20٪.",
        "كشف الخلل والأنماط الشاذة" to "فحص المصروفات والمدفوعات لكشف أي أنماط غير طبيعية أو تكاليف غير مبررة.",
        "توليد تقرير ذكي شامل" to "توليد تقرير ذكي كامل يعكس الحالة المالية الشاملة ويقدم نصائح لصناع القرار بالشركة."
    )

    var selectedFeature by remember { mutableStateOf(aiFeatures[0]) }

    // On initial view, load the first prediction
    LaunchedEffect(Unit) {
        viewModel.requestAiInsight(selectedFeature.first)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Header with sparkling visual effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "مساعد الذكاء الاصطناعي التوليدي",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right
                    )
                    Text(
                        text = "مدعوم بنموذج Gemini 3.5 Flash لتحليل البيانات ورفع الأرباح",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Right
                    )
                }
            }
        }

        // Horizontal Selection Row for the 10 smart AI features
        Text(
            text = "اختر المحرك التحليلي للذكاء الاصطناعي",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Right
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(aiFeatures) { feature ->
                val isSelected = selectedFeature.first == feature.first
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedFeature = feature
                        viewModel.requestAiInsight(feature.first)
                    },
                    label = {
                        Text(
                            text = feature.first,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        // Active Feature Details
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = selectedFeature.first,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = selectedFeature.second,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Right
                )
            }
        }

        // Dynamic Response Box with scroll and loading indicators
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (aiLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "جاري الاتصال بـ Gemini وتحليل الجداول والنسب المالية...",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "مستشار الأرباح الذكي (إثراء)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Render AI text
                        Text(
                            text = aiResponse,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
