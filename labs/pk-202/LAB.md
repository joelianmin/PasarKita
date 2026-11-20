# PK-202: Keranjang berisi data yang tidak masuk akal

> Aturan umum, alur kerja, label Issue, dan templat komentar penutupan ada di [`../MINGGU-20-26Nov2026.md`](../MINGGU-20-26Nov2026.md).
> Tiap anggota kelompok memegang satu tiket. Satu tiket hanya diambil satu orang di satu kelompok.

*Folder lab: `labs/pk-202` · Data: skala M*

> **Rina, Customer Service (Rabu, 09.40):** Beberapa pelanggan mengeluh checkout mereka selalu gagal, ada yang dengan pesan "Produk ... tidak ditemukan". Waktu kami buka keranjangnya, ada barang dengan jumlah minus dan produk yang tidak ada.

> **Eko, Senior Developer (09.55):** Gampang. Bikin skrip yang menghapus baris aneh tiap malam. Kodenya tidak perlu diubah.

> **Dimas, QA (10.10):** Aplikasi mobile sudah memvalidasi jumlah kok, jadi backend tidak perlu ikut.

**Yang diharapkan**

- Permintaan dengan jumlah yang tidak sah atau produk yang tidak ada ditolak sebelum tersimpan, dengan kode status yang jelas. Permintaan sah tetap berhasil seperti sekarang.
- Menambahkan produk yang sudah ada di keranjang tidak menghasilkan baris ganda.
- Baris lama yang tidak sah atau ganda tidak lagi menggagalkan checkout.
- p95 `POST /api/cart/items` tidak lebih dari 20% di atas baseline Lab 0 kelompokmu.

## Perintah

1. `task lab LAB=pk-202`: menyalakan infrastruktur dan mengisi data (lihat skala di atas).
2. `task app` di terminal terpisah.
3. `task reproduce LAB=pk-202` sekali untuk pemanasan, lalu tiga kali untuk pengukuran.
   Ringkasan tersimpan di `labs/pk-202/out/baseline-table.md`.
4. Setelah perbaikanmu, ulangi pengukuran dengan cara yang sama dan bandingkan.
5. Commit semua perubahan, lalu `task evidence LAB=pk-202` dan lampirkan berkas bukti di PR. Jangan meng-commit berkas bukti.

Gunakan huruf kecil pada nilai `LAB` (`pk-202`).

## Paket konteks

Dokumen di folder [`konteks/`](konteks/). Tidak semua isinya relevan, dan beberapa isinya menjadi batasan yang harus kamu penuhi.

- [`01-kebijakan-pembelian.md`](konteks/01-kebijakan-pembelian.md)
- [`02-thread-tim-mobile.md`](konteks/02-thread-tim-mobile.md)
- [`03-catatan-migrasi-database.md`](konteks/03-catatan-migrasi-database.md)

## Catatan script k6

- Skrip mengirim enam jenis permintaan ke `POST /api/cart/items` (sah, jumlah 0, jumlah -5, jumlah 100, produk yang tidak ada, produk sama dua kali) sebanyak 20 putaran, lalu mencatat kode status tiap jenis di `out/baseline-table.md`.
- Skrip ini menambah baris ke tabel `cart_item` di database labmu. Jalankan `task reset` lalu `task lab LAB=pk-202` bila ingin mulai bersih.
- Kolom "Diharapkan" di tabel adalah patokan dari tiket dan paket konteks, bukan hasil sebelum perbaikanmu. Jumlah baris ganda per produk tidak terlihat dari k6: periksa lewat `task psql`.
- Data skala M.
- Beban memakai laju tetap (model terbuka), tanpa threshold lulus atau gagal. Run ini mengukur.
- Jalankan dari root repo. Hasil mentah ada di `labs/pk-202/out/` dan tidak ikut Git.
- `RATE` dan `DURATION` bisa diubah lewat variabel lingkungan (macOS dan Linux: `RATE=1 task reproduce LAB=pk-202`; PowerShell: `$env:RATE=1`) atau `-e RATE=1` pada `k6 run` langsung. Catat perubahan itu di laporanmu.
