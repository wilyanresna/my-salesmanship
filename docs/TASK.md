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
- [x] Setup `build.gradle.kts` dengan semua dependencies:
  - [x] Jetpack Compose BOM (latest stable)
  - [x] Room + KSP
  - [x] Retrofit + OkHttp + Gson/Moshi
  - [x] WorkManager
  - [x] Hilt (Dependency Injection)
  - [x] Navigation Compose
  - [x] DataStore (untuk JWT token storage)
  - [x] ZXing / ML Kit (barcode scanner)
  - [x] FusedLocationProvider (GPS)
- [x] Setup `local.properties` dengan `BASE_URL` backend

### 3.2 Konfigurasi Dependency Injection (Hilt)
- [x] Setup Application class dengan @HiltAndroidApp
- [x] Buat DatabaseModule (provide Room DB instance)
- [x] Buat NetworkModule (provide Retrofit + OkHttp dengan JWT interceptor)
- [x] Buat RepositoryModule (bind repository interfaces ke implementasinya)

### 3.3 Konfigurasi Jaringan (Retrofit)
- [x] Buat ApiService interface dengan semua endpoint (auth, pull, upload)
- [x] Buat AuthInterceptor — inject Authorization header ke setiap request
- [x] Buat TokenRefreshInterceptor — auto-refresh jika response 401
- [x] Setup DataStore untuk menyimpan access_token dan refresh_token

---

## FASE 4 — Android: Design UI/UX

> Desain visual setiap screen sebelum implementasi bisnis.
> Output: Semua Composable screen yang sudah tampil dengan data dummy/preview.

### 4.1 Design System dan Theme
- [x] Setup MaterialTheme dengan warna brand dan typography
- [x] Buat komponen shared: AppButton, AppTextField, AppTopBar, AppCard, AppBadge
- [x] Buat LoadingOverlay dan ErrorSnackbar component
- [x] Setup navigation graph (NavHost dengan semua route)

### 4.2 Screen: Authentication
- [x] LoginScreen — form username + password + tombol login
- [x] Preview state: loading, error, success

### 4.3 Screen: Dashboard
- [x] DashboardScreen dengan:
  - [x] Header: nama Sales + info week aktif
  - [x] Card: ringkasan stok yang dibawa (per UOM)
  - [x] Card: progress achievement vs target (per produk)
  - [x] List: outlet hari ini (grouped by route, dengan status visited/unvisited)
  - [x] FAB: tombol Tarik Data atau Scan Barcode

### 4.4 Screen: Daftar Outlet
- [x] OutletListScreen — list semua outlet dengan search bar
- [x] OutletListItem composable — nama, alamat, status kunjungan, badge route
- [x] State: loading, empty, error

### 4.5 Screen: Kunjungan Outlet
- [x] VisitScreen — detail satu kunjungan, parent screen untuk callsheet
- [x] Header: nama outlet + waktu kunjungan dimulai + GPS indicator
- [x] Tab: Cek Stok | Penjualan
- [x] Tombol: Selesai Kunjungan

### 4.6 Screen: Cek Stok
- [x] CheckStockScreen — list produk, masing-masing punya input stock_qty
- [x] Tampilkan history stok minggu lalu sebagai referensi
- [x] Validasi: stock_qty >= 0

### 4.7 Screen: Penjualan
- [x] SalesScreen — list produk dengan input qty
- [x] Tampilkan sisa stok Sales untuk setiap produk
- [x] Validasi: qty tidak boleh melebihi sisa stok Sales
- [x] Hitung total = qty x price secara otomatis
- [x] Ringkasan total transaksi di bagian bawah

### 4.8 Screen: Barcode Scanner
- [x] BarcodeScannerScreen — live camera feed dengan overlay scan area
- [x] Tampilkan nama outlet jika barcode ditemukan
- [x] Error state: barcode tidak dikenali

### 4.9 Screen: Sinkronisasi dan Status
- [x] SyncStatusScreen — list semua item di SyncQueue
- [x] Badge status: PENDING / SYNCING / SYNCED / FAILED
- [x] Tombol Sync Manual
- [x] Indikator koneksi internet

### 4.10 Screen: Ringkasan Retur
- [x] ReturSummaryScreen — tabel sisa stok per SKU per UOM
- [x] Kalkulasi: qty_returned = qty_init - qty_sold
- [x] Tampilkan dalam format: DUS | BAL | SLOP | BUNGKUS

