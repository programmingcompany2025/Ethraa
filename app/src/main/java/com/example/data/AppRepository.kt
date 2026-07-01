package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    // Clients
    val allClients: Flow<List<Client>> = appDao.getAllClients()
    suspend fun insertClient(client: Client): Long = appDao.insertClient(client)
    suspend fun updateClient(client: Client) = appDao.updateClient(client)
    suspend fun deleteClient(client: Client) = appDao.deleteClient(client)
    suspend fun getClientById(id: Long): Client? = appDao.getClientById(id)

    // Projects
    val allProjects: Flow<List<Project>> = appDao.getAllProjects()
    suspend fun insertProject(project: Project): Long = appDao.insertProject(project)
    suspend fun updateProject(project: Project) = appDao.updateProject(project)
    suspend fun deleteProject(project: Project) = appDao.deleteProject(project)
    suspend fun getProjectById(id: Long): Project? = appDao.getProjectById(id)

    // Invoices
    val allInvoices: Flow<List<Invoice>> = appDao.getAllInvoices()
    suspend fun insertInvoice(invoice: Invoice): Long = appDao.insertInvoice(invoice)
    suspend fun updateInvoice(invoice: Invoice) = appDao.updateInvoice(invoice)
    suspend fun deleteInvoice(invoice: Invoice) = appDao.deleteInvoice(invoice)

    // Revenues
    val allRevenues: Flow<List<Revenue>> = appDao.getAllRevenues()
    suspend fun insertRevenue(revenue: Revenue): Long = appDao.insertRevenue(revenue)
    suspend fun deleteRevenue(revenue: Revenue) = appDao.deleteRevenue(revenue)

    // Expenses
    val allExpenses: Flow<List<Expense>> = appDao.getAllExpenses()
    suspend fun insertExpense(expense: Expense): Long = appDao.insertExpense(expense)
    suspend fun deleteExpense(expense: Expense) = appDao.deleteExpense(expense)

    // Leads
    val allLeads: Flow<List<Lead>> = appDao.getAllLeads()
    suspend fun insertLead(lead: Lead): Long = appDao.insertLead(lead)
    suspend fun updateLead(lead: Lead) = appDao.updateLead(lead)
    suspend fun deleteLead(lead: Lead) = appDao.deleteLead(lead)

    // Campaigns
    val allCampaigns: Flow<List<MarketingCampaign>> = appDao.getAllCampaigns()
    suspend fun insertCampaign(campaign: MarketingCampaign): Long = appDao.insertCampaign(campaign)
    suspend fun updateCampaign(campaign: MarketingCampaign) = appDao.updateCampaign(campaign)
    suspend fun deleteCampaign(campaign: MarketingCampaign) = appDao.deleteCampaign(campaign)

    // Audit Logs
    val allAuditLogs: Flow<List<AuditLog>> = appDao.getAllAuditLogs()
    suspend fun insertAuditLog(log: AuditLog): Long = appDao.insertAuditLog(log)
    suspend fun clearAuditLogs() = appDao.clearAuditLogs()
}
