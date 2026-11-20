# PK-203: Katalog di aplikasi mobile makin lambat

> Aturan umum, alur kerja, label Issue, dan templat komentar penutupan ada di [`../MINGGU-20-26Nov2026.md`](../MINGGU-20-26Nov2026.md).
> Tiap anggota kelompok memegang satu tiket. Satu tiket hanya diambil satu orang di satu kelompok.

*Folder lab: `labs/pk-203` · Data: skala M*

> **Dewi, Tim Mobile (Selasa, 08.30):** Halaman katalog di aplikasi memuat 20 produk per halaman. Waktu muatnya terus naik sejak banyak penjual baru bergabung bulan ini.

> **Bayu, Product Manager (08.45):** Jangan-jangan gambar produknya terlalu besar? Bisa kita kompres?

**Yang diharapkan**

- p95 `GET /api/products` turun minimal 50% dari baseline Lab 0.
- Respons API tetap kompatibel dengan aplikasi mobile versi sekarang.

## Perintah

1. `task lab LAB=pk-203`: menyalakan infrastruktur dan mengisi data (lihat skala di atas).
2. `task app` di terminal terpisah.
3. `task reproduce LAB=pk-203` sekali untuk pemanasan, lalu tiga kali untuk pengukuran.
   Ringkasan tersimpan di `labs/pk-203/out/baseline-table.md`.
4. Setelah perbaikanmu, ulangi pengukuran dengan cara yang sama dan bandingkan.
5. Commit semua perubahan, lalu `task evidence LAB=pk-203` dan lampirkan berkas bukti di PR. Jangan meng-commit berkas bukti.

Gunakan huruf kecil pada nilai `LAB` (`pk-203`).

## Paket konteks

Dokumen di folder [`konteks/`](konteks/). Tidak semua isinya relevan, dan beberapa isinya menjadi batasan yang harus kamu penuhi.

- [`01-changelog-aplikasi-mobile.md`](konteks/01-changelog-aplikasi-mobile.md)
- [`02-statistik-penjual.md`](konteks/02-statistik-penjual.md)
- [`03-rencana-aplikasi-v4.md`](konteks/03-rencana-aplikasi-v4.md)

## Catatan script k6

- Endpoint: `GET /api/products?page=<0-49>&size=20`, laju 10 req/detik, 3 menit. Sama dengan skenario `catalog` di Lab 0.
- Data skala M (50.000 produk, 2.000 penjual).
- Beban memakai laju tetap (model terbuka), tanpa threshold lulus atau gagal. Run ini mengukur.
- Jalankan dari root repo. Hasil mentah ada di `labs/pk-203/out/` dan tidak ikut Git.
- `RATE` dan `DURATION` bisa diubah lewat variabel lingkungan (macOS dan Linux: `RATE=1 task reproduce LAB=pk-203`; PowerShell: `$env:RATE=1`) atau `-e RATE=1` pada `k6 run` langsung. Catat perubahan itu di laporanmu.
