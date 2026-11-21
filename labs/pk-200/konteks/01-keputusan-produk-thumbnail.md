# Keputusan Produk: Thumbnail Dinamis di Profil Toko

**Tanggal:** 12 November 2026 · **Disetujui:** Bayu (Product), Tim Desain

1. Flag `seller.dynamic-thumbnail` **tetap menyala**. Uji A/B dua minggu menunjukkan klik produk dari halaman profil toko naik 9%.
2. Mematikan flag hanya boleh sebagai langkah darurat sementara, dengan persetujuan Product, dan harus dicatat sebagai insiden.
3. Format URL thumbnail di respons profil: `/thumbnails/<id produk>-<kode warna>.jpg`. Format dan isinya **tidak boleh berubah** (lihat thread CDN).
