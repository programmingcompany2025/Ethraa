package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String,
    val rating: Int = 5, // 1 to 5 stars
    val category: String = "VIP", // VIP, Regular, Corporate, New
    val dues: Double = 0.0, // المستحقات
    val totalPaid: Double = 0.0, // المدفوعات
    val registrationDate: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val clientId: Long,
    val clientName: String,
    val budget: Double, // ميزانية المشروع
    val cost: Double = 0.0, // تكلفة التنفيذ
    val status: String = "نشط", // نشط, منتهي, معلق
    val completionPercentage: Int = 0, // نسبة الإنجاز
    val stages: String = "التخطيط", // مراحل التنفيذ (التخطيط، التصميم، التطوير، التسليم)
    val notes: String = "",
    val creationDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val projectId: Long = 0,
    val projectName: String = "",
    val clientId: Long,
    val clientName: String,
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val status: String = "غير مدفوع", // مدفوع, غير مدفوع, مدفوع جزئياً
    val type: String = "فاتورة", // عرض سعر, عقد, فاتورة, سند قبض, سند صرف
    val notes: String = ""
)

@Entity(tableName = "revenues")
data class Revenue(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val source: String, // مصدر الإيراد
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val category: String = "رسوم تصميم", // رسوم خدمات, رسوم تصميم, رسوم استشارات, اشتراكات, رسوم إضافية
    val notes: String = ""
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // رواتب, إيجارات, فواتير خدمات, معدات, تسويق, ضرائب, مصروفات متكررة
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "leads")
data class Lead(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String,
    val status: String = "جديد", // جديد, تم التواصل, تم تقديم عرض, مغلق رابح, مغلق خاسر
    val source: String = "وسائل التواصل", // وسائل التواصل, إحالة, حملة إعلانية
    val estimatedValue: Double = 0.0,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "campaigns")
data class MarketingCampaign(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val budget: Double,
    val leadsCount: Int = 0,
    val salesGenerated: Double = 0.0,
    val startDate: Long = System.currentTimeMillis(),
    val status: String = "نشطة", // نشطة, منتهية
    val notes: String = ""
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String = ""
)