---

## FASE 5 — Android: Database (Room)

> Implementasi Room Database. Output: Semua DAO + Entity yang bisa digunakan oleh Repository.

### 5.1 Setup Room Database
- [x] Buat AppDatabase class (abstract, anotasi @Database)
- [x] Konfigurasi Room dengan semua entity dan DAO
- [x] Setup migrasi jika ada schema change

### 5.2 Entity — Master (Download dari Server)
- [x] MstSalesmanEntity
- [x] MstProductEntity (id, server_id, name, sku, price, uom_bal, uom_slf, uom_bks)
- [x] MstOutletEntity (id, server_id, name, owner_name, barcode, lat, lng, outlet_status, route_id)
- [x] ParamEntity (id, group_name, key, value, description)
- [x] StockRokokEntity (id, server_id, sales_id, date_used, status)
- [x] StockRokokItemEntity (multi-UOM: qty_dus_init, qty_bal_init, qty_slf_init, qty_bks_init)
- [x] SalesTargetEntity

### 5.3 Entity — Transaksi Lokal
- [x] TrOutletEntity (id, outlet_id, visit_no, visit_type, status, start_time, end_time, lat, lng)
- [x] TrCheckStockEntity (id, tr_outlet_id, product_id, stock_qty)
- [x] TrSalesEntity (id, tr_outlet_id, sales_order)
- [x] TrSalesDetailEntity (id, tr_sales_id, product_id, qty, price, total)

### 5.4 Entity — Utilitas
- [x] SyncQueueEntity (id, entity_type, entity_id, batch_id, status)

### 5.5 DAO
- [x] MstSalesmanDao — insert (replace), query salesman saat ini
- [x] MstProductDao — insert (replace), query all, by id, by barcode
- [x] MstOutletDao — insert (replace), by route, by barcode, search by name
- [x] ParamDao — insert (replace), query by group
- [x] StockRokokDao — insert (replace), by date, update status
- [x] StockRokokItemDao — insert (replace), by stock_rokok_id, update qty
- [x] SalesTargetDao — insert (replace), by route + week
- [x] TrOutletDao — insert, update status, by outlet_id + date, query all open
- [x] TrCheckStockDao — insert, by tr_outlet_id
- [x] TrSalesDao — insert, by tr_outlet_id
- [x] TrSalesDetailDao — insert, by tr_sales_id
- [x] SyncQueueDao — insert, update status, query PENDING, query FAILED, delete SYNCED

### 5.6 Verifikasi Room Schema
- [x] Export Room schema JSON
- [x] Unit test DAO dengan in-memory Room DB

---

## FASE 6 — Android: Implementasi Bisnis

> Koneksi semua layer: ViewModel -> UseCase -> Repository -> Room/Retrofit.
> Output: Aplikasi Android yang bisa digunakan end-to-end.

### 6.1 Repository Layer
- [x] AuthRepository — login (Retrofit), save/get token (DataStore)
- [x] PullRepository — call /api/v1/pull, parse response, insert ke Room
- [x] OutletRepository — query MstOutletDao, search by barcode dan name
- [x] VisitRepository — CRUD TrOutletDao, create visit, close visit
- [x] CheckStockRepository — CRUD TrCheckStockDao
- [x] SalesRepository — CRUD TrSalesDao + TrSalesDetailDao, kalkulasi stok tersisa
- [x] SyncRepository — manage SyncQueueDao, build upload payload, call upload endpoint
- [x] StockRepository — query StockRokokDao + item, kalkulasi sisa stok per UOM
- [x] TargetRepository — query SalesTargetDao, hitung achievement dari TrSalesDetail

### 6.2 UseCase Layer
- [x] LoginUseCase — validasi input, call AuthRepository.login()
- [x] PullDataUseCase — cek status stok (READY?), call pull, simpan ke Room
- [x] OpenVisitUseCase — cari outlet by barcode/id, buat TrOutlet baru, rekam GPS + timestamp
- [x] SaveCheckStockUseCase — insert/update TrCheckStock per produk
- [x] AddSalesUseCase — insert TrSales + TrSalesDetail, validasi qty <= sisa stok
- [x] CloseVisitUseCase — update TrOutlet.status = CLOSED, insert ke SyncQueue
- [x] GetDashboardUseCase — aggregate data: stok, target, visit progress
- [x] GetReturSummaryUseCase — hitung sisa stok dari stok awal - total penjualan

