# Kebijakan Pembayaran

**Versi:** 2.0 · **Berlaku sejak:** 1 Juni 2026 · **Pemilik:** Tim Pembayaran

1. Otorisasi gateway harus **berhasil sebelum** stok dan saldo dikurangi secara permanen.
2. Pesanan tidak boleh tersimpan tanpa otorisasi yang berhasil.
3. Bila otorisasi sudah berhasil tetapi pesanan gagal tersimpan, kejadian itu dicatat di log pada level **ERROR** dengan ID pelanggan dan jumlah. Rekonsiliasi harian pukul 01.00 WIB memproses catatan itu. Tidak ada pembatalan otorisasi secara real-time.
4. Notifikasi pesanan bersifat **best-effort**. Kegagalan notifikasi tidak boleh membatalkan pesanan yang sudah berhasil.
5. Pemanggilan gateway tidak boleh diulang otomatis oleh aplikasi, karena berisiko menagih dua kali.
