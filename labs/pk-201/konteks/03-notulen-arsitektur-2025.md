# Notulen Forum Arsitektur

**Tanggal:** 12 November 2025 · **Topik:** Pertumbuhan tabel order

## Usulan: partisi tabel `orders` per bulan

- Diusulkan oleh tim data karena jumlah order diperkirakan menembus 1 juta pada akhir 2026.
- Pertimbangan: migrasi ke tabel berpartisi membutuhkan perubahan skema besar, penyesuaian foreign key, dan uji ulang semua laporan.
- **Keputusan: ditunda.** Akan dibahas ulang jika order melewati 50 juta, atau jika ada bukti bahwa ukuran tabel sendiri menjadi penyebab masalah performa.

## Lain-lain

- Jadwal on-call akhir tahun disusun oleh tim platform.
- Retensi log aplikasi tetap 30 hari.