### 6.3 ViewModel Layer
- [x] LoginViewModel — state: loading, error, success
- [x] DashboardViewModel — observe outlet list, stok summary, target progress
- [x] OutletListViewModel — search/filter outlet, observe visit status
- [x] VisitViewModel — manage state aktif kunjungan, GPS, waktu
- [x] CheckStockViewModel — manage form cek stok, history referensi
- [x] SalesViewModel — manage form penjualan, validasi stok, hitung total
- [x] SyncViewModel — observe SyncQueue, trigger manual sync

### 6.4 WorkManager — SyncWorker
- [x] Buat SyncWorker class (CoroutineWorker)
- [x] Ambil PENDING dari SyncQueue, build payload, upload, update status
- [x] Setup exponential backoff untuk FAILED items
- [x] Register SyncWorker di Application.onCreate()
- [x] Test retry mechanism: server mati -> kunjungan -> server hidup -> verify sync

### 6.5 Barcode Scanner
- [x] Integrasikan ZXing atau ML Kit ke BarcodeScannerScreen
- [x] Parse hasil scan -> lookup di MstOutletDao -> buka VisitScreen

### 6.6 GPS Location
- [x] Request permission ACCESS_FINE_LOCATION
- [x] Capture lat, lng saat outlet dibuka (simpan ke TrOutlet)

### 6.7 Integrasi dan End-to-End Testing
- [x] Test flow lengkap: Login -> Pull -> Kunjungan -> Cek Stok -> Penjualan -> Selesai -> Auto-Sync
- [x] Test offline: buat kunjungan tanpa internet -> nyalakan internet -> verify upload
- [x] Test barcode scan
- [x] Test validasi: qty melebihi stok -> sistem reject

---

## FASE 7 — Frontend Web (Angular)

> Frontend Angular untuk Supervisor.
> Stack: Angular 17 + NgModule · Angular Material (MD3) · RxJS BehaviorSubject · ng2-charts (Chart.js)
> Layout: Sidebar collapsible (full ↔ icon-only toggle).

### 7.1 Setup Project & Fondasi

- [x] Inisialisasi project Angular 17 (`ng new salesmanship-web --routing --style=scss`)
- [x] Install dependencies:
  - [x] `@angular/material` (Angular Material MD3 + CDK)
  - [x] `@angular/flex-layout` atau CSS Grid custom untuk layout
  - [x] `ng2-charts` + `chart.js` (untuk bar chart dan line chart di dashboard)
  - [x] `@auth0/angular-jwt` atau custom JWT interceptor
- [x] Setup `environment.ts` / `environment.prod.ts` dengan `apiUrl`
- [x] Setup Angular Material theme (custom color palette sesuai brand)
- [x] Buat `CoreModule` (singleton services: AuthService, ApiService, interceptors)
- [x] Buat `SharedModule` (komponen/pipe/directive yang dipakai lintas modul)

### 7.2 Shell Layout & Navigation

- [x] Buat `AppShellComponent` — parent layout dengan:
  - [x] Sidebar kiri collapsible (full width ↔ icon-only saat di-toggle)
  - [x] Top header bar (nama Spv, week aktif, tombol toggle sidebar, tombol logout)
  - [x] `<router-outlet>` untuk konten utama
- [x] Buat `SidenavComponent` dengan menu items:
  - [x] Dashboard
  - [x] Master Data (submenu: Employee, Outlet, Area/Territory/District/Route)
  - [x] Weekly Mapping (submenu: Mapping Spv, Mapping Sales, Mapping Outlet)
  - [x] Stok Distribusi
  - [x] Target Sales
  - [x] Laporan (submenu: Kunjungan, Penjualan, Achievement)
- [x] Guard: `AuthGuard` — redirect ke login jika belum auth
- [x] Guard: `RoleGuard` — pastikan role = SPV untuk semua route kecuali auth
- [x] Lazy loading untuk setiap feature module

### 7.3 Autentikasi

