# Lab 0: Baseline

**Konteks.** Kamu baru bergabung ke tim PasarKita. Sebelum ada yang mengubah apa pun, tim
butuh catatan kondisi sistem saat ini sebagai pembanding semua perbaikan di minggu 2–6.

**Tugas.** Ukur latensi, error, beban database, dan pemakaian sumber daya untuk delapan
endpoint utama, lalu catat minimal tiga hal yang mencurigakan. Jangan memperbaiki apa pun.

## Langkah singkat

1. `task lab LAB=0` – menyalakan infrastruktur dan mengisi data skala M (beberapa menit).
2. `task app` di terminal terpisah.
3. `task reproduce LAB=0` sekali untuk pemanasan, lalu tiga kali untuk pengukuran.
   Ringkasan per endpoint tersimpan di `labs/0/out/baseline-table.md`.
4. Kumpulkan data database (`task psql`, `pg_stat_statements`, `EXPLAIN`) dan sumber daya (Grafana).
5. Tulis `BASELINE.md` di root repo memakai template dari materi minggu 1.
6. Commit, lalu `task evidence LAB=0` dan lampirkan file bukti di PR.

Kolom "Query/request" diisi manual dari log SQL (lihat `application-local.yml`).

## Catatan script k6

- **Samakan skala data dan script.** `reproduce.k6.js` memakai default skala M (50.000 produk,
  200.000 customer, 2.000 penjual). Dengan seed skala S, tambahkan
  `-e PRODUCTS=5000 -e CUSTOMERS=10000 -e SELLERS=200`, kalau tidak banyak request akan 404
  dan terhitung sebagai error.
- **Checkout mengubah data.** Setiap iterasi mengurangi stok dan saldo, sehingga run
  berikutnya makin sering menghasilkan 402/409. Untuk perbandingan yang bersih, jalankan
  `task reset` lalu `task lab LAB=0` sebelum tiga run pengukuran.
- **Pemanasan.** Beban langsung penuh tanpa ramp-up. Run pertama dibuang karena JIT dan
  connection pool belum hangat.
- **Tidak ada threshold lulus/gagal.** Threshold hanya dipakai agar k6 menghitung statistik
  per endpoint. Run ini mengukur, bukan menguji.
- **Laju `cart`.** Request `POST /api/cart/items` dikirim di dalam skenario `checkout`, jadi
  lajunya mengikuti checkout (2/detik), bukan skenario sendiri.
- **Login mengukur BCrypt.** Latensi `POST /api/auth/login` didominasi CPU untuk hash
  password, bukan I/O.
