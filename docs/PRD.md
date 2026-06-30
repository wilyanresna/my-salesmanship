# Product Requirements Document (PRD)
# Salesmanship — Aplikasi Distribusi Rokok

**Versi:** 1.5
**Tanggal:** 2026-06-30
**Status:** Data Model Final — Siap Development

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
|  |  Go Backend  |<----->|     PostgreSQL          |  |
|  |  (REST API)  |       |  [Master]               |  |
|  +------+-------+       |  [Staging Master]       |  |
|         | Docker        |  [Staging Transaction]  |  |
|         | Compose       +------------------------+  |
+---------+-------------------------------------------+
          |
    +-----+------+
    |            |
    v            v
+--------+  +--------------------------+
|Angular |  |      Android App         |
|  Web   |  |  [Staging Master (snap)] |
| (Spv)  |  |  [Transaction Local]     |
| reads  |  |  [Param]                 |
| from   |  |  Room Database (offline) |
|Staging |  +-----+--------------------+
+--------+        |
                  | sync (WorkManager)
                  v
             Backend Staging Transaction
```

### 7.1 Prinsip Arsitektur Data

| Prinsip | Penjelasan |
|---|---|
| **Master is read-only for Android** | Android hanya membaca Master dari backend saat pull. Tidak ada write dari Android ke Master. |
| **Weekly Mapping = snapshot** | MappingSpv, MappingSales, MappingOutlet bersifat weekly — mencerminkan kondisi distribusi minggu itu. Android download ini saat pull. |
| **Transaksi Android → Server via batch** | Android generate UUID sebagai `batch_id`, lalu upload transaksi per kunjungan ke server. Server menyimpan data ini secara permanen. |
| **SyncQueue = general retry mechanism** | Setiap entitas yang perlu di-sync masuk ke `SyncQueue`. WorkManager memproses antrian ini dan melakukan retry otomatis jika gagal. |
| **Param = global static LOV** | Semua LOV (notasi, visit type, dll.) disimpan dalam satu tabel `Param` global, tidak terikat week. |
| **StockRokok bersifat daily** | Satu Sales bisa punya beberapa `StockRokok` dalam satu week (satu per hari kerja). Week dihitung dari `date_used`. |

### 7.2 Auto-Sync Flow (Setelah Setiap Kunjungan)

```
Sales selesai kunjungan (End Visit)
           |
           v
  TrOutlet.status = CLOSED
           |
           v
  SyncQueue entry dibuat:
  entity_type = TrOutlet
  entity_id   = TrOutlet.id
  batch_id    = UUID baru
  status      = PENDING
           |
           v
  WorkManager cek koneksi
     +-- Online  ---> Upload batch ke server (TrOutlet + TrCheckStock + TrSales + TrSalesDetail)
     |                     |
     |              Sukses: SyncQueue.status = SYNCED
     |              Gagal:  SyncQueue.status = FAILED (retry otomatis berikutnya)
     |
     +-- Offline --> Tunggu, WorkManager retry saat koneksi tersedia
