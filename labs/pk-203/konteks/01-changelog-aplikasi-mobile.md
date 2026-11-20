# Changelog Aplikasi Mobile PasarKita

## Distribusi versi aktif (data 1 November 2026)

| Versi | Pengguna aktif |
| --- | --- |
| 3.4 | 25% |
| 3.3 | 35% |
| 3.2 | 40% |

Versi di bawah 3.2 sudah dipaksa update. Versi 3.2 belum bisa dipaksa update karena sebagian pengguna memakai ponsel lama yang tidak mendukung versi 3.3.

## Versi 3.2 (Maret 2026)

- Halaman katalog membaca dari `GET /api/products`: `content[].id`, `content[].name`, `content[].price`, `content[].seller.name`, `content[].seller.city`.
- Gambar produk diambil langsung dari CDN dengan URL `/images/{id}.jpg`. Ukuran dan kompresi gambar diatur oleh CDN sejak versi 3.1, bukan oleh API.

## Versi 3.3 (Juni 2026)

- Halaman katalog juga menampilkan kode produk dari `content[].sku`.
- Perbaikan crash saat koneksi lambat.

## Versi 3.4 (September 2026)

- Mode gelap.
- `content[].description` hanya dipakai di halaman detail produk (`GET /api/products/{id}`), tidak di halaman katalog.
