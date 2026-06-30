# DATABASE.md
# Salesmanship — Desain Database PostgreSQL

**Versi:** 1.0  
**Tanggal:** 2026-06-30  
**DBMS:** PostgreSQL 16  
**Referensi:** [PRD.md](PRD.md) · [data-model.md](data-model.md)

---

## Konvensi Penamaan

| Aturan | Contoh |
|---|---|
| Nama tabel: `snake_case` | `stock_rokok_item` |
| Nama kolom: `snake_case` | `week_start` |
| Primary Key: selalu `id BIGSERIAL` | — |
| Foreign Key: `<table_singular>_id` | `outlet_id`, `sales_id` |
| Timestamp: `created_at`, `updated_at` (auto) | — |
| Soft delete: `deleted_at TIMESTAMPTZ NULL` | — |
| Boolean: prefix `is_` | `is_active`, `is_out_of_route` |

---

## Layer 1 — Master Tables

### 1.1 `employees`

Menyimpan data semua pegawai: Supervisor (SPV) dan Salesman (SALES).

```sql
CREATE TABLE employees (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    nik         VARCHAR(30)   NOT NULL UNIQUE,
    username    VARCHAR(50)   NOT NULL UNIQUE,
    password    VARCHAR(255)  NOT NULL,            -- bcrypt hash
    position    VARCHAR(10)   NOT NULL              -- 'SPV' | 'SALES'
                CHECK (position IN ('SPV', 'SALES')),
    phone       VARCHAR(20),
    address     TEXT,
    is_active   BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMPTZ
);

CREATE INDEX idx_employees_username ON employees(username);
CREATE INDEX idx_employees_position ON employees(position);
```

**Sample Data:**

| id | name | nik | username | position | is_active |
|---|---|---|---|---|---|
| 1 | Budi Santoso | 1001 | budi.spv | SPV | true |
| 2 | Andi Wijaya | 2001 | andi.sales | SALES | true |
| 3 | Rini Susanti | 2002 | rini.sales | SALES | true |

---

### 1.2 `areas`

```sql
CREATE TABLE areas (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
```

**Sample Data:**

| id | name |
|---|---|
| 1 | Jabodetabek |
| 2 | Jawa Barat |

---

### 1.3 `territories`

```sql
CREATE TABLE territories (
    id          BIGSERIAL PRIMARY KEY,
    area_id     BIGINT       NOT NULL REFERENCES areas(id),
    name        VARCHAR(100) NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_territories_area ON territories(area_id);
```

**Sample Data:**

| id | area_id | name |
|---|---|---|
| 1 | 1 | Jakarta Selatan |
| 2 | 1 | Jakarta Timur |

---

### 1.4 `districts`

```sql
CREATE TABLE districts (
    id           BIGSERIAL PRIMARY KEY,
    territory_id BIGINT       NOT NULL REFERENCES territories(id),
    name         VARCHAR(100) NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_districts_territory ON districts(territory_id);
```

**Sample Data:**

| id | territory_id | name |
|---|---|---|
| 1 | 1 | District Kebayoran |
| 2 | 1 | District Cilandak |

---

### 1.5 `routes`

Route merepresentasikan hari kunjungan dalam satu district. Satu route = satu hari kerja.

```sql
CREATE TABLE routes (
    id          BIGSERIAL PRIMARY KEY,
    district_id BIGINT       NOT NULL REFERENCES districts(id),
    name        VARCHAR(100) NOT NULL,    -- e.g. 'Route 1 - Senin'
    day_of_week SMALLINT     NOT NULL     -- 1=Senin ... 7=Minggu
                CHECK (day_of_week BETWEEN 1 AND 7),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_routes_district ON routes(district_id);
```

**Sample Data:**

| id | district_id | name | day_of_week |
|---|---|---|---|
| 1 | 1 | Route 1 - Senin | 1 |
| 2 | 1 | Route 2 - Selasa | 2 |
| 3 | 1 | Route 3 - Rabu | 3 |

