# Analisis Aplikasi Salesmanship Lama
# Referensi Database & Arsitektur

**Sumber:** `docs/reference/DatabaseHandler.java`
**DB Version Terakhir:** 49 (versi app ~3.22.0)
**Periode Pengembangan:** 2016 — 2024+
**Enkripsi DB:** SQLCipher (database Android terenkripsi)

---

## 1. Gambaran Umum Aplikasi Lama

Aplikasi lama adalah **enterprise-grade SFA (Sales Force Automation)** skala besar, kemungkinan untuk distributor rokok nasional. Dalam 8+ tahun pengembangan, aplikasi ini tumbuh menjadi platform yang sangat kompleks dengan ~150+ tabel SQLite di sisi Android. Ini bukan sekadar aplikasi distribusi sederhana — ini sistem penuh yang mencakup:

- Manajemen distribusi rokok (core)
- Warehouse Management System (WMS) terintegrasi
- Program promosi & eksekusinya
- Tanda terima outlet digital (kontrak)
- Maintenance unit display
- Pembayaran cashless (LinkAja, Virtual Account)
- Survey volume & aktivitas kompetitor
- Tracking KPI Sales
- Realisasi biaya operasional

---

## 2. Inventaris Tabel — Dikelompokkan per Domain

### 2.1 Master Data

| Tabel | Fungsi | Kolom Kunci |
|---|---|---|
| `MST_SALESMAN` | Data Sales | USERNAME, SALESMAN_NAME, REGION, AREA, TERITORY, DISTRICT_ID, DISTRICT_CODE, ROUTE, NIK, AO, EMAIL, SPV_ID, SPV_NAME, WEEK, FLAG_GK, IS_STOCKIST |
| `MST_SALESMAN_ROUTE` | Mapping Sales ke Route (bisa multi-route) | SALESMAN_ID, AO_ID, AO_CODE, OU_ID, OU_CODE, TERRITORY_ID, DISTRICT_ID, ROUTE_ID, COVERAGE, SUBCOVERAGE, POSITION_NAME |
| `MST_OUTLET` | Data Outlet (45+ kolom) | OUTLET_ID, AREA, TERITORY, DISTRICT_ID, ROUTE, OUTLET_NAME, BARCODE_ID, OUTLET_STATUS, TIPE_OUTLET, OWNER_NAME, LONGITUDE, LATITUDE, KELURAHAN, ADDRESS, FLAG_STOCKIST, COVERAGE_TYPE, FLAG_BARCODE, ORACLE_ID, COUNTY_ID, SUBDISTRICT_ID, VILLAGE_ID, CALL_CYCLE, OTL_AMBASSADOR_STATUS_ID |
| `MST_PRODUCT` | SKU Rokok | PRODUCT_ID, PRODUCT_CODE, PRODUCT_NAME, PRODUCT_SEQN, PRICE, AREA, FLAG_RIS, IS_DUS, UOM_BAL, UOM_SLF, UOM_BKS |
| `MST_PRODUCT_PRICE` | Harga produk per tipe | PRODUCT_ID, PRICE_BAND, PRICE_PACK, TYPE_PRICE_ID, WEEK |
| `MST_TARGET` | Target penjualan per route per produk | WEEK, AREA, DISTRICT_ID, ROUTE, PARAMETER_ID, PARAMETER_NAME, TARGET, ACHIEVEMENT, PERCENT, PRODUCT_ID |
| `MST_HIS_CALLSHEET` | History callsheet minggu lalu | DISTRICT_ID, ROUTE, OUTLET_ID, PRODUCT_ID, WEEK, STOCK, BUY, DISTRIBUTION_NOTATION |
| `MST_OUTLET_TYPE` | Tipe outlet (LOV) | OUTLET_TYPE_ID, OUTLET_TYPE_NAME |
| `MST_STATUS_OUTLET` | Status outlet (LOV) | STATUS_OUTLET_ID, NAME |
| `MST_NOTATION` | Notasi distribusi (LOV) | NOTATION_ID, NAME, DESCRIPTION |
| `MST_NON_VISIBLE` | Alasan non-visible (LOV) | NON_VISIBLE_ID, NAME |
| `MST_VISIBILITY_DOMINAN` | Tipe dominansi visibilitas (LOV) | VISIBILITY_ID, NAME |
| `MST_REASON_BARCODE` | Alasan tidak scan barcode | REASON_ID, REASON_NAME |
| `MST_SALES_TYPE` | Tipe penjualan (LOV) | SALES_TYPE_ID, NAME |
| `MST_ADJUSTMENT_TYPE` | Tipe adjustment stok (LOV) | ADJUSTMENT_ID, NAME |
| `MST_VILLAGE` | Data kelurahan/desa | COUNTY_ID, COUNTY_NAME, SUBDISTRICT_ID, SUBDISTRICT_NAME, VILLAGE_ID, VILLAGE_NAME, PROVINCE_ID |
| `MST_VEHICLE` | Data kendaraan Sales | VEHICLE_DESC, POLICE_NO, AO_CODE, OU_CODE, WEEK |
| `MST_OUTLET_DEDICATED` | Mapping outlet ke brand dedicated | OUTLET_ID, FOCUS_NAME, BRAND_CODE |
| `MST_OUTLET_INVEST` | Rencana investasi unit di outlet | PLAN_ID, OUTLET_ID, BRAND_CODE, ITEM_GROUP_ID, QTY, WEEK |
| `MST_PARAM` / `MST_PARAM_GLOBAL` | Parameter konfigurasi aplikasi | GROUP_PARAM, PARAM_NAME, REMARK, WEEK |
| `MST_PARAMETER_VERSION` | Versi parameter untuk sync | VERSION |
| `MST_PP_PROGRAM` | Program promosi PP | PP_PROGRAM_ID, PROGRAM_NAME, BRAND_ID, DATE_CREATED, FLAG_COA, IS_OPEN, IS_CONTRACT |
| `MST_BRAND_PROG` | Brand untuk program promosi | BRAND_ID, BRAND_NAME, FLAG_DEV, VISIBILITY_ID, POSM |
| `MST_BRAND_DEDICATED` | Brand dedicated per outlet | OU_CODE, TERRITORY_CODE, DISTRICT_CODE, BRAND_DEDICATED, BRAND_SAFETYNET |
| `MST_MAP_POSM` | Mapping POSM per route | OU_CODE, TERRITORY_CODE, DISTRICT_CODE, ROUTE, TOTAL_BRAND, BRAND_ID |
| `MST_CALLCYCLE` | Siklus kunjungan outlet | - |
| `MST_ITEM_SUBCATEGORY` / `MST_ITEM_GROUP` | Kategori item display unit | ITEM_CATEGORY_ID, ITEM_SUBCATEGORY_ID, ITEM_GROUP_ID |
| `MST_HIS_MAINTENANCE` | History maintenance unit | OUTLET_ID, ITEM_GROUP_ID, QTY_CONTRACT, QTY_ACTUAL, PLANOGRAM, CLEAN, POSITION |

