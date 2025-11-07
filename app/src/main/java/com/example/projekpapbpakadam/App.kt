package com.example.projekpapbpakadam

import android.app.Application
import androidx.room.Room
import com.example.projekpapbpakadam.data.local.AppDatabase
import com.example.projekpapbpakadam.data.repository.ExpenseRepository
import com.example.projekpapbpakadam.data.repository.ExpenseRepositoryImpl
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class App : Application() {

    // Singleton yang bisa diambil dari Activity/VM
    lateinit var repository: ExpenseRepository
        private set

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        // Room DB + DAO
        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "expenses.db"
        ).fallbackToDestructiveMigration().build()
        val dao = db.expenseDao()

        // Firebase
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        repository = ExpenseRepositoryImpl(
            dao = dao,
            firestore = firestore,
            auth = auth
        )
    }
}
