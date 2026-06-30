# TASK.md
# Salesmanship — Project Task Monitoring

**Referensi:** [PRD.md](PRD.md) · [ARCHITECTURE.md](ARCHITECTURE.md) · [DATABASE.md](DATABASE.md)
**Frontend Web:** ON HOLD (akan dikerjakan setelah Android selesai)

---

## Legenda Status

| Status | Simbol | Keterangan |
|---|---|---|
| Belum dikerjakan | `[ ]` | Task belum dimulai |
| Sedang dikerjakan | `[/]` | Task dalam progress |
| Selesai | `[x]` | Task sudah selesai |
| Diblokir | `[!]` | Task terhambat, butuh input |
| Di-skip / On Hold | `[-]` | Task ditangguhkan |

---

## FASE 1 — Database Server (PostgreSQL)

> Setup schema database di server. Ini adalah fondasi dari seluruh sistem.
> Output: Migration files yang siap dijalankan oleh GORM.

### 1.1 Setup Infrastructure
- [x] Buat struktur folder project backend Go
- [x] Setup `docker-compose.yml` (service: `db`, `api`, `nginx`)
- [x] Buat `.env.example` dengan semua variabel yang dibutuhkan
- [x] Konfigurasi koneksi GORM ke PostgreSQL

### 1.2 Database Migration — Layer 1 (Master)
- [x] Migration: `areas`
- [x] Migration: `territories`
- [x] Migration: `districts`
- [x] Migration: `routes`
- [x] Migration: `outlets`
- [x] Migration: `products` (dengan kolom multi-UOM: `uom_bal`, `uom_slf`, `uom_bks`)
- [x] Migration: `employees` (dengan bcrypt password, `position` CHECK constraint)
- [x] Migration: `params` (LOV global)

### 1.3 Database Migration — Layer 2 (Transaksi Server)
- [x] Migration: `mapping_spv`
- [x] Migration: `mapping_sales`
- [x] Migration: `mapping_outlet`
- [x] Migration: `stock_rokok` (dengan UNIQUE constraint `sales_id + date_used`)
- [x] Migration: `stock_rokok_item` (multi-UOM: `qty_dus_init`, `qty_bal_init`, `qty_slf_init`, `qty_bks_init`)
- [x] Migration: `sales_target`

### 1.4 Database Migration — Layer 3 (Upload Android)
- [x] Migration: `tr_outlet` (dengan `batch_id UUID`, `visit_type`, `lat`, `lng`)
- [x] Migration: `tr_check_stock`
- [x] Migration: `tr_sales`
- [x] Migration: `tr_sales_detail` (dengan `price` snapshot)

### 1.5 Index dan Constraints
- [x] Verifikasi semua foreign key constraints
- [x] Tambah index pada kolom yang sering di-query (lihat DATABASE.md)
- [x] Test migration: berjalan tanpa error

### 1.6 Seed Data
- [x] Seed: `params` (semua group: VISIT_TYPE, NOTATION, STOCK_STATUS)
- [x] Seed: `products` (minimal 5 SKU rokok + UOM konversi)
- [x] Seed: `areas`, `territories`, `districts`, `routes` (hierarki wilayah contoh)
- [x] Seed: `employees` (1 SPV + 2 SALES untuk testing)
- [x] Seed: `outlets` (minimal 10 outlet untuk testing)
- [x] Verifikasi seed berjalan tanpa error

---

## FASE 2 — Backend (Go + Gin + GORM)

> Implementasi REST API. Output: API yang bisa diakses oleh Android dan Web.

### 2.1 Setup Project
- [x] Inisialisasi Go module (`go mod init`)
- [x] Setup folder structure: `cmd/`, `internal/config`, `domain`, `repository`, `usecase`, `handler`, `router`
- [x] Install dependencies: gin, gorm, postgres driver, jwt-go, bcrypt, uuid, godotenv
- [x] Setup middleware: logging, CORS, rate limiter

