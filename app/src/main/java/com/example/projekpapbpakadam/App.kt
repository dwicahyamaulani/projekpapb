package com.example.projekpapbpakadam

import android.app.Application
import androidx.room.Room
import com.example.projekpapbpakadam.data.local.AppDatabase
import com.example.projekpapbpakadam.data.repository.BudgetRepository
import com.example.projekpapbpakadam.data.repository.BudgetRepositoryImpl
import com.example.projekpapbpakadam.data.repository.ExpenseRepository
import com.example.projekpapbpakadam.data.repository.ExpenseRepositoryImpl
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class App : Application() {

    lateinit var database: AppDatabase
    lateinit var repository: ExpenseRepository
    lateinit var budgetRepository: BudgetRepository   // ⬅️ TAMBAH INI

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "pocketspends.db"
        ).build()

        // ✅ Firebase instances
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        // ✅ Expense repo (butuh auth + firestore)
        repository = ExpenseRepositoryImpl(
            dao = database.expenseDao(),
            auth = auth,
            firestore = firestore
        )

        // ✅ Budget repo (lokal)
        budgetRepository = BudgetRepositoryImpl(
            dao = database.budgetDao()
        )
    }
}