---

### 2.2 Transaksi Inti (Core MVP)

| Tabel | Fungsi | Kolom Kunci Penting |
|---|---|---|
| `TR_NOTATION` | **Callsheet** — aktivitas utama per outlet per produk | DISTRICT_ID, ROUTE, OUTLET_ID, PRODUCT_ID, **STOCK** (cek stok), **BUY_SA** (penjualan), FACEUP, WHOLESALE_SCHEDULE, OOS_DURATION, DISTRIBUTION_NOTATION |
| `TEMP_TR_NOTATION` | Buffer sementara callsheet sebelum final | Struktur sama dengan TR_NOTATION |
| `TR_SALES` | Detail penjualan per produk per outlet | SALES_TYPE, SALESMAN_ID, DISTRICT_ID, ROUTE, OUTLET_ID, BARCODE_ID, PRODUCT_ID, QTY, BANDEROLE, PRICE, TOTAL_PRICE, BPPR_NO, NOTE_CODE, STATUS |
| `TR_SALES_TEMP` | Temp sales sebelum dikonfirmasi | Struktur sama dengan TR_SALES |
| `TR_RETUR_RECAP` | Rekap retur per produk | PRODUCT_ID, PRODUCT_CODE, QTY, BANDEROLE |
| `TR_DURATION_NOTES` | **Waktu kunjungan per outlet** | DISTRICT_ID, ROUTE, OUTLET_ID, **CALL_START**, **CALL_END**, **DURATION**, **ACTUAL_LONG**, **ACTUAL_LAT**, DISTANCE, NOTES |
| `START_END_ROUTE` | **Waktu mulai & selesai route harian** | OU_CODE, TERRITORY_CODE, DISTRICT_CODE, ROUTE, **TIME_START**, **TIME_END**, **LONGITUDE_START**, **LATITUDE_START**, KM_AWAL, KM_AKHIR, WEEK, GMT |
| `TR_NOTES_ROUTE` | Catatan free-text Sales per route | DISTRICT_ID, ROUTE, NOTES |
| `TR_DOWNLOAD` | Status download data per route | DISTRICT_ID, ROUTE, FLAG_SELECTED, FLAG_PRODUCT, FLAG_OUTLET, FLAG_TARGET, FLAG_HISCALLSHEET, FLAG_UPLOADED |
| `TWEEK` | Mapping date → nomor week | TRANSDATE, WEEK_NO |
| `TR_INBOX` | Notifikasi/pesan dari server | MESSAGE_TYPE, STATUS, MESSAGE, DOWNLOAD_DATE |