### 2.2 Auth
- [x] `POST /api/v1/auth/login` — validasi kredensial + generate JWT access & refresh token
- [x] `POST /api/v1/auth/refresh` — validasi refresh token + generate access token baru
- [x] `PUT  /api/v1/auth/password` — ganti password (JWT required)
- [x] Middleware: JWT validator (cek token, extract claims, inject ke context)
- [x] Middleware: Role guard (RoleSpv, RoleSales)

### 2.3 Master Data API (Spv only)
- [x] `GET/POST /api/v1/master/employees` — list + create employee
- [x] `GET/PUT  /api/v1/master/employees/:id` — detail + update employee
- [x] `GET/POST /api/v1/master/areas` — CRUD Area
- [x] `GET/POST /api/v1/master/territories` — CRUD Territory
- [x] `GET/POST /api/v1/master/districts` — CRUD District
- [x] `GET/POST /api/v1/master/routes` — CRUD Route
- [x] `GET/POST /api/v1/master/outlets` — CRUD Outlet
- [x] `GET      /api/v1/master/products` — list produk (read-only, dikelola via seed)
- [x] `GET      /api/v1/master/params` — list LOV (read-only)

### 2.4 Weekly Mapping API (Spv only)
- [x] `GET/POST /api/v1/mapping/spv` — MappingSpv (Spv ke Territory)
- [x] `GET/POST /api/v1/mapping/sales` — MappingSales (Sales ke Spv + District)
- [x] `GET/POST /api/v1/mapping/outlet` — MappingOutlet (Outlet ke Route)

### 2.5 Stock dan Target API
- [x] `POST /api/v1/stock/rokok` — Spv buat alokasi stok (header + item)
- [x] `GET  /api/v1/stock/rokok` — Spv list semua stok; Sales list stok miliknya
- [x] `PUT  /api/v1/stock/rokok/:id/status` — Spv update status (DRAFT ke READY, cancel)
- [x] `GET/POST /api/v1/target` — Spv CRUD SalesTarget

### 2.6 Pull Endpoint (Sales only)
- [x] `GET /api/v1/pull` — Sales download semua data week aktif
  - [x] Sertakan: MstSalesman, MstOutlet, MstProduct, Param, StockRokok + Item, SalesTarget
  - [x] Update `stock_rokok.status` = PULLED setelah pull berhasil

### 2.7 Upload Endpoint (Sales only)
- [x] `POST /api/v1/upload/visit` — terima batch kunjungan dari Android
  - [x] Validasi `batch_id` tidak duplikat (idempotent upload)
  - [x] INSERT tr_outlet, tr_check_stock, tr_sales, tr_sales_detail
  - [x] Return 200 OK atau 409 Conflict jika batch_id sudah ada

### 2.8 Report API (Spv only)
- [x] `GET /api/v1/report/visits` — list kunjungan
- [x] `GET /api/v1/report/sales` — rekap penjualan per produk
- [x] `GET /api/v1/report/stock` — sisa stok Sales
- [x] `GET /api/v1/report/achievement` — achievement vs target

### 2.9 Testing dan Validasi Backend
- [x] Test semua endpoint dengan Postman / HTTP file
- [x] Verifikasi JWT auth berjalan di setiap endpoint yang perlu proteksi
- [x] Verifikasi idempotency upload (POST dua kali dengan batch_id sama -> 409)
- [x] Docker Compose: `docker compose up` berjalan tanpa error

---

## FASE 3 — Android: Setup dan Konfigurasi Proyek

> Setup fondasi project Android. Output: Project yang bisa di-build dan run di emulator.

### 3.1 Inisialisasi Project
- [x] Buat project Android baru (Kotlin + Jetpack Compose + min SDK 26)
- [ ] Setup `build.gradle.kts` dengan semua dependencies:
  - [ ] Jetpack Compose BOM (latest stable)
  - [ ] Room + KSP
  - [ ] Retrofit + OkHttp + Gson/Moshi
  - [ ] WorkManager
  - [ ] Hilt (Dependency Injection)
  - [ ] Navigation Compose
  - [ ] DataStore (untuk JWT token storage)
  - [ ] ZXing / ML Kit (barcode scanner)
  - [ ] FusedLocationProvider (GPS)
