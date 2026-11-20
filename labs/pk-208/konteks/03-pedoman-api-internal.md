# Pedoman API Internal

**Versi:** 1.1 · **Berlaku sejak:** 1 September 2026 · **Pemilik:** Tim Platform

1. Parameter yang tidak valid dijawab **400** dengan pesan yang menjelaskan parameter mana dan mengapa. Kode **5xx** hanya untuk kegagalan di sisi server.
2. Aturan yang sama berlaku untuk **semua endpoint berhalaman**, supaya konsumen tidak perlu menghafal perilaku tiap endpoint.
3. Setiap endpoint daftar punya **batas atas ukuran halaman**. Batas itu tidak boleh memutus pemanggilan yang berjalan sesuai kontrak yang masih berlaku.
4. Endpoint berhalaman yang saat ini ada: `GET /api/products`, `GET /api/products/search`, `GET /api/admin/orders`, dan `GET /api/sellers/{id}/invoices`.
