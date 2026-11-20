# Laporan Bulanan Database: Oktober 2026

**Disusun oleh:** Andi (DBA kontrak)

| Tabel | Jumlah baris | Pertumbuhan per bulan |
| --- | --- | --- |
| orders | 1,0 juta | 60 ribu |
| order_line | 2,1 juta | 130 ribu |
| invoice | 2,1 juta | 130 ribu |

- Pemakaian disk: 62% dari kapasitas.
- Autovacuum berjalan normal. Tidak ada tabel dengan proporsi baris mati di atas 5%. Pemeriksaan ini mencakup tabel `invoice`, jadi tidak ada bloat yang perlu dibersihkan.
- Rekomendasi: evaluasi penambahan kapasitas disk pada kuartal 1 tahun 2027.