- [ ] Setup `local.properties` dengan `BASE_URL` backend

### 3.2 Konfigurasi Dependency Injection (Hilt)
- [ ] Setup Application class dengan @HiltAndroidApp
- [ ] Buat DatabaseModule (provide Room DB instance)
- [ ] Buat NetworkModule (provide Retrofit + OkHttp dengan JWT interceptor)
- [ ] Buat RepositoryModule (bind repository interfaces ke implementasinya)

### 3.3 Konfigurasi Jaringan (Retrofit)
- [ ] Buat ApiService interface dengan semua endpoint (auth, pull, upload)
- [ ] Buat AuthInterceptor — inject Authorization header ke setiap request
- [ ] Buat TokenRefreshInterceptor — auto-refresh jika response 401
- [ ] Setup DataStore untuk menyimpan access_token dan refresh_token

---

## FASE 4 — Android: Design UI/UX

> Desain visual setiap screen sebelum implementasi bisnis.
> Output: Semua Composable screen yang sudah tampil dengan data dummy/preview.

### 4.1 Design System dan Theme
- [ ] Setup MaterialTheme dengan warna brand dan typography
- [ ] Buat komponen shared: AppButton, AppTextField, AppTopBar, AppCard, AppBadge
- [ ] Buat LoadingOverlay dan ErrorSnackbar component
- [ ] Setup navigation graph (NavHost dengan semua route)

### 4.2 Screen: Authentication
- [ ] LoginScreen — form username + password + tombol login
- [ ] Preview state: loading, error, success

### 4.3 Screen: Dashboard
- [ ] DashboardScreen dengan:
  - [ ] Header: nama Sales + info week aktif
  - [ ] Card: ringkasan stok yang dibawa (per UOM)
  - [ ] Card: progress achievement vs target (per produk)
  - [ ] List: outlet hari ini (grouped by route, dengan status visited/unvisited)
  - [ ] FAB: tombol Tarik Data atau Scan Barcode

### 4.4 Screen: Daftar Outlet
- [ ] OutletListScreen — list semua outlet dengan search bar
- [ ] OutletListItem composable — nama, alamat, status kunjungan, badge route
- [ ] State: loading, empty, error

### 4.5 Screen: Kunjungan Outlet
- [ ] VisitScreen — detail satu kunjungan, parent screen untuk callsheet
- [ ] Header: nama outlet + waktu kunjungan dimulai + GPS indicator
- [ ] Tab: Cek Stok | Penjualan
- [ ] Tombol: Selesai Kunjungan

### 4.6 Screen: Cek Stok
- [ ] CheckStockScreen — list produk, masing-masing punya input stock_qty
- [ ] Tampilkan history stok minggu lalu sebagai referensi
- [ ] Validasi: stock_qty >= 0

### 4.7 Screen: Penjualan
- [ ] SalesScreen — list produk dengan input qty
- [ ] Tampilkan sisa stok Sales untuk setiap produk
- [ ] Validasi: qty tidak boleh melebihi sisa stok Sales
- [ ] Hitung total = qty x price secara otomatis
- [ ] Ringkasan total transaksi di bagian bawah

### 4.8 Screen: Barcode Scanner
- [ ] BarcodeScannerScreen — live camera feed dengan overlay scan area
- [ ] Tampilkan nama outlet jika barcode ditemukan
- [ ] Error state: barcode tidak dikenali

### 4.9 Screen: Sinkronisasi dan Status
- [ ] SyncStatusScreen — list semua item di SyncQueue
- [ ] Badge status: PENDING / SYNCING / SYNCED / FAILED
- [ ] Tombol Sync Manual
- [ ] Indikator koneksi internet

