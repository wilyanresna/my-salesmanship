# Product Requirements Document (PRD)
# Salesmanship — Aplikasi Distribusi Rokok

**Versi:** 1.3
**Tanggal:** 2026-06-29
**Status:** In Review — Data Model Finalized

---

## 1. Latar Belakang

Salesmanship adalah aplikasi distribusi rokok yang dirancang untuk membantu perusahaan distributor mengelola alur distribusi produk dari gudang ke outlet secara efisien. Aplikasi ini merupakan rebuild dari sistem yang pernah dikembangkan sebelumnya, dengan tujuan meningkatkan kualitas kode, modernisasi tech stack, dan memperkuat fondasi arsitektur agar lebih maintainable ke depannya.

Tantangan utama yang dijawab oleh aplikasi ini adalah kondisi infrastruktur jaringan seluler di Indonesia yang belum merata, sehingga aplikasi Android milik Sales **wajib bersifat offline-first**.

---

## 2. Tujuan Produk

1. Menyediakan platform terpadu bagi Supervisor (Spv) dan Salesman (Sales) untuk mengelola distribusi rokok ke outlet.
2. Mendukung operasional Sales di lapangan tanpa ketergantungan penuh pada koneksi internet.
3. Memberikan visibilitas data kunjungan dan penjualan secara real-time kepada Supervisor melalui web dashboard.

---

## 3. Pengguna

| Role | Platform | Deskripsi |
|---|---|---|
| **Supervisor (Spv)** | Web Frontend (Angular) | Mengelola data master, stok, dan memantau hasil kunjungan Sales |
| **Salesman (Sales)** | Android App (Kotlin) | Melakukan kunjungan ke outlet, mencatat transaksi penjualan di lapangan |

---

## 4. Hierarki Bisnis

```
Area
└── Territory           <- dikepalai oleh Supervisor (Spv)
    └── District        <- dipegang oleh tepat satu Salesman (relasi 1:1)
        └── Route       <- merepresentasikan hari kunjungan (Senin = Route 1, dst.)
            └── Outlet  <- tujuan kunjungan Sales, 1 outlet hanya ada di 1 Route
```

> **Catatan:** Terdapat role **Admin** di atas Spv yang mengelola Area, Produk, dan data master distribusi lainnya. Role ini **belum masuk scope MVP** dan akan diimplementasikan pada iterasi berikutnya.

### 4.1 Business Rules

| # | Rule |
|---|---|
| BR-01 | Satu Sales hanya boleh memegang tepat **satu** District (relasi 1:1). |
| BR-02 | Stok rokok diinput dan dilacak **per SKU/Produk**, tidak per bundel atau karton. |
| BR-03 | Barcode outlet menggunakan format **Code-128**. |
| BR-04 | Tidak ada batas maksimal jumlah outlet per Route secara sistem, namun Spv bertanggung jawab membatasi jumlah outlet agar Sales tidak bekerja lebih dari **9 jam/hari**. |
| BR-05 | Sales **diperbolehkan** mengunjungi outlet di luar Route hari ini. Kunjungan tersebut ditandai `is_out_of_route = true` pada record `Visit`. |
| BR-06 | Satuan stok rokok menggunakan **4 level UOM**: `DUS → BAL → SLOP → BUNGKUS`. Setiap produk menyimpan faktor konversi antar satuan. |
| BR-07 | Satu siklus distribusi = satu **Week Period** (minggu kalender). Semua data (stok, target, callsheet) terikat ke satu `WeekPeriod`. |
| BR-08 | Target penjualan ditetapkan **per Route per Produk per Week** oleh Spv. Sales melihat target ini di Dashboard. |

---

## 5. Fitur — MVP

Scope MVP mengacu pada alur bisnis yang terdefinisi di README. Tidak ada fitur tambahan pada tahap ini.

### 5.1 Modul Supervisor (Web Frontend — Angular)

#### 5.1.1 Manajemen Data Master Outlet
- Input data outlet: nama, alamat, nama pemilik, no. telepon, titik lokasi (koordinat), dan kode barcode.
- Penugasan outlet ke Territory -> District -> Route.