---

### 1.6 `outlets`

```sql
CREATE TABLE outlets (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(150) NOT NULL,
    owner_name   VARCHAR(100),
    phone        VARCHAR(20),
    address      TEXT,
    lat          DECIMAL(10, 7),             -- GPS latitude master
    lng          DECIMAL(10, 7),             -- GPS longitude master
    barcode      VARCHAR(100) UNIQUE,        -- Code-128 format
    outlet_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                  CHECK (outlet_status IN ('ACTIVE', 'INACTIVE', 'POTENTIAL')),
    call_cycle   VARCHAR(10) NOT NULL DEFAULT 'WEEKLY'
                  CHECK (call_cycle IN ('DAILY', 'WEEKLY', 'BIWEEKLY')),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at   TIMESTAMPTZ
);

CREATE INDEX idx_outlets_barcode ON outlets(barcode);
CREATE INDEX idx_outlets_status  ON outlets(outlet_status);
```

**Sample Data:**

| id | name | owner_name | barcode | lat | lng | outlet_status |
|---|---|---|---|---|---|---|
| 1 | Toko Mawar | Pak Joko | OTL-0001 | -6.2614 | 106.8106 | ACTIVE |
| 2 | Warung Melati | Bu Sari | OTL-0002 | -6.2650 | 106.8120 | ACTIVE |
| 3 | Toko Sejahtera | Pak Bowo | OTL-0003 | -6.2590 | 106.8090 | ACTIVE |

---

### 1.7 `products`

Faktor konversi UOM: `uom_bal` = jumlah BAL per DUS, `uom_slf` = jumlah SLOP per BAL, `uom_bks` = jumlah BUNGKUS per SLOP.

```sql
CREATE TABLE products (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(150)    NOT NULL,
    sku         VARCHAR(50)     NOT NULL UNIQUE,
    price       NUMERIC(12, 2)  NOT NULL,       -- harga per bungkus
    uom_bal     SMALLINT        NOT NULL,        -- BAL per DUS
    uom_slf     SMALLINT        NOT NULL,        -- SLOP per BAL
    uom_bks     SMALLINT        NOT NULL,        -- BUNGKUS per SLOP
    is_active   BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_products_sku ON products(sku);
```

**Sample Data:**

| id | name | sku | price | uom_bal | uom_slf | uom_bks |
|---|---|---|---|---|---|---|
| 1 | Djarum Super 12 | DJ-SUP-12 | 2.500 | 20 | 10 | 12 |
| 2 | LA Bold 16 | LA-BLD-16 | 2.800 | 20 | 10 | 16 |
| 3 | Gudang Garam Merah | GG-MERAH | 2.200 | 25 | 10 | 12 |

> **Catatan:** 1 DUS Djarum Super = 20 BAL × 10 SLOP × 12 BUNGKUS = 2.400 bungkus

---

### 1.8 `params`

Semua LOV (List of Values) disimpan di sini. Bersifat global, tidak terikat week.

```sql
CREATE TABLE params (
    id          BIGSERIAL PRIMARY KEY,
    group_name  VARCHAR(50)  NOT NULL,     -- e.g. 'VISIT_TYPE', 'NOTATION'
    key         VARCHAR(50)  NOT NULL,
    value       VARCHAR(200) NOT NULL,
    description TEXT,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    UNIQUE (group_name, key)
);

CREATE INDEX idx_params_group ON params(group_name);
```

**Sample Data:**