### 4.10 Screen: Ringkasan Retur
- [ ] ReturSummaryScreen — tabel sisa stok per SKU per UOM
- [ ] Kalkulasi: qty_returned = qty_init - qty_sold
- [ ] Tampilkan dalam format: DUS | BAL | SLOP | BUNGKUS

---

## FASE 5 — Android: Database (Room)

> Implementasi Room Database. Output: Semua DAO + Entity yang bisa digunakan oleh Repository.

### 5.1 Setup Room Database
- [ ] Buat AppDatabase class (abstract, anotasi @Database)
- [ ] Konfigurasi Room dengan semua entity dan DAO
- [ ] Setup migrasi jika ada schema change

### 5.2 Entity — Master (Download dari Server)
- [ ] MstSalesmanEntity
- [ ] MstProductEntity (id, server_id, name, sku, price, uom_bal, uom_slf, uom_bks)
- [ ] MstOutletEntity (id, server_id, name, owner_name, barcode, lat, lng, outlet_status, route_id)
- [ ] ParamEntity (id, group_name, key, value, description)
- [ ] StockRokokEntity (id, server_id, sales_id, date_used, status)
- [ ] StockRokokItemEntity (multi-UOM: qty_dus_init, qty_bal_init, qty_slf_init, qty_bks_init)
- [ ] SalesTargetEntity

### 5.3 Entity — Transaksi Lokal
- [ ] TrOutletEntity (id, outlet_id, visit_no, visit_type, status, start_time, end_time, lat, lng)
- [ ] TrCheckStockEntity (id, tr_outlet_id, product_id, stock_qty)
- [ ] TrSalesEntity (id, tr_outlet_id, sales_order)
- [ ] TrSalesDetailEntity (id, tr_sales_id, product_id, qty, price, total)

### 5.4 Entity — Utilitas
- [ ] SyncQueueEntity (id, entity_type, entity_id, batch_id, status)

### 5.5 DAO
- [ ] MstSalesmanDao — insert (replace), query salesman saat ini
- [ ] MstProductDao — insert (replace), query all, by id, by barcode
- [ ] MstOutletDao — insert (replace), by route, by barcode, search by name
- [ ] ParamDao — insert (replace), query by group
- [ ] StockRokokDao — insert (replace), by date, update status
- [ ] StockRokokItemDao — insert (replace), by stock_rokok_id, update qty
- [ ] SalesTargetDao — insert (replace), by route + week
- [ ] TrOutletDao — insert, update status, by outlet_id + date, query all open
- [ ] TrCheckStockDao — insert, by tr_outlet_id
- [ ] TrSalesDao — insert, by tr_outlet_id
- [ ] TrSalesDetailDao — insert, by tr_sales_id
- [ ] SyncQueueDao — insert, update status, query PENDING, query FAILED, delete SYNCED

### 5.6 Verifikasi Room Schema
- [ ] Export Room schema JSON
- [ ] Unit test DAO dengan in-memory Room DB

---

## FASE 6 — Android: Implementasi Bisnis

> Koneksi semua layer: ViewModel -> UseCase -> Repository -> Room/Retrofit.
> Output: Aplikasi Android yang bisa digunakan end-to-end.

### 6.1 Repository Layer
- [ ] AuthRepository — login (Retrofit), save/get token (DataStore)
- [ ] PullRepository — call /api/v1/pull, parse response, insert ke Room
- [ ] OutletRepository — query MstOutletDao, search by barcode dan name
- [ ] VisitRepository — CRUD TrOutletDao, create visit, close visit
- [ ] CheckStockRepository — CRUD TrCheckStockDao
- [ ] SalesRepository — CRUD TrSalesDao + TrSalesDetailDao, kalkulasi stok tersisa
- [ ] SyncRepository — manage SyncQueueDao, build upload payload, call upload endpoint
- [ ] StockRepository — query StockRokokDao + item, kalkulasi sisa stok per UOM
- [ ] TargetRepository — query SalesTargetDao, hitung achievement dari TrSalesDetail

