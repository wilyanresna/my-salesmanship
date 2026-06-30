# ARCHITECTURE.md
# Salesmanship — Dokumen Arsitektur Sistem

**Versi:** 1.0  
**Tanggal:** 2026-06-30  
**Referensi:** [PRD.md](PRD.md) · [data-model.md](data-model.md)

---

## 1. Gambaran Umum

Salesmanship adalah sistem distribusi rokok berbasis **offline-first** yang terdiri dari tiga komponen utama:

| Komponen | Teknologi | Pengguna |
|---|---|---|
| **Backend** | Go + Gin + GORM | — (server-side) |
| **Database** | PostgreSQL | — (server-side) |
| **Android App** | Kotlin + Jetpack Compose | Salesman (Sales) |
| **Web Frontend** | Angular *(On Hold)* | Supervisor (Spv) |

Semua komponen server berjalan di atas satu **VPS (self-hosted)** menggunakan **Docker Compose**.

---

## 2. Diagram Arsitektur

```
╔══════════════════════════════════════════════════════════════════╗
║                     VPS — Self-Hosted                           ║
║                  (Docker Compose Network)                       ║
║                                                                  ║
║  ┌─────────────────────────┐   ┌──────────────────────────────┐ ║
║  │   Go Backend (Gin)      │   │       PostgreSQL 16           │ ║
║  │   Container: api        │◄──►   Container: db              │ ║
║  │   Port: 8080 (internal) │   │   Port: 5432 (internal)      │ ║
║  │                         │   │                              │ ║
║  │   /api/v1/auth          │   │   Layer 1: Master            │ ║
║  │   /api/v1/master        │   │   Layer 2: Transaksi Server  │ ║
║  │   /api/v1/pull          │   │   Layer 3: Upload Android    │ ║
║  │   /api/v1/upload        │   │                              │ ║
║  │   /api/v1/report        │   └──────────────────────────────┘ ║
║  └────────────┬────────────┘                                     ║
║               │                                                  ║
║  ┌────────────▼────────────┐                                     ║
║  │   Nginx Reverse Proxy   │                                     ║
║  │   Container: nginx      │                                     ║
║  │   Port: 80, 443 (ext)   │                                     ║
║  └────────────┬────────────┘                                     ║
╚══════════════════════════════╪═══════════════════════════════════╝
                               │ HTTPS
               ┌───────────────┴────────────────┐
               │                                │
               ▼                                ▼
     ┌─────────────────┐            ┌─────────────────────────────┐
     │  Angular Web    │            │      Android App             │
     │  (On Hold)      │            │   Kotlin + Jetpack Compose  │
     │  Supervisor     │            │   Salesman                  │
     └─────────────────┘            │                             │
                                    │  ┌───────────────────────┐  │
                                    │  │    Room Database       │  │
                                    │  │    (SQLite — Local)    │  │
                                    │  │                        │  │
                                    │  │  Mst* Tables (pull)   │  │
                                    │  │  Tr* Tables (local)   │  │
                                    │  │  SyncQueue            │  │
                                    │  └───────────────────────┘  │
                                    │                             │
                                    │  ┌───────────────────────┐  │
                                    │  │    WorkManager         │  │
                                    │  │    (Background Sync)   │  │
                                    │  └───────────────────────┘  │
                                    └─────────────────────────────┘
```

---

## 3. Komponen Backend

### 3.1 Go + Gin Framework

```
cmd/
└── main.go                  # Entry point

internal/
├── config/                  # Konfigurasi environment (.env)
├── database/                # GORM connection, migrations, seeder
├── middleware/              # JWT auth, CORS, logging, rate limiter
├── domain/                  # Domain entities (structs + interfaces)
│   ├── employee.go
│   ├── outlet.go
│   ├── product.go
│   ├── stock.go
│   ├── transaction.go
│   └── ...
├── repository/              # Data access layer (GORM queries)
├── usecase/                 # Business logic
├── handler/                 # HTTP handlers (Gin route handlers)
│   ├── auth_handler.go
│   ├── master_handler.go
│   ├── pull_handler.go
│   ├── upload_handler.go
│   └── report_handler.go
└── router/                  # Route registration
    └── router.go
```

### 3.2 API Endpoint Groups

