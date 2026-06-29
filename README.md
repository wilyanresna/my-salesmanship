# Ringkasan Aplikasi
Salesmanship ada adalah aplikasi yang digunakan oleh distributor rokok untuk memasarkan rokoknya ke outlet.
Pada Salesmanship terdapat aplikasi android (folder *android*) yang digunakan Salesman, frontend web (folder *frontend*) yang digunakan Supervisor dan backend(folder *backend*) yang melayani android dan web frontend.
Aplikasi android bersifat offline first karena akan digunakan untuk distribusi ke berbagai daerah di Indonesia yang infrastruktur jaringan selulernya belum merata.

# Pengguna:
1. Supervisor (selanjutnya disebut Spv).
2. Salesman (Sales).

# Ringkasan Bisnis
Berikut adalah ringkasan bisnis yang akan dijalankan, ringkasan ini hanya berisi MVP.
## Hirarki bisnis yang ada pada perusahan ini adalah sebagai berikut:
Area
|_Territory
    |_District
        |_Route
            |_Outlet
Satu Area memiliki banyak Territory, Territory dikepalai oleh seorang Spv.
Satu Territory memiliki banyak District, District dipegang oleh seorang Sales.
Satu District memiliki banyak Route. Route mewakilkan hari, seperti Senin = Route 1, Selasa = Route 2 dan seterusnya.
Satu Route memiliki banyak Outlet. Satu Outlet hanya bisa berada pada saru Route.

## Persiapan data Master (user: Spv, aplikasi: frontend web)
Seorang Spv akan bertanggung jawab untuk mengatur dan mengawasi kegiatan distribusi, berikut hal-hal yang dilakukan Spv:
1. Melakukan input data Outlet, data master outlet berisi informasi outlet seperti nama, alamat, nama pemilik, no telepon, titik lokasi dan kode barcode.
2. Setelah data Outlet terbentuk, Spv akan mengatur outlet tersebut masuk ke Territory, District dan Route mana.
3. Setelah data Territory sampai Route terbentuk, Spv akan input data Sales yang akan bekerja di bawah-nya, data Sales berisi nama, nik, username, password, alamat dan no telepon.
4. Spv akan memasangkan Sales dengan District di bawahnya.

## Alur kerja Distribusi (user: Spv, aplikasi: android, frontend web)
1. Pada frontend web, Spv akan menyiapkan data stock rokok yang akan diambil oleh Sales.
2. Sales akan mendapatkan info username dan password yang sudah disiapkan Spv (Sales dapat merubah password).
3. Sales login pada aplikasi android.
4. Sales masuk ke halaman Dashboard yang berisi ringkasan Route yang akan dikunjungi.
5. Sales akan menarik data stock rokok yang sudah disiapkan Spv.
6. Sales melakukan kunjungan ke outlet (dengan cari outlet pada daftar outlet atau dengan cara scan barcode yang sudah ditempel ke outlet).
7. Saat kunjungan outlet ada banyak aktivitas yang harus dilakukan, beberapa diantaranya:
    7.1 Cek stock rokok yang ada dioutlet.
    7.2 Melakukan penjualan rokok ke outlet dengan batasan sesuai stock yang dibawa.
8. Sales selesai kunjungan dan Dashboard terupdate.
9. Jika sales sudah mengunjungi semua outlet, sales kembali ke kantor untuk:
    9.1 Mengembalikan barang sesuai data stock rokok yang sudah dikalkulasi berdasarkan penjualan yang sudah dilakukan.
    9.2 Melapor ke Spv sehingga Spv dapat memulai untuk melihat data hasil kunjungan dari android yang sudah diupload melalui frontent web.