| id | group_name | key | value | description |
|---|---|---|---|---|
| 1 | VISIT_TYPE | NORMAL | Normal | Kunjungan dalam route |
| 2 | VISIT_TYPE | OUT_OF_ROUTE | Luar Route | Kunjungan di luar route |
| 3 | NOTATION | NORMAL | Normal | Distribusi normal |
| 4 | NOTATION | OOS | Out of Stock | Outlet kehabisan stok |
| 5 | NOTATION | WHOLESALE | Grosir | Outlet melayani grosir |
| 6 | STOCK_STATUS | DRAFT | Draft | Belum siap |
| 7 | STOCK_STATUS | READY | Ready | Siap ditarik Sales |
| 8 | STOCK_STATUS | PULLED | Pulled | Sudah ditarik Sales |
| 9 | STOCK_STATUS | CLOSED | Closed | Selesai |

---

## Layer 2 — Transaksi Server (Weekly Mapping + Stok + Target)

### 2.1 `mapping_spv`

Mapping Supervisor ke Territory, bersifat weekly.

```sql
CREATE TABLE mapping_spv (
    id           BIGSERIAL PRIMARY KEY,
    spv_id       BIGINT      NOT NULL REFERENCES employees(id),
    territory_id BIGINT      NOT NULL REFERENCES territories(id),
    week_start   DATE        NOT NULL,
    week_end     DATE        NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (spv_id, territory_id, week_start)
);

CREATE INDEX idx_mapping_spv_week ON mapping_spv(week_start, week_end);
```

**Sample Data:**

| id | spv_id | territory_id | week_start | week_end |
|---|---|---|---|---|
| 1 | 1 | 1 | 2026-06-29 | 2026-07-05 |

---

### 2.2 `mapping_sales`

Mapping Sales ke Spv + District, bersifat weekly.

```sql
CREATE TABLE mapping_sales (
    id          BIGSERIAL PRIMARY KEY,
    spv_id      BIGINT      NOT NULL REFERENCES employees(id),
    sales_id    BIGINT      NOT NULL REFERENCES employees(id),
    district_id BIGINT      NOT NULL REFERENCES districts(id),
    week_start  DATE        NOT NULL,
    week_end    DATE        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (sales_id, week_start)
);

CREATE INDEX idx_mapping_sales_week  ON mapping_sales(week_start, week_end);
CREATE INDEX idx_mapping_sales_sales ON mapping_sales(sales_id);
```

**Sample Data:**

| id | spv_id | sales_id | district_id | week_start | week_end |
|---|---|---|---|---|---|
| 1 | 1 | 2 | 1 | 2026-06-29 | 2026-07-05 |
| 2 | 1 | 3 | 2 | 2026-06-29 | 2026-07-05 |

---

### 2.3 `mapping_outlet`

Mapping Outlet ke Route, bersifat weekly. Memungkinkan outlet berpindah route antar week.

```sql
CREATE TABLE mapping_outlet (
    id          BIGSERIAL PRIMARY KEY,
    outlet_id   BIGINT      NOT NULL REFERENCES outlets(id),
    route_id    BIGINT      NOT NULL REFERENCES routes(id),
    week_start  DATE        NOT NULL,
    week_end    DATE        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (outlet_id, week_start)
);

CREATE INDEX idx_mapping_outlet_route ON mapping_outlet(route_id);
CREATE INDEX idx_mapping_outlet_week  ON mapping_outlet(week_start);
```

**Sample Data:**

| id | outlet_id | route_id | week_start | week_end |
|---|---|---|---|---|
| 1 | 1 | 1 | 2026-06-29 | 2026-07-05 |
| 2 | 2 | 1 | 2026-06-29 | 2026-07-05 |
| 3 | 3 | 2 | 2026-06-29 | 2026-07-05 |

---

### 2.4 `stock_rokok`

Header alokasi stok rokok per Sales per hari kerja.

```sql
CREATE TABLE stock_rokok (
    id          BIGSERIAL PRIMARY KEY,
    sales_id    BIGINT      NOT NULL REFERENCES employees(id),
    date_used   DATE        NOT NULL,
    status      VARCHAR(10) NOT NULL DEFAULT 'DRAFT'
                CHECK (status IN ('DRAFT', 'READY', 'PULLED', 'CLOSED')),
    created_by  BIGINT      NOT NULL REFERENCES employees(id),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (sales_id, date_used)
);

CREATE INDEX idx_stock_rokok_sales_date ON stock_rokok(sales_id, date_used);
CREATE INDEX idx_stock_rokok_status     ON stock_rokok(status);
```