### 6.2 UseCase Layer
- [ ] LoginUseCase — validasi input, call AuthRepository.login()
- [ ] PullDataUseCase — cek status stok (READY?), call pull, simpan ke Room
- [ ] OpenVisitUseCase — cari outlet by barcode/id, buat TrOutlet baru, rekam GPS + timestamp
- [ ] SaveCheckStockUseCase — insert/update TrCheckStock per produk
- [ ] AddSalesUseCase — insert TrSales + TrSalesDetail, validasi qty <= sisa stok
- [ ] CloseVisitUseCase — update TrOutlet.status = CLOSED, insert ke SyncQueue
- [ ] GetDashboardUseCase — aggregate data: stok, target, visit progress
- [ ] GetReturSummaryUseCase — hitung sisa stok dari stok awal - total penjualan

### 6.3 ViewModel Layer
- [ ] LoginViewModel — state: loading, error, success
- [ ] DashboardViewModel — observe outlet list, stok summary, target progress
- [ ] OutletListViewModel — search/filter outlet, observe visit status
- [ ] VisitViewModel — manage state aktif kunjungan, GPS, waktu
- [ ] CheckStockViewModel — manage form cek stok, history referensi
- [ ] SalesViewModel — manage form penjualan, validasi stok, hitung total
- [ ] SyncViewModel — observe SyncQueue, trigger manual sync

### 6.4 WorkManager — SyncWorker
- [ ] Buat SyncWorker class (CoroutineWorker)
- [ ] Ambil PENDING dari SyncQueue, build payload, upload, update status
- [ ] Setup exponential backoff untuk FAILED items
- [ ] Register SyncWorker di Application.onCreate()
- [ ] Test retry mechanism: server mati -> kunjungan -> server hidup -> verify sync

### 6.5 Barcode Scanner
- [ ] Integrasikan ZXing atau ML Kit ke BarcodeScannerScreen
- [ ] Parse hasil scan -> lookup di MstOutletDao -> buka VisitScreen

### 6.6 GPS Location
- [ ] Request permission ACCESS_FINE_LOCATION
- [ ] Capture lat, lng saat outlet dibuka (simpan ke TrOutlet)

### 6.7 Integrasi dan End-to-End Testing
- [ ] Test flow lengkap: Login -> Pull -> Kunjungan -> Cek Stok -> Penjualan -> Selesai -> Auto-Sync
- [ ] Test offline: buat kunjungan tanpa internet -> nyalakan internet -> verify upload
- [ ] Test barcode scan
- [ ] Test validasi: qty melebihi stok -> sistem reject

---

## FASE 7 — Frontend Web (ON HOLD)

> Frontend Angular untuk Supervisor. Ditangguhkan sampai Android selesai.

### 7.1 Setup Angular
- [-] Setup project Angular + design system
- [-] Auth: login Supervisor
- [-] Master Data: CRUD Employee, Outlet, dll.
- [-] Weekly Mapping: atur mapping per week
- [-] StockRokok: buat alokasi stok harian
- [-] SalesTarget: input target per route per produk
- [-] Dashboard: monitoring kunjungan dan penjualan
- [-] Laporan: achievement vs target, retur stok

---

## Progress Tracker

| Fase | Deskripsi | Status | % |
|---|---|---|---|
| Fase 1 | Database Server | `[x]` Selesai | 100% |
| Fase 2 | Backend Go API | `[x]` Selesai | 100% |
| Fase 3 | Android: Setup | `[ ]` Belum dimulai | 0% |
| Fase 4 | Android: UI/UX | `[ ]` Belum dimulai | 0% |
| Fase 5 | Android: Database (Room) | `[ ]` Belum dimulai | 0% |
| Fase 6 | Android: Business Logic | `[ ]` Belum dimulai | 0% |
| Fase 7 | Web Frontend | `[-]` ON HOLD | - |

---

*File ini diupdate setiap kali ada progress. Centang [x] pada task yang sudah selesai.*