---

### 2.3 WMS — Warehouse Management System

| Tabel | Fungsi |
|---|---|
| `TR_BPPR` | Header dokumen BPPR (Bon Pengiriman Pengembalian Rokok) |
| `TR_STOCK_ROKOK` | Stok rokok per BPPR — **multi-UOM**: STOCK_INIT_DUS, STOCK_INIT_BAL, STOCK_INIT_SLF, STOCK_INIT_BKS + versi final good |
| `TR_ADJUST_TKRGLG` | Adjustment stok TKR-GLG (kelebihan/kekurangan) |
| `TR_TOPPING_UP` | Penambahan stok di tengah distribusi |
| `TR_BPPR_BAD_STOCK` | Stok rusak/bad stock |
| `TR_BPPR_DELIVER_ORDER` | Surat jalan pengiriman stok |
| `TR_BPPM` | Header BPPM (Bukti Pengeluaran Promosi Materi) |
| `TR_BPPM_DTL` | Detail item BPPM |
| `TR_BPPM_ALOCATION` | Alokasi BPPM ke program PP |
| `TR_BPPU` | BPPU (Bukti Pengeluaran Promosi Uang) |
| `TR_BPPU_ALOCATION` | Alokasi BPPU ke program PP |
| `TR_PU_HDR` / `TR_PU_DTL` | Pickup Unit — pengambilan display unit |
| `TR_PU_ALLOCATION` | Alokasi PU ke program PP |

> **Catatan:** Semua tabel WMS memiliki tabel `_HIST` (history/backup) untuk audit trail.

---

### 2.4 Program Promosi

| Tabel | Fungsi |
|---|---|
| `TR_PP_PROG_EXEC` | Eksekusi program promosi PP di outlet |
| `TR_NONPP_PROG_EXEC` | Eksekusi program promosi Non-PP |
| `TR_COMPENSATION` | Kompensasi produk dari program PP |
| `TR_PROG_PHOTO` | Foto bukti program PP di outlet |
| `TR_BPPM_ACV` | Achievement/realisasi BPPM |
| `TR_PU_ACV` | Achievement Pickup Unit |

---

### 2.5 Tanda Terima Outlet (TTO) — Kontrak Digital

| Tabel | Fungsi |
|---|---|
| `TR_TTO_HDR` | Header kontrak TTO | 
| `TR_TTO_DTL` | Detail item kontrak |
| `TR_TTO_CONTRACT` | Klausul kontrak |
| `TR_TTO_RECEIVER` | Penerima manfaat |
| `TR_TTO_SIGNATURE` | Tanda tangan digital |
| `TR_TTO_PRODUCT_DISPLAY` | Produk dalam kontrak display |
| `TR_TTO_UNIT_DISPLAY` | Unit display dalam kontrak |
| `MST_OUTLET_OWNER` | Data pemilik outlet (KTP, NPWP, dll.) |
| `MST_OI_PKS` / `MST_OI_PKS_DTL` | Outlet Investment PKS |

---

### 2.6 Maintenance Unit Display

| Tabel | Fungsi |
|---|---|
| `TR_MAINTENANCE` | Maintenance unit display di outlet |
| `TR_PHOTO_MAINTENANCE` | Foto maintenance unit |
| `TR_ADDT_UNIT` | Penambahan unit baru |
| `MST_ACTUAL_UNIT_SUPPORT` | Realisasi unit support di outlet |

---

### 2.7 Survey & Competitor Activity (CR Nirwana)

| Tabel | Fungsi |
|---|---|
| `TR_SVY_VOLUME` | Survey volume penjualan outlet per produk |
| `TR_COMP_PROG` | Program kompetitor |
| `TR_COMP_EVENT` | Event kompetitor |
| `TR_COMP_EVENT_DTL` | Detail event kompetitor |
| `TR_COMP_EVENT_PHOTOS` | Foto event kompetitor |
| `TR_COMP_PROG_FEE` / `_UNIT` / `_CIG` | Aktivitas kompetitor detail |

---

### 2.8 KPI Retail