#### 5.1.2 Manajemen Data Sales
- Input data Sales: nama, NIK, username, password awal, alamat, no. telepon.
- Assignment Sales ke District.

#### 5.1.3 Manajemen Target Mingguan
- Spv menginput **target penjualan per Route per Produk** untuk suatu Week Period.
- Target ditampilkan di dashboard Sales (sebagai acuan) dan di dashboard Spv (untuk monitoring achievement).

#### 5.1.4 Persiapan Stok Distribusi (Week Period)
- Spv menginput alokasi stok per SKU yang akan dibawa Sales untuk suatu **Week Period**.
- Stok diinput dalam multi-satuan: **DUS, BAL, SLOP, BUNGKUS** sesuai UOM Produk.
- Tidak ada pengelolaan stok gudang pada MVP.
- Stok yang sudah dibuat **tidak dapat diedit** setelah Sales melakukan tarik data (pull).
- Spv dapat membatalkan alokasi stok selama Sales belum melakukan pull.

#### 5.1.5 Monitoring Hasil Kunjungan
- Lihat rekap hasil kunjungan yang telah diupload oleh Sales dari Android.
- Data mencakup: outlet yang dikunjungi, callsheet (stok cek + penjualan), durasi kunjungan, achievement vs target.

#### 5.1.6 Manajemen Data Produk (Seed)
- Data Produk (nama rokok, SKU, harga, UOM konversi) **disediakan melalui database seed** pada MVP.
- CRUD Produk menjadi tanggung jawab role Admin pada iterasi berikutnya.

---

### 5.2 Modul Salesman (Android — Kotlin + Jetpack Compose)

#### 5.2.1 Autentikasi
- Login menggunakan username & password yang disediakan Spv.
- Sales dapat mengubah password sendiri.

#### 5.2.2 Dashboard
- Ringkasan Route yang akan dikunjungi pada Week Period aktif.
- Status kunjungan outlet (sudah/belum dikunjungi) per route/hari.
- Ringkasan stok yang dibawa (per UOM).
- Progress achievement vs target mingguan per produk.

#### 5.2.3 Tarik Data (Pull)
- Sales menarik data dari server untuk Week Period aktif (memerlukan koneksi internet):
  - Data outlet & route
  - Alokasi stok (per SKU, per UOM)
  - Target penjualan per produk
  - History callsheet week sebelumnya (referensi)
- Setelah pull berhasil, data dikunci — tidak bisa ditarik ulang kecuali Spv membatalkan.
- Semua data tersimpan lokal (Room Database) untuk digunakan offline.

#### 5.2.4 Sesi Route Harian (Route Session)
- Sebelum memulai kunjungan, Sales **memulai sesi route** (Start Route):
  - Sistem merekam **waktu mulai** dan **koordinat GPS awal**.
- Setelah selesai, Sales **mengakhiri sesi route** (End Route):
  - Sistem merekam **waktu selesai** dan **koordinat GPS akhir**.
- Data ini digunakan Spv untuk memverifikasi aktivitas Sales di lapangan.

#### 5.2.5 Kunjungan Outlet
- Cari outlet dari daftar (list view) atau **scan barcode Code-128** (kamera).
- Sistem merekam **timestamp mulai kunjungan** (call_start) saat outlet dibuka.
- Kunjungan outlet di luar Route hari ini diperbolehkan dan ditandai `is_out_of_route = true`.

#### 5.2.6 Callsheet Outlet
Callsheet adalah form utama yang diisi Sales di setiap outlet untuk setiap produk:
- **Cek Stok:** Input jumlah stok rokok yang ada di outlet saat dikunjungi.
- **Penjualan (Buy):** Input jumlah yang dijual ke outlet — dibatasi oleh sisa stok yang dibawa.
- **Notasi Distribusi:** Pilih kode notasi (misal: Normal, OOS, Grosir).
- Sistem merekam **timestamp selesai kunjungan** (call_end) saat callsheet di-submit.
- History callsheet minggu lalu ditampilkan sebagai referensi saat mengisi.

