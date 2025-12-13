package com.example.projekpapbpakadam.data.repository

import com.example.projekpapbpakadam.data.local.BudgetDao
import com.example.projekpapbpakadam.data.local.BudgetEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class BudgetRepositoryImpl(
    private val dao: BudgetDao
) : BudgetRepository {

    override fun observeCurrentMonth(): Flow<Long> {
        val now = Calendar.getInstance()
        val month = now.get(Calendar.MONTH) + 1
        val year = now.get(Calendar.YEAR)
        val monthKey = "%04d-%02d".format(year, month)


        return dao.observeBudget(monthKey)
            .map { it?.amount ?: 0L }
    }

    override suspend fun setBudget(amount: Long) {
        val now = Calendar.getInstance()
        val month = now.get(Calendar.MONTH) + 1
        val year = now.get(Calendar.YEAR)

        val monthKey = "%04d-%02d".format(year, month)

        val entity = BudgetEntity(
            monthKey = monthKey,
            amount = amount
        )

        dao.insertOrUpdate(entity)
    }
}
