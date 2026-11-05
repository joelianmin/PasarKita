# Bagian 3: Memahami sistem

*Aplikasi dan basis data*

> **Tujuan.** Membaca kode dan basis data PasarKita dengan tahu di mana biaya dan risiko biasanya muncul.  
> **Prasyarat.** Bagian 1. Stack sebaiknya sudah berjalan (bagian 2) agar bisa mencoba contohnya.  
> **Setelah bagian ini kamu bisa:**
> - menjelaskan lapisan Controller, Service, Repository, serta transaksi dan connection pool
> - mengenali pola N+1 dan panggilan layanan luar di dalam transaksi
> - membaca skema, memakai `EXPLAIN`, dan membaca `pg_stat_statements`

---

## 3.1 Aplikasi: Spring Boot, JPA, transaksi

**Struktur.** Satu aplikasi, banyak modul (modulith). Setiap folder di
`src/main/java/id/pasarkita/` adalah satu modul dengan tanggung jawab sendiri. Modul
saling memanggil lewat kelas `public` (biasanya `*Service`); kelas lain sengaja
`package-private` agar tidak bisa dipakai modul lain. `task modules` memeriksa aturan ini.

**Lapisan dalam satu modul**

```
Controller  →  Service  →  Repository  →  Database
(HTTP)         (aturan)    (akses data)
```

**Konsep yang perlu dikenal**

- **Dependency injection**: kelas tidak membuat dependensinya sendiri; Spring
  menyuntikkannya lewat constructor.
- **Entity dan repository (JPA)**: `Product`, `Order`, dan sejenisnya adalah entity yang
  dipetakan ke tabel. `ProductRepository` menyediakan query tanpa menulis SQL manual.
- **`@Transactional`**: semua langkah dalam satu method berhasil bersama atau dibatalkan
  bersama (rollback). Transaksi menahan satu koneksi database selama method berjalan.
- **Connection pool (HikariCP)**: koneksi database dibatasi jumlahnya (default 10). Kalau
  semua terpakai, request berikutnya antre menunggu koneksi.
- **Masalah N+1**: satu query mengambil N baris, lalu N query tambahan mengambil data
  terkait satu per satu. Gejalanya: jumlah query per request ikut membesar seiring data.
  Itulah alasan kolom "Query/request" ada di tabel baseline.
- **Panggilan ke layanan luar**: aplikasi memanggil API pajak, payment gateway, dan
  notifikasi lewat HTTP (di lab ditiru oleh WireMock). Panggilan ini lebih lambat daripada
  query database dan bisa gagal; perhatikan di mana ia dipanggil.
- **Feature flag**: tabel `feature_flag` menyalakan atau mematikan perilaku tanpa deploy
  ulang (`task flag NAME=... ON=true`). Nilainya di-cache 5 detik.
- **Spring Profile**: `task app` memakai profil `local` (`application-local.yml`). Di sana
  tersedia logging SQL yang bisa kamu aktifkan untuk menghitung query per request.
- **Validasi input dan DTO**: data dari luar diperiksa di tepi, misalnya dengan `@Valid`
  (tersedia lewat `spring-boot-starter-validation`). Respons API sebaiknya memakai objek
  khusus (`record` sebagai DTO), bukan entity JPA langsung, supaya field internal tidak
  ikut terbuka (OWASP API3, bagian 5.3).
- **Timeout dan kegagalan layanan luar**: setiap panggilan HTTP perlu batas waktu dan
  rencana jika gagal. Tanpa itu, satu layanan yang lambat menahan thread dan koneksi
  database (ISO 25010: *fault tolerance*, bagian 5.2).
- **Desain kelas**: prinsip SOLID (bagian 5.1) membantumu membaca dan menilai kode ini.

Alur checkout, dari `order/CheckoutService`: ambil keranjang, hitung harga, kurangi stok,
debit saldo, simpan order, buat invoice, beri poin, kirim notifikasi, minta pengiriman,
kosongkan keranjang. Baca file ini sampai paham sebelum mengukur `POST /api/checkout`.

> Cek diri: sebuah method `@Transactional` memanggil API eksternal yang butuh 300 ms. Apa
> pengaruhnya pada connection pool saat 20 request datang bersamaan?

---

## 3.2 Database: PostgreSQL, indeks, EXPLAIN

- **Tabel dan skema** dibuat oleh migrasi Flyway di `src/main/resources/db/migration/`.
  Berkas bernama `V1__...sql`, `V2__...sql`, dan seterusnya dijalankan berurutan, sekali
  saja. Jangan mengubah migrasi yang sudah dijalankan; tambahkan versi baru.
- **Seed** mengisi data contoh. `SCALE=S` untuk latihan ringan, `SCALE=M` (1 juta order)
  untuk Lab 0 agar masalah skala terlihat. `GROUP_SEED` di `group.env` membuat data tiap
  kelompok berbeda; jangan diubah sendiri.
- **Indeks** mempercepat pencarian dengan menghindari pembacaan seluruh tabel. Tanpa indeks
  yang cocok, PostgreSQL melakukan *sequential scan* (baca semua baris). Indeks juga
  memperlambat penulisan dan memakan ruang, jadi tidak semua kolom perlu diindeks.
- **`EXPLAIN` dan `EXPLAIN (ANALYZE, BUFFERS)`** menunjukkan bagaimana PostgreSQL
  mengeksekusi query. Hal yang dicari: `Seq Scan` pada tabel besar, perkiraan baris yang
  jauh dari kenyataan, dan waktu aktual.
- **`pg_stat_statements`** mencatat statistik semua query: berapa kali dipanggil, total
  waktu, dan waktu rata-rata. Cara memakainya:

```sql
-- buka lewat: task psql
SELECT calls, round(total_exec_time::numeric, 0) AS total_ms,
       round(mean_exec_time::numeric, 2) AS mean_ms, left(query, 80) AS query
FROM pg_stat_statements
ORDER BY total_exec_time DESC
LIMIT 10;

SELECT pg_stat_statements_reset();   -- kosongkan sebelum tiap run pengukuran
```

- **Offset pagination**: `page=49&size=20` memaksa database melewati 980 baris dulu. Makin
  dalam halamannya, makin lambat.
- **Query berparameter**: nilai dari pengguna dikirim sebagai parameter terikat (`:q`, `?`),
  tidak pernah disambung ke teks SQL. Repository JPA dan `JdbcTemplate` di proyek ini sudah
  memakainya, sehingga aman dari SQL injection (OWASP A03).
- **Hak akses minimum**: akun database aplikasi cukup memiliki hak yang dibutuhkan. Di lab,
  satu akun `pasarkita` dipakai untuk semuanya demi kemudahan; di produksi, akun migrasi
  dan akun aplikasi dipisah.
- **Data sensitif**: password disimpan sebagai hash BCrypt (kolom `password_hash`), tidak
  pernah sebagai teks asli.

> Cek diri: apa bedanya "query ini lambat" dengan "query ini murah tetapi dipanggil
> 50.000 kali"? Kolom mana di `pg_stat_statements` yang membedakannya?

---

*Sebelumnya: [Bagian 2](02-lingkungan-kerja.md) · [Daftar isi](../MATERI-DASAR.md) · Berikutnya: [Bagian 4](04-mengukur-dan-mengamati.md)*