| Tabel | Fungsi |
|---|---|
| `TR_ACHIEVEMENT_HDR` / `_DTL` | Achievement KPI Sales |
| `TR_ACHIEVEMENT_DTL_OTL` | Achievement per outlet |
| `TR_RECAP_PRODUCT_SALES` | Rekap penjualan produk untuk KPI |
| `KPI_MST_PARAMETER` | Parameter KPI |
| `KPI_MST_JENISOTL_PARAM` | Parameter jenis outlet untuk KPI |

---

### 2.9 Cashless Payment

| Tabel | Fungsi |
|---|---|
| `TR_SALES_PAYMENT` | Pembayaran per transaksi (cash/LinkAja/VA) |
| `MST_VIRTUAL_ACCOUNT` | Data virtual account |
| `MST_BANK` / `MST_OUTLET_BANK` | Data bank |
| `MST_PAYMENT_MAP` | Mapping metode pembayaran |

---

### 2.10 Realisasi Biaya

| Tabel | Fungsi |
|---|---|
| `TR_COST_REALIZATION` | Klaim biaya operasional Sales (BBM, transport, dll.) |
| `MST_COST_ITEM` | Jenis biaya yang bisa diklaim |
| `MST_COST_ITEM_MAP` | Mapping biaya ke DFMS |

---

### 2.11 Utilitas & Log

| Tabel | Fungsi |
|---|---|
| `LOG_ACTIVIY` | Log aktivitas user (username, activity, datetime, week) |
| `LOG_PRINT` | Log cetak nota |
| `DB_TRACKING` | Tracking versi database & flag upload |
| `HISTORY_UPLOAD_MOBIDOC` | History upload data |
| `TR_INBOX` | Pesan/notifikasi dari server |

---

## 3. Pola Arsitektur yang Bisa Dipelajari

### 3.1 Pola Dual-Table (Active + Temp)
Banyak tabel transaksi punya pasangan `_TEMP`:
- `TR_NOTATION` + `TEMP_TR_NOTATION`
- `TR_SALES` + `TR_SALES_TEMP`
- `TR_PP_PROG_EXEC` + `TR_PP_PROG_EXEC_TEMP`

**Fungsi:** Data di-input ke tabel `_TEMP` dulu, baru dipindah ke tabel utama setelah dikonfirmasi/disubmit. Ini adalah **optimistic local commit** sebelum finalisasi.

**Ide untuk project baru:** Gantikan dengan `status` field pada satu tabel: `DRAFT → SUBMITTED → SYNCED`.

---

### 3.2 Pola History/Audit Trail (`_HIST`)
Semua tabel WMS memiliki tabel `_HIST` untuk backup sebelum modifikasi besar. Ini pola audit manual karena SQLite tidak punya built-in history.

**Ide untuk project baru:** Di PostgreSQL, gunakan **`updated_at` + soft delete** atau ekstensi `pgaudit` untuk audit trail yang lebih clean.

---

### 3.3 Sistem "WEEK" sebagai Periode
Hampir semua data terikat ke `WEEK` (nomor minggu dalam tahun, bukan `DATE`). Ini berarti satu siklus distribusi = satu minggu kerja.

**Konteks bisnis:** Download data dilakukan per week, target ditetapkan per week, history callsheet adalah week lalu.

**Ide untuk project baru:** Buat entitas `Period` atau `Week` yang menjadi referensi untuk semua data distribusi.

---

### 3.4 Multi-UOM Stok Rokok
Stok rokok dikelola dalam 4 satuan yang bertingkat:
```
1 DUS = N BAL
1 BAL = N SLOP (SLF)  
1 SLF = N BKS (bungkus)
```

Tabel `MST_PRODUCT` menyimpan konversi: `UOM_BAL`, `UOM_SLF`, `UOM_BKS`, `IS_DUS`.
Tabel `TR_STOCK_ROKOK` menyimpan stok per satuan: `STOCK_INIT_DUS`, `STOCK_INIT_BAL`, `STOCK_INIT_SLF`, `STOCK_INIT_BKS`.

---

### 3.5 Flag-Flag Penting di MST_OUTLET
Outlet memiliki banyak flag status yang mempengaruhi alur:
- `OUTLET_STATUS` — aktif/non-aktif
- `FLAG_BARCODE` — apakah sudah punya barcode
- `BARCODE_REASON` — alasan belum ada barcode
- `FLAG_STOCKIST` — apakah outlet adalah stockist
- `FLAG_GPS` — apakah koordinat GPS sudah divalidasi
- `NEW_OUTLET` — outlet baru yang belum dikunjungi
- `CALL_CYCLE` — siklus kunjungan (harian/mingguan)
- `COVERAGE_TYPE` — tipe coverage