#### 5.2.7 Sinkronisasi Data
- Semua data transaksi disimpan lokal (offline-first via Room Database).
- Status sync dilacak per komponen: `PENDING → SYNCING → SYNCED`.
- Background sync otomatis menggunakan **WorkManager** saat koneksi tersedia.
- Sales dapat memicu sync manual.

#### 5.2.8 Pengembalian Barang (Retur)
- Sistem menghitung sisa stok otomatis per SKU per UOM: `qty_returned = qty_initial - qty_sold`.
- Ringkasan retur ditampilkan di Android sebelum Sales kembali ke kantor.
- Data retur ter-upload ke server; Spv konfirmasi fisik secara manual di kantor.

---

### 5.3 Backend (Go + PostgreSQL)

#### 5.3.1 REST API
- Melayani permintaan dari Android App dan Web Frontend.
- Autentikasi berbasis **JWT (JSON Web Token)**.

#### 5.3.2 Endpoint Utama

| Kelompok | Deskripsi |
|---|---|
| Auth | Login, refresh token, ganti password |
| Master Data | CRUD Area, Territory, District, Route, Outlet, User |
| Week Period | Manajemen periode mingguan aktif |
| Produk | Read produk & UOM (data di-seed) |
| Target | Input target per Route per Produk per Week (Spv) |
| Stok | Persiapan alokasi stok per Week & pull oleh Sales |
| Sinkronisasi | Upload batch data kunjungan, callsheet, route session dari Android |
| Laporan | Query hasil kunjungan, callsheet, achievement vs target untuk Web |

---

## 6. Tech Stack

| Layer | Teknologi |
|---|---|
| **Backend** | Go + **Gin** framework |
| **Database** | PostgreSQL |
| **ORM** | **GORM** |
| **Android** | Kotlin + Jetpack Compose |
| **Android Local DB** | Room Database (SQLite) |
| **Android Background Sync** | WorkManager |
| **Android Barcode** | ZXing atau Google ML Kit (Code-128) |
| **Android Location** | Android Location API (FusedLocationProvider) |
| **Web Frontend** | Angular (latest stable) |
| **API Protocol** | REST API |
| **Autentikasi** | JWT (Access Token + Refresh Token) |
| **Deployment** | Docker + Docker Compose di VPS (self-hosted) |
| **Bahasa UI** | Bilingual: Bahasa Indonesia & Inggris (toggle) |
| **Periode Distribusi** | Weekly Period (minggu kalender ISO) |

---

## 7. Arsitektur Sistem

```
+-----------------------------------------------------+
|                  VPS (Self-Hosted)                  |
|                                                     |
|  +--------------+       +------------------------+  |
|  |  Go Backend  |<----->|  PostgreSQL Database   |  |
|  |  (REST API)  |       +------------------------+  |
|  +------+-------+                                   |
|         | Docker Compose                             |
+---------+-------------------------------------------+
          |
    +-----+------+
    |            |
    v            v
+--------+  +--------------+
|Angular |  | Android App  |
|  Web   |  | (Kotlin +    |
| (Spv)  |  |  Compose)    |
+--------+  +------+-------+
                   |
            +------v-------+
            | Room Database|
            |  (Offline)   |
            +--------------+
```

### 7.1 Offline-First Strategy (Android)
1. **Read:** Selalu baca dari Room Database (lokal) terlebih dahulu.
2. **Write:** Semua operasi tulis disimpan lokal, lalu di-queue untuk sync.
3. **Sync:** WorkManager menjalankan sync task saat koneksi tersedia (constraint `NetworkType.CONNECTED`).
4. **Conflict Resolution:** Server sebagai *source of truth* untuk data master; Android sebagai *source of truth* untuk data transaksi lapangan.

---

## 8. Data Model (High-Level)

### 8.1 Entitas Master

