# Kontrak Integrasi Mitra Logistik

**Versi:** 1.3 · **Berlaku sejak:** 1 Maret 2025 · **Pemilik:** Tim Kemitraan

Mitra logistik mengambil daftar pesanan secara berkala lewat API PasarKita:

- `GET /api/admin/orders?status=<status>&page=<n>&size=200`
- Mereka mengambil **200 pesanan per halaman** dan membaca halaman berikutnya sampai habis.
- Field yang dibaca: `content[].id`, `content[].status`, `content[].createdAt`, `page.totalPages`.

## Perubahan

- Perubahan perilaku yang memutus pemanggilan ini membutuhkan **pemberitahuan tertulis 60 hari** kepada mitra.
- Penambahan validasi untuk nilai yang tidak masuk akal tidak dianggap memutus kontrak, selama pemanggilan di atas tetap berhasil.