---

### 3.6 Sistem Download per Route (TR_DOWNLOAD)
Sebelum Sales bisa bekerja, ia harus "download" data untuk route tertentu. `TR_DOWNLOAD` melacak status download per komponen:
- `FLAG_PRODUCT` — data produk sudah didownload?
- `FLAG_OUTLET` — data outlet sudah didownload?
- `FLAG_TARGET` — data target sudah didownload?
- `FLAG_HISCALLSHEET` — history callsheet sudah didownload?
- `FLAG_UPLOADED` — data sudah diupload ke server?

**Ide untuk project baru:** Model ini sangat baik. Implementasikan sebagai `SyncStatus` entity dengan status per komponen data.

---

### 3.7 Callsheet (`TR_NOTATION`) sebagai Inti Bisnis
`TR_NOTATION` adalah tabel paling kritis — ini adalah "form kunjungan" yang diisi Sales di setiap outlet untuk setiap produk:

```
STOCK          -> berapa stok rokok yang ada di outlet saat ini (cek stok)
BUY_SA         -> berapa yang dijual ke outlet (penjualan SA)
FACEUP         -> jumlah face-up di display
WHOLESALE_SCH  -> jadwal grosir
OOS_DURATION   -> durasi out-of-stock
DISTRIBUTION_NOTATION -> kode notasi distribusi (LOV dari MST_NOTATION)
```

**Insight:** Di project baru, `OutletStockCheck` dan `SalesOrder` dalam PRD kita sebenarnya bisa diintegrasikan menjadi satu "Callsheet" per outlet per produk, mirip dengan pola ini.

---

## 4. Ringkasan Keputusan untuk Project Baru

| # | Temuan dari DB Lama | Keputusan untuk Project Baru |
|---|---|---|
| 1 | Multi-UOM: DUS, BAL, SLF, BKS | **Implementasikan.** Entitas `Product` punya `uom_bal`, `uom_slf`, `uom_bks`. `StockDistributionItem` punya qty per satuan. |
| 2 | `TR_DURATION_NOTES` — waktu + GPS kunjungan | **Masuk MVP.** Entitas `Visit` tambah `call_start`, `call_end`, `actual_lat`, `actual_lng`. |
| 3 | `START_END_ROUTE` — waktu + GPS start/end route | **Masuk MVP.** Entitas baru `RouteSession` per Sales per hari. |
| 4 | `MST_TARGET` — target per route per produk | **Masuk MVP.** Ditampilkan di dashboard Sales dan Spv. |
| 5 | Sistem `WEEK` sebagai periode | **Adopt.** Semua data terikat ke entitas `WeekPeriod`. |
| 6 | `TR_DOWNLOAD` — tracking status sync per komponen | **Adopt & modernisasi.** Jadikan `SyncStatus` dengan per-entity tracking. |
| 7 | Dual-table (_TEMP) untuk draft | **Simplifikasi.** Satu tabel dengan `status` (DRAFT/SUBMITTED/SYNCED). |
| 8 | History table (_HIST) untuk audit | **Gantikan** dengan PostgreSQL audit pattern (updated_at + soft delete). |
| 9 | `MST_HIS_CALLSHEET` — history kunjungan week lalu | **Masuk MVP.** Sales butuh referensi ini saat mengisi callsheet baru. |
| 10 | `TR_NOTATION` + `TR_SALES` terpisah | **Satukan** menjadi satu `Callsheet` per outlet per produk yang berisi stock check + penjualan sekaligus. |

---

## 5. Fitur yang Disepakati OUT OF SCOPE untuk MVP (untuk Iterasi Berikutnya)

| Fitur | Domain |
|---|---|
| WMS (BPPR, BPPM, BPPU) | Warehouse Management |
| Program Promosi PP & Non-PP | Trade Marketing |
| Tanda Terima Outlet (TTO) | Kontrak Digital |
| Maintenance Unit Display | Trade Asset |
| Cashless Payment (LinkAja, VA) | Pembayaran Digital |
| Survey Volume | Research |
| Competitor Activity | Market Intelligence |
| KPI Retail tracking | Performance Management |
| Realisasi Biaya Operasional | Finance |
| POSM (Point of Sale Materials) | Trade Marketing |

---

*Dokumen ini adalah referensi statis dari analisis aplikasi lama. Tidak perlu diupdate kecuali ada temuan baru dari kode referensi.*