| Entitas | Atribut Kunci |
|---|---|
| `Area` | id, name |
| `Territory` | id, area_id, name, supervisor_id |
| `District` | id, territory_id, name, **salesman_id UNIQUE** (1:1 dengan Sales) |
| `Route` | id, district_id, name, day_of_week |
| `Outlet` | id, route_id, name, address, owner_name, phone, lat, lng, barcode (Code-128), outlet_status, call_cycle |
| `User` | id, name, nik, username, password_hash, role (spv/sales), phone, address |
| `Product` | id, name, sku (UNIQUE), price, **uom_bal, uom_slf, uom_bks** (faktor konversi multi-UOM) |
| `WeekPeriod` | id, week_number, year, date_start, date_end, is_active |

### 8.2 Entitas Transaksi

| Entitas | Atribut Kunci |
|---|---|
| `StockAllocation` | id, salesman_id, week_period_id, status (DRAFT/PULLED/CLOSED), created_by |
| `StockAllocationItem` | id, stock_allocation_id, product_id, qty_dus, qty_bal, qty_slf, qty_bks (initial & sold & returned per UOM) |
| `SalesTarget` | id, route_id, product_id, week_period_id, target_qty, created_by |
| `RouteSession` | id, salesman_id, route_id, week_period_id, date, **time_start, time_end**, **lat_start, lng_start, lat_end, lng_end**, sync_status |
| `Visit` | id, outlet_id, salesman_id, route_session_id, **call_start, call_end**, **actual_lat, actual_lng**, **is_out_of_route**, sync_status |
| `Callsheet` | id, visit_id, product_id, **stock_qty** (cek stok di outlet), **buy_qty** (penjualan), price, total_price, notation_code |
| `ReturRecap` | id, stock_allocation_id, product_id, qty_dus, qty_bal, qty_slf, qty_bks (sisa = initial - sold) |

### 8.3 Entitas Referensi (LOV)

| Entitas | Atribut Kunci |
|---|---|
| `NotationCode` | id, code, name, description (notasi distribusi: Normal, OOS, dll.) |
| `OutletStatus` | id, code, name (Aktif, Non-Aktif, dll.) |

---

## 9. Non-Functional Requirements

| NFR | Target |
|---|---|
| **Offline Availability** | 100% operasional saat tanpa internet (kecuali pull stok & sync) |
| **Sync Latency** | Data ter-sync dalam < 30 detik setelah koneksi tersedia |
| **API Response Time** | < 500ms untuk endpoint data master, < 1s untuk endpoint sync |
| **Security** | JWT dengan expiry pendek (15 menit), refresh token (7 hari) |
| **Deployment** | Docker Compose — mudah di-deploy di VPS mana pun |
| **Scalability** | Single-tenant per instance (satu instance = satu distributor) |
| **Bahasa** | UI bilingual Indonesia/Inggris dengan toggle di settings |

---

## 10. Urutan Pengerjaan (Prioritas)

Pengembangan akan dilakukan secara berurutan:

```
Prioritas 1 -> Backend (Go + PostgreSQL + Gin + GORM)
              +-- Setup project structure & Docker Compose
              +-- Database schema design & GORM migrations
              +-- Auth: JWT login, refresh token, ganti password
              +-- API: Master Data (Area, Territory, District, Route, Outlet, User)
              +-- API: WeekPeriod management
              +-- API: Product (read + seed data)
              +-- API: SalesTarget (CRUD by Spv)
              +-- API: StockAllocation (CRUD by Spv, pull by Sales)
              +-- API: Sync endpoint (upload RouteSession, Visit, Callsheet batch)
              +-- API: Laporan (query hasil kunjungan untuk Web)

Prioritas 2 -> Android (Kotlin + Jetpack Compose)
              +-- Setup project + Room + WorkManager + Retrofit
              +-- Auth: login, JWT storage, auto refresh
              +-- Pull data: outlet, stok, target, history callsheet
              +-- Dashboard: route summary, achievement vs target
              +-- Route Session: start/end route dengan GPS
              +-- Kunjungan outlet: list view + scan barcode Code-128
              +-- Callsheet: cek stok + penjualan + notasi per produk
              +-- Retur recap: ringkasan sisa stok
              +-- Background sync (WorkManager): upload ke server

Prioritas 3 -> Web Frontend (Angular)
              +-- Setup project & design system (bilingual toggle)
              +-- Auth: login Supervisor
              +-- Master Data: CRUD outlet, user, route assignment
              +-- WeekPeriod: buat & kelola periode mingguan
              +-- Target: input target per route per produk
              +-- Stok: buat alokasi stok distribusi Sales
              +-- Dashboard: monitoring kunjungan, achievement vs target
              +-- Retur: lihat rekap retur Sales
```

