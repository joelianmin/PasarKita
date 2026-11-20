# Catatan Tim Platform: Migrasi Database

**Disusun oleh:** Tim Platform · **Diperbarui:** 12 November 2026

- Migrasi dijalankan Flyway lewat pipeline saat deploy.
- **Berkas migrasi yang sudah dijalankan tidak boleh diubah.** Flyway menyimpan checksum, dan deploy gagal bila isinya berbeda. Perubahan skema dan pembersihan data selalu ditulis sebagai berkas versi baru (V2, V3, dan seterusnya).
- Migrasi harus aman dijalankan pada database yang sudah berisi data. Jangan menghapus data yang sah.
- Migrasi yang menambah batasan unik harus lebih dulu membereskan data yang melanggarnya.
- Tabel keranjang berisi data aktif pelanggan. Pembersihan hanya boleh menyentuh baris yang tidak memenuhi kebijakan pembelian.
