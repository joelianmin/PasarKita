# PK-201: Daftar order di back-office lambat

> Aturan umum, alur kerja, label Issue, dan templat komentar penutupan ada di [`../MINGGU-20-26Nov2026.md`](../MINGGU-20-26Nov2026.md).
> Tiap anggota kelompok memegang satu tiket. Satu tiket hanya diambil satu orang di satu kelompok.

*Folder lab: `labs/pk-201` · Data: skala M*

> **Sari, Operasional (Senin, 09.05):** Halaman daftar order di back-office butuh satu sampai dua detik setiap kali dibuka, padahal isinya cuma 20 baris. Tim kami membukanya ratusan kali sehari untuk mengecek pesanan.

> **Bayu, Product Manager (09.20):** Order kita sudah lebih dari sejuta. Mungkin sudah waktunya tabel order dipartisi per bulan?

**Yang diharapkan**

- p95 `GET /api/admin/orders` turun minimal 70% dari baseline Lab 0.
- Data yang ditampilkan di back-office tidak berubah.

## Perintah

1. `task lab LAB=pk-201`: menyalakan infrastruktur dan mengisi data (lihat skala di atas).
2. `task app` di terminal terpisah.
3. `task reproduce LAB=pk-201` sekali untuk pemanasan, lalu tiga kali untuk pengukuran.
   Ringkasan tersimpan di `labs/pk-201/out/baseline-table.md`.
4. Setelah perbaikanmu, ulangi pengukuran dengan cara yang sama dan bandingkan.
5. Commit semua perubahan, lalu `task evidence LAB=pk-201` dan lampirkan berkas bukti di PR. Jangan meng-commit berkas bukti.

Gunakan huruf kecil pada nilai `LAB` (`pk-201`).

## Paket konteks

Dokumen di folder [`konteks/`](konteks/). Tidak semua isinya relevan, dan beberapa isinya menjadi batasan yang harus kamu penuhi.

- [`01-notulen-evaluasi-back-office.md`](konteks/01-notulen-evaluasi-back-office.md)
- [`02-thread-tim-frontend.md`](konteks/02-thread-tim-frontend.md)
- [`03-notulen-arsitektur-2025.md`](konteks/03-notulen-arsitektur-2025.md)

## Catatan script k6

- Endpoint: `GET /api/admin/orders?page=<0-19>&size=20`, laju 2 req/detik, 3 menit. Sama dengan skenario `adminOrders` di Lab 0, jadi bisa dibandingkan dengan baseline Lab 0 kelompokmu.
- Data skala M (1 juta order). Jangan memakai seed skala S.
- Beban memakai laju tetap (model terbuka), tanpa threshold lulus atau gagal. Run ini mengukur.
- Jalankan dari root repo. Hasil mentah ada di `labs/pk-201/out/` dan tidak ikut Git.
- `RATE` dan `DURATION` bisa diubah lewat variabel lingkungan (macOS dan Linux: `RATE=1 task reproduce LAB=pk-201`; PowerShell: `$env:RATE=1`) atau `-e RATE=1` pada `k6 run` langsung. Catat perubahan itu di laporanmu.