**Sample Data:**

| id | sales_id | date_used | status | created_by |
|---|---|---|---|---|
| 1 | 2 | 2026-06-30 | READY | 1 |
| 2 | 3 | 2026-06-30 | READY | 1 |

---

### 2.5 `stock_rokok_item`

Detail stok per SKU. Kolom `qty_*_init` = stok awal yang diberikan Spv.

```sql
CREATE TABLE stock_rokok_item (
    id              BIGSERIAL PRIMARY KEY,
    stock_rokok_id  BIGINT         NOT NULL REFERENCES stock_rokok(id) ON DELETE CASCADE,
    product_id      BIGINT         NOT NULL REFERENCES products(id),
    qty_dus_init    SMALLINT       NOT NULL DEFAULT 0,
    qty_bal_init    SMALLINT       NOT NULL DEFAULT 0,
    qty_slf_init    SMALLINT       NOT NULL DEFAULT 0,
    qty_bks_init    SMALLINT       NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    UNIQUE (stock_rokok_id, product_id)
);

CREATE INDEX idx_stock_item_rokok ON stock_rokok_item(stock_rokok_id);
```

**Sample Data:**

| id | stock_rokok_id | product_id | qty_dus | qty_bal | qty_slf | qty_bks |
|---|---|---|---|---|---|---|
| 1 | 1 | 1 | 2 | 5 | 3 | 0 |
| 2 | 1 | 2 | 1 | 10 | 0 | 0 |
| 3 | 1 | 3 | 3 | 0 | 0 | 0 |

> **Interpretasi:** Sales Andi membawa 2 DUS + 5 BAL + 3 SLOP Djarum Super hari ini.

---

### 2.6 `sales_target`

Target penjualan per Sales per Route per Produk, bersifat weekly.

```sql
CREATE TABLE sales_target (
    id          BIGSERIAL PRIMARY KEY,
    sales_id    BIGINT         NOT NULL REFERENCES employees(id),
    route_id    BIGINT         NOT NULL REFERENCES routes(id),
    product_id  BIGINT         NOT NULL REFERENCES products(id),
    target_qty  INTEGER        NOT NULL,          -- dalam satuan BUNGKUS
    week_start  DATE           NOT NULL,
    week_end    DATE           NOT NULL,
    created_by  BIGINT         NOT NULL REFERENCES employees(id),
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    UNIQUE (sales_id, route_id, product_id, week_start)
);

CREATE INDEX idx_target_sales_week ON sales_target(sales_id, week_start);
CREATE INDEX idx_target_route      ON sales_target(route_id);
```

**Sample Data:**

| id | sales_id | route_id | product_id | target_qty | week_start |
|---|---|---|---|---|---|
| 1 | 2 | 1 | 1 | 100 | 2026-06-29 |
| 2 | 2 | 1 | 2 | 80 | 2026-06-29 |
| 3 | 2 | 2 | 1 | 90 | 2026-06-29 |

---

## Layer 3 — Transaksi Upload dari Android

Semua tabel di layer ini memiliki `batch_id` (UUID dari Android) dan `uploaded_at`.

### 3.1 `tr_outlet`

Header kunjungan per outlet. Satu kunjungan = satu row.