- [x] `AuthModule` dengan route `/login`
- [x] `LoginComponent` — form username + password, tombol login
- [x] `AuthService`:
  - [x] `login()` — call `POST /api/v1/auth/login`, simpan token ke `localStorage`
  - [x] `refreshToken()` — call `POST /api/v1/auth/refresh`
  - [x] `logout()` — clear token, redirect ke `/login`
  - [x] `currentUser$` — BehaviorSubject untuk data user saat ini
- [x] `JwtInterceptor` — inject `Authorization: Bearer <token>` ke semua request
- [x] `TokenExpiredInterceptor` — auto-refresh jika response 401, retry request
- [x] Halaman error: session expired, unauthorized

### 7.4 Master Data

- [x] `MasterModule` dengan lazy loading
- [x] **Employee:**
  - [x] `EmployeeListComponent` — tabel paginated (nama, NIK, username, posisi, status aktif), tombol tambah + edit
  - [x] `EmployeeFormComponent` — form dialog (MatDialog): nama, NIK, username, password, posisi, telepon, alamat
  - [x] `EmployeeService` — CRUD via `GET/POST/PUT /api/v1/master/employees`
- [x] **Outlet:**
  - [x] `OutletListComponent` — tabel paginated + search bar (nama, barcode, status)
  - [x] `OutletFormComponent` — form dialog: nama, pemilik, telepon, alamat, lat, lng, barcode, status, call cycle
  - [x] `OutletService` — CRUD via `GET/POST/PUT /api/v1/master/outlets`
- [x] **Area / Territory / District / Route:**
  - [x] `AreaListComponent` — tabel sederhana + form inline atau dialog
  - [x] `TerritoryListComponent` — filter by Area
  - [x] `DistrictListComponent` — filter by Territory
  - [x] `RouteListComponent` — filter by District, tampilkan day_of_week
  - [x] Service masing-masing untuk CRUD hierarki wilayah
- [x] **Product & Param** (read-only):
  - [x] `ProductListComponent` — tabel produk + UOM konversi (informational, tidak bisa edit)
  - [x] `ParamListComponent` — tabel LOV per group (informational)

### 7.5 Weekly Mapping

- [x] `MappingModule` dengan lazy loading
- [x] `WeekSelectorComponent` — shared component pilih week_start (date picker, snap ke Senin)
- [x] **Mapping Spv:**
  - [x] `MappingSpvComponent` — list mapping aktif + form tambah: pilih Spv, pilih Territory, week range
  - [x] `MappingSpvService` — `GET/POST /api/v1/mapping/spv`
- [x] **Mapping Sales:**
  - [x] `MappingSalesComponent` — list mapping aktif + form tambah: pilih Spv, pilih Sales, pilih District, week range
  - [x] Validasi: satu Sales hanya boleh punya satu mapping aktif per week
  - [x] `MappingSalesService` — `GET/POST /api/v1/mapping/sales`
- [x] **Mapping Outlet:**
  - [x] `MappingOutletComponent` — list mapping aktif dengan filter by route + week
  - [x] Form tambah: pilih Outlet (searchable), pilih Route, week range
  - [x] `MappingOutletService` — `GET/POST /api/v1/mapping/outlet`

### 7.6 Stok Distribusi

- [x] `StockModule` dengan lazy loading
- [x] `StockListComponent` — tabel StockRokok dengan filter: Sales, tanggal, status
  - [x] Kolom: Sales, tanggal, status badge (DRAFT/READY/PULLED/CLOSED), aksi
  - [x] Tombol "Buat Stok Baru"
  - [x] Tombol "Ubah Status" (DRAFT↔READY, cancel jika belum PULLED)
- [x] `StockFormComponent` — form buat alokasi stok baru:
  - [x] Pilih Sales (dropdown dari MappingSales week ini)
  - [x] Pilih tanggal kerja
  - [x] Tabel input per produk: qty_dus, qty_bal, qty_slf, qty_bks
  - [x] Kalkulasi total bungkus (display-only)
- [x] `StockDetailComponent` — detail stok + list item per SKU
- [x] `StockService` — `GET/POST/PUT /api/v1/stock/rokok`

### 7.7 Target Sales

- [ ] `TargetModule` dengan lazy loading
- [ ] `TargetListComponent` — tabel target dengan filter: Sales, route, week
  - [ ] Kolom: Sales, Route, Produk, Target Qty, Week
  - [ ] Tombol "Tambah Target"
