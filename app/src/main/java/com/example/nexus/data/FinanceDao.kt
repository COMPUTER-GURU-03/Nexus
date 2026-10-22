package com.example.nexus.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nexus.model.FinanceBudget
import com.example.nexus.model.FinanceTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    // Transactions
    @Query("SELECT * FROM finance_transactions ORDER BY date DESC, id DESC")
    fun getAllTransactions(): Flow<List<FinanceTransaction>>

    @Query("SELECT * FROM finance_transactions WHERE date >= :startTimestamp AND date <= :endTimestamp ORDER BY date DESC")
    fun getTransactionsInRange(startTimestamp: Long, endTimestamp: Long): Flow<List<FinanceTransaction>>

    @Query("SELECT * FROM finance_transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): FinanceTransaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: FinanceTransaction): Long

    @Update
    suspend fun updateTransaction(transaction: FinanceTransaction)

    @Delete
    suspend fun deleteTransaction(transaction: FinanceTransaction)

    @Query("DELETE FROM finance_transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    // Budgets
    @Query("SELECT * FROM finance_budgets WHERE monthYear = :monthYear")
    fun getBudgetsForMonth(monthYear: String): Flow<List<FinanceBudget>>

    @Query("SELECT * FROM finance_budgets ORDER BY monthYear DESC")
    fun getAllBudgets(): Flow<List<FinanceBudget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: FinanceBudget): Long

    @Update
    suspend fun updateBudget(budget: FinanceBudget)

    @Delete
    suspend fun deleteBudget(budget: FinanceBudget)

    @Query("DELETE FROM finance_budgets WHERE id = :id")
    suspend fun deleteBudgetById(id: Long)
}
