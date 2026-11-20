# PK-206: Pencarian lambat setelah import katalog

> Aturan umum, alur kerja, label Issue, dan templat komentar penutupan ada di [`../MINGGU-20-26Nov2026.md`](../MINGGU-20-26Nov2026.md).
> Tiap anggota kelompok memegang satu tiket. Satu tiket hanya diambil satu orang di satu kelompok.

*Folder lab: `labs/pk-206` · Data: skala L, sekitar 500 ribu produk*

> **Bayu, Product Manager (Rabu, 09.00):** Minggu lalu kita import katalog pemasok baru, total produk sekarang sekitar 500 ribu. Sejak itu pencarian terasa lambat. Apa kita perlu langsung pasang Elasticsearch?

**Yang diharapkan**

- p95 `GET /api/products/search` di bawah 150 ms pada data 500 ribu produk.
- Hasil pencarian tidak berkurang dibanding perilaku sekarang.

## Perintah

1. `task lab LAB=pk-206`: menyalakan infrastruktur dan mengisi data (lihat skala di atas).
2. `task app` di terminal terpisah.
3. `task reproduce LAB=pk-206` sekali untuk pemanasan, lalu tiga kali untuk pengukuran.
   Ringkasan tersimpan di `labs/pk-206/out/baseline-table.md`.
4. Setelah perbaikanmu, ulangi pengukuran dengan cara yang sama dan bandingkan.
5. Commit semua perubahan, lalu `task evidence LAB=pk-206` dan lampirkan berkas bukti di PR. Jangan meng-commit berkas bukti.

Gunakan huruf kecil pada nilai `LAB` (`pk-206`).

## Paket konteks

Dokumen di folder [`konteks/`](konteks/). Tidak semua isinya relevan, dan beberapa isinya menjadi batasan yang harus kamu penuhi.

- [`01-analitik-pencarian-oktober.md`](konteks/01-analitik-pencarian-oktober.md)
- [`02-keputusan-ux-pencarian.md`](konteks/02-keputusan-ux-pencarian.md)
- [`03-roadmap-infrastruktur-2027.md`](konteks/03-roadmap-infrastruktur-2027.md)

## Catatan script k6

- Endpoint: `GET /api/products/search?q=<kata>`, laju 3 req/detik, 3 menit. Kata kunci mengikuti sebaran analitik pencarian pada paket konteks.
- Data skala L. Seed lebih lama dan memakai disk jauh lebih besar daripada skala M, jadi mulai lebih awal dan pastikan ruang disk cukup.
- Targetnya angka mutlak (di bawah 150 ms), bukan relatif terhadap Lab 0.
- Beban memakai laju tetap (model terbuka), tanpa threshold lulus atau gagal. Run ini mengukur.
- Jalankan dari root repo. Hasil mentah ada di `labs/pk-206/out/` dan tidak ikut Git.
- `RATE` dan `DURATION` bisa diubah lewat variabel lingkungan (macOS dan Linux: `RATE=1 task reproduce LAB=pk-206`; PowerShell: `$env:RATE=1`) atau `-e RATE=1` pada `k6 run` langsung. Catat perubahan itu di laporanmu.