| Prefix | Middleware | Deskripsi |
|---|---|---|
| `POST /api/v1/auth/login` | — | Login Sales / Spv |
| `POST /api/v1/auth/refresh` | — | Refresh access token |
| `PUT  /api/v1/auth/password` | JWT | Ganti password |
| `GET/POST/PUT/DELETE /api/v1/master/*` | JWT + RoleSpv | CRUD Master Data (Employee, Outlet, dll.) |
| `GET/POST /api/v1/mapping/*` | JWT + RoleSpv | Weekly Mapping (MappingSpv, MappingSales, MappingOutlet) |
| `GET/POST /api/v1/stock/*` | JWT | StockRokok (POST=Spv, GET pull=Sales) |
| `GET/POST /api/v1/target/*` | JWT + RoleSpv | SalesTarget |
| `GET /api/v1/pull` | JWT + RoleSales | Pull semua data week aktif ke Android |
| `POST /api/v1/upload/visit` | JWT + RoleSales | Upload batch TrOutlet + children |
| `GET /api/v1/report/*` | JWT + RoleSpv | Query laporan kunjungan |

### 3.3 JWT Strategy

```
Login Request
    │
    ▼
Validate credentials (bcrypt hash)
    │
    ├── Success → Return:
    │       access_token  (exp: 15 menit)
    │       refresh_token (exp: 7 hari)
    │
    └── Failed → 401 Unauthorized

Every API Request:
    Header: Authorization: Bearer <access_token>
    │
    ├── Valid   → proceed
    ├── Expired → 401 → Android uses refresh_token
    └── Invalid → 401 → Force re-login
```

---

## 4. Komponen Android

### 4.1 Layer Architecture (Clean Architecture)

```
┌────────────────────────────────────────┐
│          UI Layer (Jetpack Compose)    │
│  Screens: Login, Dashboard, Visit,     │
│  Callsheet, StockCheck, SyncStatus     │
└────────────────┬───────────────────────┘
                 │ observes
┌────────────────▼───────────────────────┐
│         ViewModel Layer                │
│  LoginViewModel, DashboardViewModel,   │
│  VisitViewModel, SyncViewModel         │
└────────────────┬───────────────────────┘
                 │ calls
┌────────────────▼───────────────────────┐
│         UseCase / Repository Layer     │
│  PullDataUseCase, CreateVisitUseCase,  │
│  SyncUseCase, StockUseCase             │
└────────┬───────────────────┬───────────┘
         │ local             │ remote
┌────────▼────────┐  ┌───────▼──────────┐
│  Room Database  │  │  Retrofit API    │
│  (SQLite local) │  │  (HTTP Client)   │
└─────────────────┘  └──────────────────┘
```

### 4.2 WorkManager — Sync Strategy

```
Trigger: Sales klik "Selesai Kunjungan"
    │
    ▼
[UseCase] CreateVisitUseCase.closeVisit()
    1. TrOutlet.status = CLOSED
    2. Insert SyncQueue row:
       { entity_type: "TrOutlet", entity_id: X,
         batch_id: UUID.random(), status: PENDING }
    │
    ▼
[WorkManager] SyncWorker (triggered immediately)
    Constraints: NetworkType.CONNECTED
    │
    ├── Ambil semua SyncQueue WHERE status = PENDING
    │
    ├── Untuk setiap item:
    │     1. SET status = SYNCING
    │     2. Build payload:
    │        TrOutlet + TrCheckStock[] + TrSales[] + TrSalesDetail[]
    │     3. POST /api/v1/upload/visit
    │     4a. HTTP 200 → SET status = SYNCED
    │     4b. HTTP error → SET status = FAILED, increment retry_count
    │
    └── Schedule retry untuk FAILED items (exponential backoff)
```

### 4.3 Room Database Schema (Ringkasan)

| Layer | Tabel | Deskripsi |
|---|---|---|
| Master (pull) | MstSalesman | Info Sales + Spv + District |
| Master (pull) | MstProduct | Produk + UOM week ini |
| Master (pull) | MstOutlet | Outlet yang masuk route Sales |
| Master (pull) | Param | LOV global (notasi, visit type) |
| Master (pull) | StockRokok | Header alokasi stok harian |
| Master (pull) | StockRokokItem | Detail stok per SKU (multi-UOM) |
| Master (pull) | SalesTarget | Target mingguan per produk |
| Transaksi | TrOutlet | Header kunjungan per outlet |
| Transaksi | TrCheckStock | Cek stok per produk per kunjungan |
| Transaksi | TrSales | Header penjualan per kunjungan |
| Transaksi | TrSalesDetail | Detail penjualan per SKU |
| Utilitas | SyncQueue | Antrian sync ke server |

