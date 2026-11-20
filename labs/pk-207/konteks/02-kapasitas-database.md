# Kapasitas Database Produksi

**Disusun oleh:** Tim Platform · **Data per 1 Oktober 2026**

| Item | Nilai |
| --- | --- |
| `max_connections` PostgreSQL | 100 |
| Instance aplikasi PasarKita | 8 |
| Koneksi maksimum per instance | 10 |
| Cadangan untuk pekerjaan latar, monitoring, dan akses darurat | 20 |

- Koneksi database mahal: setiap koneksi membuat proses sendiri di server.
- Batas 10 per instance **tidak boleh dinaikkan** sebelum penambahan memori server (dianggarkan kuartal 2 tahun 2027). Menaikkannya ke 100 per instance berarti 800 koneksi, jauh di atas `max_connections`.
- Antrean menunggu koneksi lebih dari 30 detik dihitung sebagai insiden.
