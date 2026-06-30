Nomor dan Urutan Data Dibuat	|	Lokasi Table	|	Jenis Table	|	Nama Table	|	Fungsi Table	|	Atribut Kunci	|	Sumber Data	|	Rules
---	|	---	|	---	|	---	|	---	|	---	|	---	|	---
1	|	Server	|	Master	|	Employee	|	Berisi data Spv dan Sales	|	id, name, nik, username, password, position	|	Seed	|	
2	|	Server	|	Master	|	Area	|	Berisi master Area	|	id, name	|	Seed	|	
3	|	Server	|	Master	|	Territory	|	Berisi master Territory	|	id, area_id, name	|	Seed	|	
4	|	Server	|	Master	|	District	|	Berisi master District	|	id, territory_id, name	|	Seed	|	
5	|	Server	|	Master	|	Route	|	Berisi master Route	|	id, district_id, name	|	Seed	|	
6	|	Server	|	Master	|	Outlet	|	Berisi data master Outlet	|	id, name, owner_name, lat, lng, barcode	|	Seed	|	
7	|	Server	|	Master	|	Product	|	Berisi data Product	|	id, name, sku, price, uom_bal, uom_slf, uom_bks	|	Seed	|	
8	|	Server	|	Master	|	Param	|	Berisi data param untuk semua LOV	|	id, group, name, value, description	|	Seed	|	Global & static, tidak terikat week
9	|	Server	|	Transaksi	|	MappingSpv	|	Mapping Territory dengan Spv	|	id, spv_id (employee_id), territory_id, week_start, week_end	|	Seed	|	Data bersifat Weekly
10	|	Server	|	Transaksi	|	MappingSales	|	Mapping Sales dengan Spv + District	|	id, spv_id (employee_id), sales_id (employee_id), district_id, week_start, week_end	|	Seed	|	Data bersifat Weekly
11	|	Server	|	Transaksi	|	MappingOutlet	|	Mapping Outlet dengan Route	|	id, outlet_id, route_id, week_start, week_end	|	Seed	|	Data bersifat Weekly
12	|	Server	|	Transaksi	|	StockRokok	|	Berisi data header stock rokok untuk sales	|	id, sales_id, date_used, status (draft, ready, pulled, closed)	|	Dibuat Spv	|	Data bersifat daily; week dapat dihitung dari date_used
13	|	Server	|	Transaksi	|	StockRokokItem	|	Berisi data detail stock rokok (per-SKU)	|	id, stock_rokok_id, product_id, qty_dus_init, qty_bal_init, qty_slf_init, qty_bks_init	|	Dibuat Spv	|	
14	|	Server	|	Transaksi	|	SalesTarget	|	Berisi data Target per-Sales dan per-Route	|	id, week, sales_id, route_id, product_id, target_qty	|	Dibuat Spv	|	Data bersifat Weekly
15	|	Android	|	Master	|	MstSalesman	|	Berisi data detail Salesman untuk week berjalan	|	sales_id, sales_name, spv_id, spv_name, territory_id, district_id	|	Join table 1, 10	|	Kondisi terkini
16	|	Android	|	Master	|	MstProduct	|	Berisi data product week berjalan	|	id, name, sku, price, uom_bal, uom_slf, uom_bks	|	Table 7	|	Snapshot week berjalan
17	|	Android	|	Master	|	MstOutlet	|	Berisi data outlet dengan kondisi week berjalan	|	id, name, owner_name, lat, lng, barcode	|	Join table 6, 11	|	Snapshot week berjalan; hanya outlet yang masuk route Sales
18	|	Android	|	Master	|	Param	|	Berisi data param untuk semua LOV	|	id, group, name, value, description	|	Table 8	|	Di-download sekali, update jika ada versi baru
19	|	Android	|	Master	|	StockRokok	|	Berisi data header stock rokok untuk sales	|	id, server_id, sales_id, date_used, status (draft, ready, pulled, closed)	|	Table 12	|	server_id = id dari server untuk keperluan sync
20	|	Android	|	Master	|	StockRokokItem	|	Berisi data detail stock rokok (per-SKU)	|	id, stock_rokok_id, product_id, qty_dus_init, qty_bal_init, qty_slf_init, qty_bks_init	|	Table 13	|	
21	|	Android	|	Master	|	SalesTarget	|	Berisi data Target per-Sales dan per-Route	|	id, week, sales_id, route_id, product_id, target_qty	|	Table 14	|	
22	|	Android	|	Transaksi	|	TrOutlet	|	Header semua table transaksi per-Outlet per-kunjungan	|	id, outlet_id, visit_no, visit_type (NORMAL/OUT_OF_ROUTE), status, start_time, end_time, lat, lng	|	Table 17	|	1 row per kunjungan; jika outlet dikunjungi 2x maka ada 2 row. lat/lng = koordinat GPS aktual saat kunjungan
23	|	Android	|	Transaksi	|	TrCheckStock	|	Data check stock per-Outlet dan per-Product	|	id, tr_outlet_id, product_id, stock_qty	|	Input	|	
24	|	Android	|	Transaksi	|	TrSales	|	Data header penjualan dalam satu kunjungan	|	id, tr_outlet_id, sales_order	|	Input	|	Dalam satu kunjungan bisa ada lebih dari satu header penjualan
25	|	Android	|	Transaksi	|	TrSalesDetail	|	Data detail penjualan per-SKU	|	id, tr_sales_id, product_id, qty, price, total	|	Input	|	price = snapshot harga saat transaksi (dari MstProduct); menginduk ke table 24
26	|	Android	|	Utilitas	|	SyncQueue	|	Antrian sync data ke server	|	id, entity_type, entity_id, batch_id (UUID), status (PENDING/SYNCING/SYNCED/FAILED)	|	Generated	|	General: bisa menampung TrOutlet, TrCheckStock, TrSales, dll. Setiap TrOutlet yang di-close otomatis masuk queue. Retry otomatis via WorkManager jika FAILED
27	|	Server	|	Transaksi	|	TrOutlet	|	Header transaksi kunjungan hasil upload Android	|	batch_id, id, outlet_id, visit_no, visit_type, status, start_time, end_time, lat, lng, uploaded_at	|	Upload table 22	|	batch_id = UUID yang di-generate Android sebelum upload
28	|	Server	|	Transaksi	|	TrCheckStock	|	Data check stock hasil upload Android	|	batch_id, id, tr_outlet_id, product_id, stock_qty, uploaded_at	|	Upload table 23	|	
29	|	Server	|	Transaksi	|	TrSales	|	Data header penjualan hasil upload Android	|	batch_id, id, tr_outlet_id, sales_order, uploaded_at	|	Upload table 24	|	
30	|	Server	|	Transaksi	|	TrSalesDetail	|	Data detail penjualan hasil upload Android	|	batch_id, id, tr_sales_id, product_id, qty, price, total, uploaded_at	|	Upload table 25	|	price di-simpan sebagai snapshot harga saat transaksi