```sql
CREATE TABLE tr_outlet (
    id              BIGSERIAL PRIMARY KEY,
    batch_id        UUID           NOT NULL,           -- UUID dari Android
    sales_id        BIGINT         NOT NULL REFERENCES employees(id),
    outlet_id       BIGINT         NOT NULL REFERENCES outlets(id),
    visit_no        SMALLINT       NOT NULL DEFAULT 1, -- urutan kunjungan ke outlet ini hari ini
    visit_type      VARCHAR(20)    NOT NULL DEFAULT 'NORMAL'
                    CHECK (visit_type IN ('NORMAL', 'OUT_OF_ROUTE')),
    status          VARCHAR(10)    NOT NULL DEFAULT 'CLOSED',
    start_time      TIMESTAMPTZ    NOT NULL,
    end_time        TIMESTAMPTZ,
    lat             DECIMAL(10, 7),                    -- GPS aktual saat kunjungan
    lng             DECIMAL(10, 7),
    uploaded_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    UNIQUE (batch_id)
);

CREATE INDEX idx_tr_outlet_sales      ON tr_outlet(sales_id);
CREATE INDEX idx_tr_outlet_outlet     ON tr_outlet(outlet_id);
CREATE INDEX idx_tr_outlet_start_time ON tr_outlet(start_time);
CREATE INDEX idx_tr_outlet_batch      ON tr_outlet(batch_id);
```

**Sample Data:**

| id | batch_id | sales_id | outlet_id | visit_type | start_time | end_time | lat | lng |
|---|---|---|---|---|---|---|---|---|
| 1 | uuid-a1b2 | 2 | 1 | NORMAL | 2026-06-30 08:15:00+07 | 2026-06-30 08:45:00+07 | -6.2614 | 106.8106 |
| 2 | uuid-c3d4 | 2 | 2 | NORMAL | 2026-06-30 09:10:00+07 | 2026-06-30 09:35:00+07 | -6.2650 | 106.8120 |

---

### 3.2 `tr_check_stock`

Hasil cek stok per produk dalam satu kunjungan.

```sql
CREATE TABLE tr_check_stock (
    id          BIGSERIAL PRIMARY KEY,
    batch_id    UUID           NOT NULL,
    tr_outlet_id BIGINT        NOT NULL REFERENCES tr_outlet(id) ON DELETE CASCADE,
    product_id  BIGINT         NOT NULL REFERENCES products(id),
    stock_qty   INTEGER        NOT NULL DEFAULT 0,  -- stok yang ada di outlet (bungkus)
    uploaded_at TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    UNIQUE (tr_outlet_id, product_id)
);

CREATE INDEX idx_tr_check_outlet  ON tr_check_stock(tr_outlet_id);
CREATE INDEX idx_tr_check_product ON tr_check_stock(product_id);
```

**Sample Data:**

| id | tr_outlet_id | product_id | stock_qty |
|---|---|---|---|
| 1 | 1 | 1 | 3 |
| 2 | 1 | 2 | 0 |
| 3 | 1 | 3 | 7 |

> **Interpretasi:** Di Toko Mawar — Djarum Super sisa 3 bungkus, LA Bold habis (OOS), GG Merah sisa 7 bungkus.

---

### 3.3 `tr_sales`

Header penjualan. Dalam satu kunjungan bisa ada lebih dari satu header sales (misal: transaksi diedit/ditambah).

```sql
CREATE TABLE tr_sales (
    id           BIGSERIAL PRIMARY KEY,
    batch_id     UUID           NOT NULL,
    tr_outlet_id BIGINT         NOT NULL REFERENCES tr_outlet(id) ON DELETE CASCADE,
    sales_order  VARCHAR(50)    NOT NULL,           -- nomor SO yang dibuat Android
    uploaded_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    UNIQUE (batch_id, sales_order)
);

CREATE INDEX idx_tr_sales_outlet ON tr_sales(tr_outlet_id);
```

**Sample Data:**

| id | tr_outlet_id | sales_order |
|---|---|---|
| 1 | 1 | SO-20260630-001 |
| 2 | 2 | SO-20260630-002 |

---

### 3.4 `tr_sales_detail`

Detail penjualan per SKU. `price` adalah snapshot harga saat transaksi.

