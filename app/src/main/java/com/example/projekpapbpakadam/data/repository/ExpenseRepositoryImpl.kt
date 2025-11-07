package com.example.projekpapbpakadam.data.repository

import com.example.projekpapbpakadam.data.local.ExpenseDao
import com.example.projekpapbpakadam.data.local.ExpenseEntity
import com.example.projekpapbpakadam.data.mappers.toEntity
import com.example.projekpapbpakadam.data.mappers.toRemote
import com.example.projekpapbpakadam.data.remote.ExpenseRemoteDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class ExpenseRepositoryImpl(
    private val dao: ExpenseDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ExpenseRepository {

    private suspend fun uid(): String {
        auth.currentUser?.uid?.let { return it }
        val res = auth.signInAnonymously().await()
        return res.user?.uid ?: error("Anonymous auth failed")
    }

    private suspend fun collection() =
        firestore.collection("users").document(uid()).collection("expenses")

    override fun observeAll(): Flow<List<ExpenseEntity>> = dao.observeAll()

    override suspend fun get(id: String): ExpenseEntity? = dao.getById(id)

    override suspend fun addOrUpdate(entity: ExpenseEntity) {
        dao.upsert(
            entity.copy(
                synced = false, // <-- was isSynced
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun delete(id: String) {
        dao.getById(id)?.let { dao.delete(it) }
        runCatching { collection().document(id).delete().await() }
    }

    override suspend fun sync() {
        val col = collection() // users/{uid}/expenses

        val pending = dao.getBySyncStatus(false)
        for (e in pending) {
            try {
                col.document(e.id).set(e.toRemote()).await()
                dao.upsert(e.copy(synced = true))       // tandai sukses HANYA jika upload ok
            } catch (t: Throwable) {
                android.util.Log.w("SYNC", "Push fail id=${e.id}: ${t.message}")
            }
        }

        try {
            val snap = col.get().await()
            for (doc in snap.documents) {
                val remote = doc.toObject(ExpenseRemoteDto::class.java) ?: continue
                val local = dao.getById(remote.id)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    dao.upsert(remote.toEntity(local?.localPhotoPath))
                }
            }
        } catch (t: Throwable) {
            android.util.Log.w("SYNC", "Pull fail: ${t.message}")
        }
    }

}