---

## 5. Deployment — Docker Compose

```yaml
# docker-compose.yml (ringkasan)
services:
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: salesmanship
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks: [internal]

  api:
    build: ./backend
    environment:
      DB_HOST: db
      DB_PORT: 5432
      JWT_SECRET: ${JWT_SECRET}
    depends_on: [db]
    networks: [internal]

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./nginx/ssl:/etc/nginx/ssl
    depends_on: [api]
    networks: [internal, external]

networks:
  internal:
  external:

volumes:
  postgres_data:
```

---

## 6. Simulasi Flow Data — End-to-End

### Flow 1: Persiapan Mingguan (Spv → Server)

```
Senin pagi, Spv login ke Web Dashboard
    │
    ├─[1]─► POST /api/v1/mapping/sales
    │        Body: { sales_id: 5, district_id: 3,
    │                week_start: "2026-06-30",
    │                week_end: "2026-07-06" }
    │        → DB: INSERT INTO mapping_sales
    │
    ├─[2]─► POST /api/v1/mapping/outlet
    │        Body: { outlet_id: 12, route_id: 7,
    │                week_start: "2026-06-30" }
    │        → DB: INSERT INTO mapping_outlet
    │
    ├─[3]─► POST /api/v1/stock/rokok
    │        Body: {
    │          sales_id: 5, date_used: "2026-06-30",
    │          items: [
    │            { product_id: 1, qty_dus_init: 2,
    │              qty_bal_init: 10, qty_slf_init: 5,
    │              qty_bks_init: 0 },
    │            { product_id: 2, qty_dus_init: 1, ... }
    │          ]
    │        }
    │        → DB: INSERT stock_rokok + stock_rokok_item
    │
    └─[4]─► POST /api/v1/target
             Body: { sales_id: 5, route_id: 7,
                     product_id: 1, target_qty: 50,
                     week_start: "2026-06-30" }
             → DB: INSERT INTO sales_target
```

### Flow 2: Pull Data (Sales Android → Server)

```
Senin pagi, Sales buka app → Dashboard kosong
    │
    ├─ Tap "Tarik Data"
    │
    └─► GET /api/v1/pull
         Header: Authorization: Bearer <token>
         │
         Server response:
         {
           salesman: { id, name, district_id, spv_name },
           outlets: [ { id, name, barcode, lat, lng, route_id } ],
           products: [ { id, name, sku, price, uom_bal, uom_slf, uom_bks } ],
           params: [ { group, key, value } ],
           stock_rokok: { id, date_used, status, items: [...] },
           targets: [ { route_id, product_id, target_qty } ]
         }
         │
         Android Room DB:
           INSERT INTO mst_salesman ...
           INSERT INTO mst_outlet (n rows) ...
           INSERT INTO mst_product (m rows) ...
           INSERT INTO param ...
           INSERT INTO stock_rokok + stock_rokok_item ...
           INSERT INTO sales_target ...
           └─ Pull selesai → Dashboard tampil data lengkap
```

### Flow 3: Kunjungan Outlet (Offline)

```
Sales tiba di Outlet A — tidak ada sinyal internet
    │
    ├─[1]─ Tap outlet dari list / Scan barcode Code-128
    │       Room DB: SELECT * FROM mst_outlet WHERE barcode = '...'
    │       Room DB: INSERT TrOutlet {
    │                 outlet_id: 12, visit_no: 1,
    │                 visit_type: NORMAL,
    │                 status: OPEN,
    │                 start_time: 2026-06-30T08:15:00,
    │                 lat: -6.2088, lng: 106.8456
    │               }
    │
    ├─[2]─ Input cek stok (per produk)
    │       Room DB: INSERT TrCheckStock {
    │                 tr_outlet_id: 1, product_id: 1,
    │                 stock_qty: 3
    │               }
    │
    ├─[3]─ Input penjualan
    │       Room DB: INSERT TrSales { tr_outlet_id: 1, sales_order: 'SO-001' }
    │       Room DB: INSERT TrSalesDetail {
    │                 tr_sales_id: 1, product_id: 1,
    │                 qty: 2, price: 25000, total: 50000
    │               }
    │       [Validasi] qty_sold <= sisa stok Sales
    │       → Update StockRokokItem.qty: kurangi qty yang terjual
    │
    └─[4]─ Tap "Selesai Kunjungan"
            Room DB: UPDATE TrOutlet SET
              status = CLOSED,
              end_time = 2026-06-30T08:45:00
            Room DB: INSERT SyncQueue {
              entity_type: 'TrOutlet', entity_id: 1,
              batch_id: 'uuid-xxxx-yyyy',
              status: PENDING
            }
```