- [ ] `TargetFormComponent` — form dialog input target:
  - [ ] Pilih Sales → otomatis filter Route yang relevan
  - [ ] Pilih Route
  - [ ] Pilih Produk
  - [ ] Input target_qty (dalam satuan bungkus)
  - [ ] Pilih week (week_start)
- [ ] `TargetService` — `GET/POST /api/v1/target`

### 7.8 Dashboard

- [ ] `DashboardModule` dengan lazy loading
- [ ] `DashboardComponent` — halaman utama setelah login, berisi:
  - [ ] **Header Info:** week aktif, total Sales aktif, total outlet hari ini
  - [ ] **Summary Cards (mat-card):**
    - [ ] Total kunjungan hari ini (dari `tr_outlet`)
    - [ ] Total penjualan hari ini (sum `tr_sales_detail.total`)
    - [ ] Sales yang belum pull stok hari ini
    - [ ] Sales dengan sync FAILED (dari monitoring)
  - [ ] **Bar Chart — Achievement vs Target:**
    - [ ] X-axis: nama Sales
    - [ ] Bar 1 (biru): total penjualan week ini (bungkus)
    - [ ] Bar 2 (merah): target week ini (bungkus)
    - [ ] Menggunakan `ng2-charts` + `Chart.js`
  - [ ] **Line Chart — Tren Kunjungan:**
    - [ ] X-axis: hari dalam week aktif (Sen, Sel, Rab, Kam, Jum, Sab)
    - [ ] Y-axis: jumlah outlet dikunjungi
    - [ ] Multiple line: satu line per Sales
  - [ ] **Tabel Ringkasan Sales Hari Ini:**
    - [ ] Kolom: nama Sales, outlet dikunjungi / total outlet, total penjualan, status stok
    - [ ] Klik baris → pergi ke halaman detail Sales
- [ ] `DashboardService` — aggregate data dari report endpoints

### 7.9 Laporan

- [ ] `ReportModule` dengan lazy loading
- [ ] **Laporan Kunjungan:**
  - [ ] `VisitReportComponent` — tabel `tr_outlet` dengan filter: Sales, tanggal range, visit_type
  - [ ] Kolom: Sales, outlet, jam mulai, jam selesai, durasi, GPS koordinat, visit type, status
  - [ ] Paginated (server-side pagination)
- [ ] **Laporan Penjualan:**
  - [ ] `SalesReportComponent` — tabel penjualan dengan filter: Sales, produk, tanggal range
  - [ ] Kolom: Sales, outlet, produk, qty, harga, total
  - [ ] Subtotal per Sales di bawah grup
  - [ ] Paginated (server-side pagination)
- [ ] **Laporan Achievement vs Target:**
  - [ ] `AchievementReportComponent` — tabel per Sales per produk
  - [ ] Kolom: Sales, Route, Produk, Target (bungkus), Achievement (bungkus), %, gap
  - [ ] Color coding: hijau >= 100%, kuning 80-99%, merah < 80%
- [ ] `ReportService` — call `GET /api/v1/report/*`

### 7.10 Testing & Validasi Web
- [ ] Test flow Spv: Login → buat Mapping → buat Stok → set Target → lihat Dashboard
- [ ] Test guard: akses URL tanpa login → redirect ke `/login`
- [ ] Test token refresh: tunggu 15 menit → lakukan action → verify auto-refresh
- [ ] Test chart: data kosong → empty state yang informatif
- [ ] Build production: `ng build --configuration=production` tanpa error
- [ ] Integrasi dengan Docker Compose (serve build output via Nginx)

---

## Progress Tracker

| Fase | Deskripsi | Status | % |
|---|---|---|---|
| Fase 1 | Database Server | `[x]` Selesai | 100% |
| Fase 2 | Backend Go API | `[x]` Selesai | 100% |
| Fase 3 | Android: Setup | `[x]` Selesai | 100% |
| Fase 4 | Android: UI/UX | `[x]` Selesai | 100% |
| Fase 5 | Android: Database (Room) | `[x]` Selesai | 100% |
| Fase 6 | Android: Business Logic | `[x]` Selesai | 100% |
| Fase 7 | Web Frontend | `[/]` Sedang dikerjakan | 20% |

---

*File ini diupdate setiap kali ada progress. Centang [x] pada task yang sudah selesai.*
