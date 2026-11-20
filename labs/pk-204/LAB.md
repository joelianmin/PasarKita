# PK-204: Login terasa lambat

> Aturan umum, alur kerja, label Issue, dan templat komentar penutupan ada di [`../MINGGU-20-26Nov2026.md`](../MINGGU-20-26Nov2026.md).
> Tiap anggota kelompok memegang satu tiket. Satu tiket hanya diambil satu orang di satu kelompok.

*Folder lab: `labs/pk-204` · Data: skala L, sekitar 2 juta pelanggan*

> **Rina, Customer Service (Senin, 10.15):** Banyak pelanggan bilang login lama, bisa hampir satu detik, padahal halaman lain terasa cepat.

> **Pak Hendra, Senior Developer (10.30):** Itu karena bcrypt. Turunkan cost-nya dari 10 ke 4, pasti langsung cepat.

**Yang diharapkan**

- p95 `POST /api/auth/login` turun minimal 70% dibanding pengukuran awal di lab ini.
- Setiap perubahan pada cara password disimpan harus disetujui tim keamanan.

## Perintah

1. `task lab LAB=pk-204`: menyalakan infrastruktur dan mengisi data (lihat skala di atas).
2. `task app` di terminal terpisah.
3. `task reproduce LAB=pk-204` sekali untuk pemanasan, lalu tiga kali untuk pengukuran.
   Ringkasan tersimpan di `labs/pk-204/out/baseline-table.md`.
4. Setelah perbaikanmu, ulangi pengukuran dengan cara yang sama dan bandingkan.
5. Commit semua perubahan, lalu `task evidence LAB=pk-204` dan lampirkan berkas bukti di PR. Jangan meng-commit berkas bukti.

Gunakan huruf kecil pada nilai `LAB` (`pk-204`).

## Paket konteks

Dokumen di folder [`konteks/`](konteks/). Tidak semua isinya relevan, dan beberapa isinya menjadi batasan yang harus kamu penuhi.

- [`01-kebijakan-keamanan-password.md`](konteks/01-kebijakan-keamanan-password.md)
- [`02-thread-customer-service.md`](konteks/02-thread-customer-service.md)
- [`03-ringkasan-data-pelanggan.md`](konteks/03-ringkasan-data-pelanggan.md)

## Catatan script k6

- Endpoint: `POST /api/auth/login` dengan email `user<n>@pasarkita.test` (n acak 1 sampai 2.000.000), password `rahasia123`. Laju 3 req/detik, 3 menit. Ubah dengan `RATE=n` bila laptopmu tidak kuat dan catat di laporan.
- Data skala L. Seed lebih lama dan memakai disk jauh lebih besar daripada skala M, jadi mulai lebih awal dan pastikan ruang disk cukup.
- Target dihitung dari pengukuran awalmu di lab ini (data skala L), bukan dari baseline Lab 0.
- Beban memakai laju tetap (model terbuka), tanpa threshold lulus atau gagal. Run ini mengukur.
- Jalankan dari root repo. Hasil mentah ada di `labs/pk-204/out/` dan tidak ikut Git.
- `RATE` dan `DURATION` bisa diubah lewat variabel lingkungan (macOS dan Linux: `RATE=1 task reproduce LAB=pk-204`; PowerShell: `$env:RATE=1`) atau `-e RATE=1` pada `k6 run` langsung. Catat perubahan itu di laporanmu.