### Flow 4: Auto-Sync (Android → Server)

```
Sales berjalan ke outlet berikutnya — sinyal masuk
    │
    WorkManager SyncWorker aktif (constraint: CONNECTED)
    │
    ├─ SELECT * FROM sync_queue WHERE status = 'PENDING'
    │   → [ { entity_type: 'TrOutlet', entity_id: 1, batch_id: 'uuid-xxxx' } ]
    │
    ├─ UPDATE sync_queue SET status = 'SYNCING' WHERE id = ...
    │
    ├─ Build payload:
    │   {
    │     batch_id: 'uuid-xxxx-yyyy',
    │     visit: {
    │       id: 1, outlet_id: 12, visit_no: 1,
    │       visit_type: 'NORMAL', status: 'CLOSED',
    │       start_time: '2026-06-30T08:15:00',
    │       end_time: '2026-06-30T08:45:00',
    │       lat: -6.2088, lng: 106.8456,
    │       check_stocks: [
    │         { product_id: 1, stock_qty: 3 }
    │       ],
    │       sales: [{
    │         sales_order: 'SO-001',
    │         details: [
    │           { product_id: 1, qty: 2, price: 25000, total: 50000 }
    │         ]
    │       }]
    │     }
    │   }
    │
    ├─► POST /api/v1/upload/visit
    │    Server:
    │      INSERT tr_outlet (batch_id, id, outlet_id, ..., uploaded_at=NOW())
    │      INSERT tr_check_stock (batch_id, ...)
    │      INSERT tr_sales + tr_sales_detail
    │      → HTTP 200 OK { message: 'synced' }
    │
    └─ UPDATE sync_queue SET status = 'SYNCED'

    [Jika gagal]
    └─ UPDATE sync_queue SET status = 'FAILED'
       WorkManager schedule retry (backoff: 1m → 5m → 15m → 30m)
```

### Flow 5: Monitoring Spv (Web — On Hold)

```
Spv buka Web Dashboard
    │
    └─► GET /api/v1/report/visits?sales_id=5&date=2026-06-30
         Server:
           SELECT tr_outlet.*, tr_check_stock.*, tr_sales.*
           FROM tr_outlet
           JOIN tr_check_stock ON ...
           JOIN tr_sales ON ...
           WHERE sales_id = 5 AND date(start_time) = '2026-06-30'
         │
         Response: daftar kunjungan + stok cek + penjualan
         → Tampil di tabel Web Dashboard
```

---

## 7. Prinsip Keamanan

| Aspek | Implementasi |
|---|---|
| **Autentikasi** | JWT Bearer Token, access 15 menit, refresh 7 hari |
| **Password** | Bcrypt hash (cost factor 12) |
| **HTTPS** | Nginx SSL/TLS termination (Let's Encrypt / wildcard cert) |
| **DB Password** | Tersimpan di `.env` file, tidak di-commit ke repo |
| **Rate Limiting** | Gin middleware rate limiter pada endpoint auth |
| **CORS** | Konfigurasi whitelist origin di Nginx + Gin |

---

## 8. Environment Variables

```env
# .env (tidak di-commit, hanya .env.example)
DB_HOST=db
DB_PORT=5432
DB_NAME=salesmanship
DB_USER=salesapp
DB_PASSWORD=<strong_password>
JWT_SECRET=<random_256bit_hex>
JWT_ACCESS_EXPIRY=15m
JWT_REFRESH_EXPIRY=168h
APP_PORT=8080
APP_ENV=production
```

---

*Dokumen ini adalah referensi teknis arsitektur. Update diperlukan jika ada perubahan fundamental pada tech stack atau desain sistem.*