```

---

## 8. Data Model

> Detail lengkap data model tersedia di [`docs/data-model.md`](data-model.md).
> PRD ini menyajikan ringkasan per layer.

> Arsitektur data terbagi menjadi dua dunia: **Server (PostgreSQL)** dan **Android (Room DB)**.
> Keduanya tidak berbagi tabel secara langsung. Sync terjadi melalui REST API dengan pola `batch_id` (UUID).

---

### 8.A — Server / Backend & Web Frontend (PostgreSQL)

#### Layer 1: Master
Data canonical yang bersifat permanen. Android hanya membaca, tidak pernah menulis ke sini.

| # | Tabel | Fungsi | Dikelola oleh |
|---|---|---|---|
| 1 | `Employee` | Data Spv dan Sales | Seed |
| 2 | `Area` | Master Area | Seed |
| 3 | `Territory` | Master Territory | Seed |
| 4 | `District` | Master District | Seed |
| 5 | `Route` | Master Route | Seed |
| 6 | `Outlet` | Data master Outlet | Seed / Spv |
| 7 | `Product` | Data Produk + multi-UOM | Seed |
| 8 | `Param` | Semua LOV (global, static) | Seed |

#### Layer 2: Transaksi Server (Weekly Mapping + Stok + Target)
Dibuat dan dikelola Spv. Android men-download data ini saat pull.

| # | Tabel | Fungsi | Bersifat |
|---|---|---|---|
| 9 | `MappingSpv` | Mapping Territory ↔ Spv | Weekly |
| 10 | `MappingSales` | Mapping Sales ↔ Spv + District | Weekly |
| 11 | `MappingOutlet` | Mapping Outlet ↔ Route | Weekly |
| 12 | `StockRokok` | Header alokasi stok per Sales | Daily |
| 13 | `StockRokokItem` | Detail stok per SKU (multi-UOM) | Daily |
| 14 | `SalesTarget` | Target penjualan per Sales per Route | Weekly |

#### Layer 3: Transaksi Upload dari Android
Hasil upload transaksi Sales dari lapangan. Web membaca layer ini untuk laporan.
Semua tabel di layer ini memiliki `batch_id` (UUID dari Android) dan `uploaded_at`.

| # | Tabel | Fungsi | Sumber |
|---|---|---|---|
| 27 | `TrOutlet` | Header kunjungan per outlet | Upload Android |
| 28 | `TrCheckStock` | Cek stok per outlet per produk | Upload Android |
| 29 | `TrSales` | Header penjualan dalam kunjungan | Upload Android |
| 30 | `TrSalesDetail` | Detail penjualan per SKU + snapshot price | Upload Android |

---

### 8.B — Android (Room Database — SQLite)

Semua data di Room DB bersifat lokal dan offline-first.

#### Layer 1: Master Android (Download dari Server)
Data yang di-download saat pull. **Read-only** di Android.

| # | Tabel (Room) | Fungsi | Sumber |
|---|---|---|---|
| 15 | `MstSalesman` | Data Salesman + Spv + District week ini | Join Employee + MappingSales |
| 16 | `MstProduct` | Produk week berjalan | Product |
| 17 | `MstOutlet` | Outlet yang masuk route Sales week ini | Join Outlet + MappingOutlet |
| 18 | `Param` | LOV global | Param |
| 19 | `StockRokok` | Header alokasi stok (server_id disimpan) | StockRokok |
| 20 | `StockRokokItem` | Detail stok per SKU | StockRokokItem |
| 21 | `SalesTarget` | Target per route per produk | SalesTarget |

#### Layer 2: Transaksi Lokal Android
Dibuat Sales di lapangan. Di-sync ke server setelah selesai kunjungan.

| # | Tabel (Room) | Fungsi | Atribut Kunci |
|---|---|---|---|
| 22 | `TrOutlet` | Header per kunjungan outlet | id, outlet_id, visit_no, **visit_type** (NORMAL/OUT_OF_ROUTE), status, start_time, end_time, **lat, lng** |
| 23 | `TrCheckStock` | Cek stok per outlet per produk | id, tr_outlet_id, product_id, stock_qty |
| 24 | `TrSales` | Header penjualan | id, tr_outlet_id, sales_order |
| 25 | `TrSalesDetail` | Detail per SKU + snapshot harga | id, tr_sales_id, product_id, qty, **price**, total |

#### Layer 3: Utilitas

| # | Tabel (Room) | Fungsi | Atribut Kunci |
|---|---|---|---|
| 26 | `SyncQueue` | Antrian sync general — semua entitas | id, entity_type, entity_id, **batch_id** (UUID), status (PENDING/SYNCING/SYNCED/FAILED) |

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
              +-- Database schema & GORM migrations (Master + Transaksi + Upload)
              +-- Auth: JWT login, refresh token, ganti password
              +-- API: Master Data (Employee, Area, Territory, District, Route, Outlet, Product, Param)
              +-- API: Weekly Mapping (MappingSpv, MappingSales, MappingOutlet) — CRUD by Spv
              +-- API: StockRokok + StockRokokItem — CRUD by Spv, pull by Sales
              +-- API: SalesTarget — CRUD by Spv
              +-- API: Pull endpoint — Sales download data week aktif
              +-- API: Upload endpoint — terima batch transaksi dari Android (TrOutlet, TrCheckStock, TrSales, TrSalesDetail)
              +-- API: Laporan — query hasil kunjungan untuk Web

Prioritas 2 -> Android (Kotlin + Jetpack Compose)
              +-- Setup project + Room + WorkManager + Retrofit
              +-- Auth: login, JWT storage, auto refresh
              +-- Pull data: MstSalesman, MstOutlet, MstProduct, Param, StockRokok, SalesTarget
              +-- Dashboard: route summary, progress stok, achievement vs target
              +-- Kunjungan outlet: list view + scan barcode Code-128 + GPS capture
              +-- TrCheckStock: cek stok per produk
              +-- TrSales + TrSalesDetail: penjualan per kunjungan (dengan price snapshot)
              +-- SyncQueue: antrian general, trigger otomatis saat End Visit
              +-- WorkManager: proses SyncQueue, retry jika FAILED

Prioritas 3 -> Web Frontend (Angular)
              +-- Setup project & design system (bilingual toggle)
              +-- Auth: login Supervisor
              +-- Master Data: CRUD Employee, Outlet, dll.
              +-- Weekly Mapping: atur MappingSpv, MappingSales, MappingOutlet per week
              +-- StockRokok: buat alokasi stok harian Sales
              +-- SalesTarget: input target per route per produk
              +-- Dashboard: monitoring kunjungan, TrOutlet, TrCheckStock, TrSales
              +-- Laporan: achievement vs target, retur stok
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
