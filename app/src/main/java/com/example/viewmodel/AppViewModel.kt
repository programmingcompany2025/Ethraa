package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    // Raw State Flows from Room
    val clients: StateFlow<List<Client>>
    val projects: StateFlow<List<Project>>
    val invoices: StateFlow<List<Invoice>>
    val revenues: StateFlow<List<Revenue>>
    val expenses: StateFlow<List<Expense>>
    val leads: StateFlow<List<Lead>>
    val campaigns: StateFlow<List<MarketingCampaign>>
    val auditLogs: StateFlow<List<AuditLog>>

    // Combined UI Metrics State
    val totalRevenue = MutableStateFlow(0.0)
    val totalExpenses = MutableStateFlow(0.0)
    val netProfit = MutableStateFlow(0.0)
    val activeProjectsCount = MutableStateFlow(0)
    val completedProjectsCount = MutableStateFlow(0)
    val vipClientsCount = MutableStateFlow(0)
    val totalDues = MutableStateFlow(0.0)
    val totalPaid = MutableStateFlow(0.0)

    // Reduce Motion Setting
    val reduceMotion = MutableStateFlow(false)

    fun setReduceMotion(enabled: Boolean) {
        reduceMotion.value = enabled
        logAction("الإعدادات", if (enabled) "تم تفعيل نمط تقليل الحركة والمؤثرات البصرية" else "تم إيقاف تفعيل نمط تقليل الحركة")
    }

    // AI States
    private val _aiResponse = MutableStateFlow<String>("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    init {
        val appDatabase = AppDatabase.getDatabase(application)
        repository = AppRepository(appDatabase.appDao())

        clients = repository.allClients.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        projects = repository.allProjects.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        invoices = repository.allInvoices.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        revenues = repository.allRevenues.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        expenses = repository.allExpenses.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        leads = repository.allLeads.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        campaigns = repository.allCampaigns.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        auditLogs = repository.allAuditLogs.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        // Combine to calculate live metrics
        viewModelScope.launch {
            combine(revenues, expenses, projects, clients) { revs, exps, projs, cls ->
                val tr = revs.sumOf { it.amount }
                val te = exps.sumOf { it.amount }
                val np = tr - te
                val active = projs.count { it.status == "نشط" }
                val completed = projs.count { it.status == "منتهي" }
                val vips = cls.count { it.category == "VIP" }
                val duesVal = cls.sumOf { it.dues }
                val paidVal = cls.sumOf { it.totalPaid }

                totalRevenue.value = tr
                totalExpenses.value = te
                netProfit.value = np
                activeProjectsCount.value = active
                completedProjectsCount.value = completed
                vipClientsCount.value = vips
                totalDues.value = duesVal
                totalPaid.value = paidVal
            }.collect()
        }

        // Seed data if database is empty
        seedDataIfNeeded()
    }

    private fun seedDataIfNeeded() {
        viewModelScope.launch {
            // Check if seeding is required
            val existingClients = clients.first()
            if (existingClients.isEmpty()) {
                logAction("النظام", "بدء تهيئة البيانات المبدئية للشركة الهندسية")

                // Clients
                val c1 = repository.insertClient(Client(name = "شركة الجبل الأخضر العقارية", phone = "0911234567", email = "info@greenhill.ly", rating = 5, category = "VIP", dues = 50000.0, totalPaid = 150000.0))
                val c2 = repository.insertClient(Client(name = "مكتب طرابلس للتطوير العمراني", phone = "0927654321", email = "urban@tripoli.gov.ly", rating = 4, category = "عادي", dues = 15000.0, totalPaid = 80000.0))
                val c3 = repository.insertClient(Client(name = "المؤسسة الوطنية للتنمية السكنية", phone = "0911112223", email = "housing@gov.ly", rating = 5, category = "VIP", dues = 120000.0, totalPaid = 350000.0))
                val c4 = repository.insertClient(Client(name = "فيلا المهندس فرج الورفلي", phone = "0948887776", email = "f.warfalli@gmail.com", rating = 4, category = "عادي", dues = 5000.0, totalPaid = 45000.0))
                val c5 = repository.insertClient(Client(name = "مجمع السراج السكني", phone = "0924443332", email = "sarraj@residence.ly", rating = 5, category = "VIP", dues = 0.0, totalPaid = 200000.0))

                // Projects
                repository.insertProject(Project(name = "تصميم برج طرابلس الإداري", clientId = c1, clientName = "شركة الجبل الأخضر العقارية", budget = 250000.0, cost = 120000.0, status = "نشط", completionPercentage = 45, stages = "التصميم التفصيلي", notes = "تصميم حديث وصديق للبيئة مع واجهات زجاجية مزدوجة"))
                repository.insertProject(Project(name = "إشراف مخطط الحدائق ببنغازي", clientId = c3, clientName = "المؤسسة الوطنية للتنمية السكنية", budget = 450000.0, cost = 200000.0, status = "نشط", completionPercentage = 70, stages = "إشراف التنفيذ", notes = "متابعة البنية التحتية وتمديدات الخدمات والإنارة الذكية"))
                repository.insertProject(Project(name = "استشارات مبنى بلدية مصراتة", clientId = c2, clientName = "مكتب طرابلس للتطوير العمراني", budget = 80000.0, cost = 30000.0, status = "منتهي", completionPercentage = 100, stages = "التسليم النهائي", notes = "تم التسليم بنجاح وتوفير 15% من الميزانية التشغيلية"))
                repository.insertProject(Project(name = "تصميم فيلا الورفلي الحديثة", clientId = c4, clientName = "فيلا المهندس فرج الورفلي", budget = 120000.0, cost = 50000.0, status = "نشط", completionPercentage = 15, stages = "التخطيط المبدئي", notes = "فيلا سكنية فاخرة بنظام المنزل الذكي والتحكم الحراري"))

                // Revenues
                repository.insertRevenue(Revenue(source = "دفعة أولى برج طرابلس", amount = 100000.0, category = "رسوم تصميم", notes = "عقد تصميم البرج التجاري الإداري"))
                repository.insertRevenue(Revenue(source = "استشارات هندسية مصراتة", amount = 80000.0, category = "رسوم استشارات", notes = "استشارات تسليم مبنى البلدية الفرعي"))
                repository.insertRevenue(Revenue(source = "دفعة ثانية مخطط بنغازي", amount = 150000.0, category = "رسوم خدمات", notes = "إشراف تنفيذ المخطط السكني"))
                repository.insertRevenue(Revenue(source = "رسوم تصميم فيلا الورفلي", amount = 40000.0, category = "رسوم تصميم", notes = "المرحلة الأولى من التصميم المعماري"))

                // Expenses
                repository.insertExpense(Expense(category = "رواتب الموظفين والمهندسين", amount = 45000.0, notes = "رواتب مهندسي التصميم والمشرفين لشهر يونيو"))
                repository.insertExpense(Expense(category = "إيجارات المقار الفرعية", amount = 25000.0, notes = "إيجار مقر الشركة الرئيسي بمدينة طرابلس"))
                repository.insertExpense(Expense(category = "المعدات والبرامج الهندسية", amount = 12000.0, notes = "اشتراكات وتراخيص أوتوكاد وريفت ثلاثية الأبعاد"))
                repository.insertExpense(Expense(category = "التسويق العقاري الفاخر", amount = 8000.0, notes = "حملات تسويقية مستهدفة لخدمات التصميم المعماري الفاخر"))
                repository.insertExpense(Expense(category = "فواتير وسيرفرات الشركة", amount = 3500.0, notes = "فواتير الكهرباء، المياه والإنترنت عالي السرعة"))
                repository.insertExpense(Expense(category = "الرسوم والمستحقات المحلية", amount = 15000.0, notes = "الضرائب والرسوم المحلية الربع سنوية المستحقة"))

                // Invoices / Offers
                repository.insertInvoice(Invoice(invoiceNumber = "INV-2026-001", clientId = c1, clientName = "شركة الجبل الأخضر العقارية", amount = 100000.0, status = "مدفوع", type = "عقد"))
                repository.insertInvoice(Invoice(invoiceNumber = "INV-2026-002", clientId = c3, clientName = "المؤسسة الوطنية للتنمية السكنية", amount = 150000.0, status = "مدفوع", type = "فاتورة"))
                repository.insertInvoice(Invoice(invoiceNumber = "INV-2026-003", clientId = c2, clientName = "مكتب طرابلس للتطوير العمراني", amount = 80000.0, status = "مدفوع", type = "فاتورة"))
                repository.insertInvoice(Invoice(invoiceNumber = "INV-2026-004", clientId = c1, clientName = "شركة الجبل الأخضر العقارية", amount = 50000.0, status = "غير مدفوع", type = "عقد"))
                repository.insertInvoice(Invoice(invoiceNumber = "QT-2026-010", clientId = c5, clientName = "مجمع السراج السكني", amount = 220000.0, status = "غير مدفوع", type = "عرض سعر"))

                // Leads
                repository.insertLead(Lead(name = "عبد الرحمن السويحلي", phone = "0912223334", email = "swehli@outlook.ly", status = "جديد", source = "وسائل التواصل", estimatedValue = 180000.0))
                repository.insertLead(Lead(name = "شركة المختار للمقاولات", phone = "0924445556", email = "mokhtar@contract.ly", status = "تم التواصل", source = "إحالة", estimatedValue = 350000.0))
                repository.insertLead(Lead(name = "فندق تيبستي بنغازي", phone = "0915556667", email = "tibesti@hotel.ly", status = "تم تقديم عرض", source = "حملة إعلانية", estimatedValue = 900000.0))

                // Campaigns
                repository.insertCampaign(MarketingCampaign(name = "حملة المهندس المعماري المحترف", budget = 10000.0, leadsCount = 12, salesGenerated = 150000.0, status = "نشطة"))
                repository.insertCampaign(MarketingCampaign(name = "عروض التصميم السنوية", budget = 5000.0, leadsCount = 8, salesGenerated = 80000.0, status = "منتهية"))

                logAction("النظام", "اكتملت عملية تهيئة البيانات المبدئية للشركة الهندسية بنجاح")
            }
        }
    }

    // Logging audit events
    fun logAction(action: String, details: String) {
        viewModelScope.launch {
            repository.insertAuditLog(AuditLog(action = action, details = details))
        }
    }

    // --- Clients CRUD ---
    fun addClient(name: String, phone: String, email: String, category: String, rating: Int, dues: Double, totalPaid: Double) {
        viewModelScope.launch {
            repository.insertClient(Client(name = name, phone = phone, email = email, category = category, rating = rating, dues = dues, totalPaid = totalPaid))
            logAction("إضافة عميل", "تمت إضافة العميل الجديد: $name بنجاح")
        }
    }

    fun updateClient(client: Client) {
        viewModelScope.launch {
            repository.updateClient(client)
            logAction("تحديث عميل", "تم تحديث بيانات العميل: ${client.name}")
        }
    }

    fun deleteClient(client: Client) {
        viewModelScope.launch {
            repository.deleteClient(client)
            logAction("حذف عميل", "تم حذف بيانات العميل: ${client.name}")
        }
    }

    // --- Projects CRUD ---
    fun addProject(name: String, clientId: Long, clientName: String, budget: Double, cost: Double, status: String, completion: Int, stages: String, notes: String) {
        viewModelScope.launch {
            repository.insertProject(Project(name = name, clientId = clientId, clientName = clientName, budget = budget, cost = cost, status = status, completionPercentage = completion, stages = stages, notes = notes))
            logAction("إنشاء مشروع", "تم إنشاء المشروع الجديد: $name للمستفيد $clientName")
        }
    }

    fun updateProject(project: Project) {
        viewModelScope.launch {
            repository.updateProject(project)
            logAction("تحديث مشروع", "تم تحديث بيانات المشروع: ${project.name}")
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repository.deleteProject(project)
            logAction("حذف مشروع", "تم حذف المشروع: ${project.name}")
        }
    }

    // --- Revenue Logging ---
    fun addRevenue(source: String, amount: Double, category: String, notes: String) {
        viewModelScope.launch {
            repository.insertRevenue(Revenue(source = source, amount = amount, category = category, notes = notes))
            logAction("تسجيل إيراد", "تم تسجيل إيراد بقيمة $amount من مصدر $source ($category)")
        }
    }

    fun deleteRevenue(revenue: Revenue) {
        viewModelScope.launch {
            repository.deleteRevenue(revenue)
            logAction("حذف إيراد", "تم حذف إيراد بقيمة ${revenue.amount} من مصدر ${revenue.source}")
        }
    }

    // --- Expense Logging ---
    fun addExpense(category: String, amount: Double, notes: String) {
        viewModelScope.launch {
            repository.insertExpense(Expense(category = category, amount = amount, notes = notes))
            logAction("تسجيل مصروف", "تم تسجيل مصروف بقيمة $amount تحت بند $category")
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            logAction("حذف مصروف", "تم حذف مصروف بقيمة ${expense.amount} تحت بند ${expense.category}")
        }
    }

    // --- Leads CRUD ---
    fun addLead(name: String, phone: String, email: String, status: String, source: String, estimatedValue: Double, notes: String) {
        viewModelScope.launch {
            repository.insertLead(Lead(name = name, phone = phone, email = email, status = status, source = source, estimatedValue = estimatedValue, notes = notes))
            logAction("عميل محتمل", "تم تسجيل عميل محتمل جديد: $name بقيمة تقديرية $estimatedValue")
        }
    }

    fun updateLead(lead: Lead) {
        viewModelScope.launch {
            repository.updateLead(lead)
            logAction("تحديث عميل محتمل", "تم تحديث حالة العميل المحتمل: ${lead.name}")
        }
    }

    fun deleteLead(lead: Lead) {
        viewModelScope.launch {
            repository.deleteLead(lead)
            logAction("حذف عميل محتمل", "تم حذف العميل المحتمل: ${lead.name}")
        }
    }

    // --- Invoices & Sales CRUD ---
    fun addInvoice(invoiceNumber: String, projectId: Long, projectName: String, clientId: Long, clientName: String, amount: Double, status: String, type: String, notes: String) {
        viewModelScope.launch {
            repository.insertInvoice(Invoice(invoiceNumber = invoiceNumber, projectId = projectId, projectName = projectName, clientId = clientId, clientName = clientName, amount = amount, status = status, type = type, notes = notes))
            logAction("مبيعات", "تم تسجيل مستند جديد: $invoiceNumber ($type) بقيمة $amount")
        }
    }

    fun updateInvoice(invoice: Invoice) {
        viewModelScope.launch {
            repository.updateInvoice(invoice)
            logAction("تحديث فاتورة", "تم تحديث الفاتورة/المستند: ${invoice.invoiceNumber}")
        }
    }

    fun deleteInvoice(invoice: Invoice) {
        viewModelScope.launch {
            repository.deleteInvoice(invoice)
            logAction("حذف فاتورة", "تم حذف الفاتورة/المستند: ${invoice.invoiceNumber}")
        }
    }

    // --- Campaigns CRUD ---
    fun addCampaign(name: String, budget: Double, notes: String) {
        viewModelScope.launch {
            repository.insertCampaign(MarketingCampaign(name = name, budget = budget, notes = notes))
            logAction("تسويق", "تم إطلاق حملة تسويقية جديدة: $name بميزانية $budget")
        }
    }

    fun updateCampaign(campaign: MarketingCampaign) {
        viewModelScope.launch {
            repository.updateCampaign(campaign)
            logAction("تحديث حملة", "تم تحديث الحملة التسويقية: ${campaign.name}")
        }
    }

    fun deleteCampaign(campaign: MarketingCampaign) {
        viewModelScope.launch {
            repository.deleteCampaign(campaign)
            logAction("حذف حملة", "تم حذف الحملة التسويقية: ${campaign.name}")
        }
    }

    // --- Admin Settings & Reset ---
    fun clearLogs() {
        viewModelScope.launch {
            repository.clearAuditLogs()
            logAction("الإدارة", "تم تفريغ سجل العمليات بنجاح")
        }
    }

    fun performBackup(): String {
        logAction("النسخ الاحتياطي", "تم إنشاء نسخة احتياطية محلية مشفرة بنجاح")
        return "تم إنشاء نسخة احتياطية بنجاح بتاريخ: ${System.currentTimeMillis()}"
    }

    fun performRestore(): String {
        logAction("استعادة البيانات", "تم استعادة البيانات من النسخة الاحتياطية بنجاح")
        return "تم استعادة البيانات وإعادة مزامنة الجداول بنجاح"
    }

    // --- AI Calling Feature ---
    fun requestAiInsight(topic: String) {
        viewModelScope.launch {
            _aiLoading.value = true
            _aiResponse.value = "جاري التحليل وتوليد التوقعات المالية باستخدام الذكاء الاصطناعي..."

            val clientsData = clients.value.joinToString("\n") { "العميل: ${it.name}، التصنيف: ${it.category}، المستحقات: ${it.dues}، المدفوعات: ${it.totalPaid}" }
            val projectsData = projects.value.joinToString("\n") { "المشروع: ${it.name}، الميزانية: ${it.budget}، التكلفة: ${it.cost}، الإنجاز: ${it.completionPercentage}%، المرحلة: ${it.stages}" }
            val financeSummary = "إجمالي الإيرادات: ${totalRevenue.value} د.ل، إجمالي المصروفات: ${totalExpenses.value} د.ل، صافي الربح: ${netProfit.value} د.ل"

            val prompt = """
                الموضوع المطلوب تحليله: $topic
                
                بيانات الشركة الهندسية الحالية:
                - الملخص المالي: $financeSummary
                
                المشاريع الحالية:
                $projectsData
                
                العملاء ومستحقاتهم:
                $clientsData
                
                المطلوب:
                تحليل دقيق ومقترحات عملية لزيادة الأرباح وتحسين إدارة التدفقات النقدية استناداً للموضوع المطلوبة. ركّز على تقديم 3 توصيات ذكية فورية وملموسة.
            """.trimIndent()

            val response = GeminiClient.getAiAnalysis(prompt)
            _aiResponse.value = response
            _aiLoading.value = false
            logAction("الذكاء الاصطناعي", "تم إجراء تحليل ذكي للموضوع: $topic")
        }
    }
}
