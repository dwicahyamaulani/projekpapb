# PocketSpends (Expense Tracker with Camera & Sync (PAPB Final Project))

PocketSpends adalah aplikasi pencatat pengeluaran sederhana berbasis **Android Jetpack Compose**, dikembangkan sebagai proyek akhir mata kuliah **Pemrograman Aplikasi Perangkat Bergerak (PAPB)**.
Aplikasi ini memanfaatkan **Room Database** untuk penyimpanan lokal dan **Firebase Firestore** untuk sinkronisasi cloud, dengan dukungan **kamera untuk foto struk** dan **navigasi antar layar**.


## Fitur Utama

✅ **Tambah / Edit / Hapus Pengeluaran (CRUD)** <br>
✅ **Ambil Foto Struk** menggunakan kamera perangkat <br>
✅ **Sinkronisasi dengan Tombol ke Firebase Firestore** <br>
✅ **Tampilan dinamis menggunakan Jetpack Compose** <br>
✅ **Offline-first dengan Room Database** <br>
✅ **Arsitektur MVVM + Repository Pattern**

## Struktur Proyek

```
app/
├── src/main/
│   ├── java/com/example/projekpapbpakadam/
│   │   ├── App.kt                     # Application class untuk inisialisasi database & repo
│   │   ├── MainActivity.kt            # Entry point aplikasi
│   │   │
│   │   ├── core/
│   │   │   └── camera/
│   │   │       └── CameraUtils.kt
|   |   |   └── location/
│   │   │       └── LocationUtils.kt
│   │   │
│   │   ├── data/
│   │   │   ├── local/
│   │   │   │   ├── AppDatabase.kt     # Database Room
│   │   │   │   ├── ExpenseDao.kt      # DAO pengeluaran
│   │   │   │   └── ExpenseEntity.kt   # Entity Room
│   │   │   │
│   │   │   ├── remote/
│   │   │   │   ├── ExpenseRemoteDto.kt  # Model untuk Firestore
│   │   │   │   └── ExpenseMappers.kt          # Konversi Entity <-> DTO
│   │   │   │
│   │   │   └── repository/
│   │   │       ├── ExpenseRepository.kt
│   │   │       └── ExpenseRepositoryImpl.kt
│   │   │
│   │   ├── uii/
│   │   │   ├── addEdit/
│   │   │   │   ├── AddEditScreen.kt
│   │   │   │   └── AddEditViewModel.kt
│   │   │   │
│   │   │   ├── home/
│   │   │   │   ├── HomeScreen.kt
│   │   │   │   └── HomeViewModel.kt
│   │   │   │
│   │   │   ├── detail/
│   │   │   │   └── DetailScreen.kt
│   │   │   │
│   │   │   ├── settings/
│   │   │   │   └── SettingsScreen.kt
│   │   │   │
│   │   │   └── nav/
│   │   │       ├── AppNavGraph.kt
│   │   │       └── Routes.kt
│   │   │
│   │   └── ui/theme/
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│   │
│   └── AndroidManifest.xml
│
└── google-services.json               # Konfigurasi Firebase
```


## Teknologi yang Digunakan

| Komponen             | Teknologi                             |
| -------------------- | ------------------------------------- |
| UI                   | Jetpack Compose + Material 3          |
| Database Lokal       | Room                                  |
| Database Cloud       | Firebase Firestore                    |
| Dependency Injection | Manual (ViewModel Factory)            |
| Kamera               | ActivityResultContracts.TakePicture() |
| Arsitektur           | MVVM + Repository Pattern             |
| Bahasa               | Kotlin                                |


## Developer Notes

* Struktur modular agar mudah diperluas (mis. tambah kategori, statistik, dsb).
* Semua operasi database dijalankan di coroutine `viewModelScope`.
* Firestore diakses lewat user anonim (`FirebaseAuth.getInstance().currentUser`).
* Sinkronisasi manual disediakan agar pengguna bisa kontrol upload.

## Kontributor

**Aufii Fathin Nabila** – 235150200111002 <br>
**Dwi Cahya Maulani** – 235150201111003 <br>
Fakultas Ilmu Komputer, Universitas Brawijaya

## Lisensi

MIT License © 2025

