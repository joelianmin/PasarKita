# Kesepakatan Tingkat Layanan (SLA) Operasional

**Versi:** 3.0 · **Berlaku sejak:** 1 Juli 2026

## Jam operasional

- Layanan penjual dan pelanggan aktif penuh pukul **07.00–22.00 WIB**, setiap hari.
- **Tidak ada jendela maintenance.** Deploy dan migrasi skema dijalankan lewat pipeline pada jam kerja.

## Batasan perubahan database

- **Pada jam berapa pun**, termasuk di luar 07.00–22.00 WIB, operasi yang mengunci tabel transaksi (order, invoice, pembayaran) **lebih dari 1 detik dilarang**. Pukul 22.00–07.00 bukan jendela maintenance: layanan tetap menerima penulisan dari proses terjadwal dan pipeline.
- Perubahan yang berpotensi mengunci tabel harus memakai cara yang tidak memblokir penulisan.

## Lain-lain

- Backup penuh database dijalankan setiap hari pukul 02.00 WIB.
- Target ketersediaan layanan 99,9% per bulan.