```sql
CREATE TABLE tr_sales_detail (
    id          BIGSERIAL PRIMARY KEY,
    batch_id    UUID           NOT NULL,
    tr_sales_id BIGINT         NOT NULL REFERENCES tr_sales(id) ON DELETE CASCADE,
    product_id  BIGINT         NOT NULL REFERENCES products(id),
    qty         INTEGER        NOT NULL,
    price       NUMERIC(12, 2) NOT NULL,    -- snapshot harga saat transaksi
    total       NUMERIC(14, 2) NOT NULL,    -- qty × price
    uploaded_at TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tr_sales_detail_sales   ON tr_sales_detail(tr_sales_id);
CREATE INDEX idx_tr_sales_detail_product ON tr_sales_detail(product_id);
```

**Sample Data:**

| id | tr_sales_id | product_id | qty | price | total |
|---|---|---|---|---|---|
| 1 | 1 | 1 | 24 | 2500.00 | 60000.00 |
| 2 | 1 | 3 | 12 | 2200.00 | 26400.00 |

> **Interpretasi:** Di Toko Mawar, Sales Andi menjual 24 bungkus Djarum Super (Rp 60.000) dan 12 bungkus GG Merah (Rp 26.400). Total transaksi: Rp 86.400.

---

## Ringkasan Semua Tabel

| # | Tabel | Layer | Baris Estimasi |
|---|---|---|---|
| 1 | `employees` | Master | ~50 |
| 2 | `areas` | Master | ~10 |
| 3 | `territories` | Master | ~30 |
| 4 | `districts` | Master | ~100 |
| 5 | `routes` | Master | ~500 |
| 6 | `outlets` | Master | ~5.000 |
| 7 | `products` | Master | ~50 |
| 8 | `params` | Master | ~30 |
| 9 | `mapping_spv` | Transaksi Weekly | ~10/week |
| 10 | `mapping_sales` | Transaksi Weekly | ~50/week |
| 11 | `mapping_outlet` | Transaksi Weekly | ~5.000/week |
| 12 | `stock_rokok` | Transaksi Daily | ~250/week |
| 13 | `stock_rokok_item` | Transaksi Daily | ~2.500/week |
| 14 | `sales_target` | Transaksi Weekly | ~1.500/week |
| 15 | `tr_outlet` | Upload Android | ~2.500/week |
| 16 | `tr_check_stock` | Upload Android | ~12.500/week |
| 17 | `tr_sales` | Upload Android | ~2.500/week |
| 18 | `tr_sales_detail` | Upload Android | ~12.500/week |

---

## Migration Order (GORM AutoMigrate)

Urutan migrasi harus mengikuti dependency foreign key:

```
1.  areas
2.  territories          ← depends: areas
3.  districts            ← depends: territories
4.  routes               ← depends: districts
5.  outlets
6.  products
7.  params
8.  employees
9.  mapping_spv          ← depends: employees, territories
10. mapping_sales         ← depends: employees, districts
11. mapping_outlet        ← depends: outlets, routes
12. stock_rokok           ← depends: employees
13. stock_rokok_item      ← depends: stock_rokok, products
14. sales_target          ← depends: employees, routes, products
15. tr_outlet             ← depends: employees, outlets
16. tr_check_stock        ← depends: tr_outlet, products
17. tr_sales              ← depends: tr_outlet
18. tr_sales_detail       ← depends: tr_sales, products
```

---

## Seed Data Required

Data berikut harus tersedia sebelum aplikasi bisa digunakan:

1. **`params`** — semua LOV (VISIT_TYPE, NOTATION, STOCK_STATUS)
2. **`products`** — SKU rokok yang akan didistribusikan
3. **`areas`**, **`territories`**, **`districts`**, **`routes`** — hierarki wilayah
4. **`employees`** — minimal 1 SPV dan 1 SALES untuk testing
5. **`outlets`** — daftar outlet awal

---

*Database ini didesain untuk PostgreSQL 16. Tidak kompatibel langsung dengan SQLite (Android Room DB — schema berbeda, lihat data-model.md).*