---

## 11. Out of Scope (MVP)

Item-item berikut **tidak** termasuk dalam scope MVP dan dapat dipertimbangkan untuk iterasi berikutnya:

- GPS tracking / location history Sales secara realtime
- Foto bukti kunjungan (camera integration)
- Laporan & analytics dashboard (charts, grafik tren)
- Notifikasi push (Firebase Cloud Messaging)
- Multi-tenant support
- Role tambahan (selain Spv dan Sales)
- Aplikasi iOS

---

## 12. Keputusan yang Sudah Final

Semua pertanyaan telah dijawab dan menjadi bagian dari spesifikasi.

| # | Pertanyaan | Keputusan | Dampak pada Desain |
|---|---|---|---|
| 1 | Apakah satu Sales bisa memegang lebih dari satu District? | **Tidak** — 1 Sales : 1 District | UNIQUE constraint pada `district.salesman_id` |
| 2 | Stok per SKU atau per bundel/karton? | **Per SKU/Produk** | Entitas `Stock` dan `SalesOrder` menggunakan `product_id` |
| 3 | Format barcode outlet? | **Code-128** | Scanner Android dikonfigurasi untuk Code-128 |
| 4 | Batas maksimal outlet per Route? | **Tidak ada** (sistem), namun Spv membatasi agar Sales max **9 jam/hari** | Tidak ada validasi jumlah di sistem, tanggung jawab Spv |
| 5 | Kunjungan outlet di luar Route — boleh? | **Boleh**, ditandai `is_out_of_route = true` | Field boolean pada entitas `Visit` |
| 6 | Framework Go? | **Gin** | Setup project backend menggunakan Gin |
| 7 | ORM? | **GORM** | Backend menggunakan GORM untuk query dan migrasi |

---

## 13. Referensi Database Lama

Analisis lengkap database aplikasi lama tersedia di [`docs/old-salesmanship.md`](old-salesmanship.md).

Temuan kunci yang sudah diintegrasikan ke PRD ini:
- Multi-UOM stok (DUS, BAL, SLF, BKS) → Section 8.2 `StockAllocationItem`
- Waktu + GPS kunjungan → Section 8.2 `Visit` (`call_start`, `call_end`, `actual_lat/lng`)
- Sesi route harian → Section 8.2 `RouteSession`
- Target mingguan → Section 8.2 `SalesTarget`
- Weekly Period system → Section 8.1 `WeekPeriod`
- Callsheet (gabungan cek stok + penjualan) → Section 8.2 `Callsheet`
- History callsheet → ditarik saat pull data, disimpan lokal sebagai referensi

---

## 14. Design Notes — Untuk Dibahas di Iterasi Berikutnya

Berikut adalah pertanyaan desain yang **tidak blocking MVP** tapi perlu dijawab sebelum iterasi 2:

| # | Topik | Catatan |
|---|---|---|
| DN-01 | Konflik data dua Sales di outlet sama | Bisa terjadi jika Sales A kunjungan out-of-route ke outlet milik Sales B. Perlu strategi merge/flag di server. |
| DN-02 | Approval digital retur | Saat ini hanya konfirmasi fisik. Di iterasi berikutnya, pertimbangkan approval Spv di Web sebelum data retur final. |
| DN-03 | Edit stok setelah pull | Jika Sales sudah pull stok tapi belum berangkat, apakah ada mekanisme koreksi? |
| DN-04 | Role Admin | Mengelola Area, Produk, dan Territory lintas Spv. Akan didesain ulang di iterasi berikutnya. |
| DN-05 | Satu Spv multi-Territory | Apakah seorang Spv bisa mengelola lebih dari satu Territory? Saat ini asumsikan 1:1. |

---

*Dokumen ini adalah living document. Akan diperbarui seiring dengan progress development dan keputusan desain yang dibuat.*
