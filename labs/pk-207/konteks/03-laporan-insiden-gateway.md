# Laporan Insiden: Gateway Pembayaran Melambat

**Tanggal:** 4 November 2026 · **Durasi:** 38 menit (13.10 sampai 13.48 WIB) · **Disusun oleh:** Sinta (Tim Pembayaran)

## Ringkasan

- Latensi gateway biasanya sekitar 150 ms. Selama insiden naik menjadi sekitar 2 detik. Gateway tidak pernah mengembalikan error.
- Halaman lain yang tidak berhubungan dengan checkout ikut melambat, lalu beberapa permintaan menunggu koneksi database.
- Timeout pemanggilan gateway dari aplikasi: 5 detik. Tidak diubah.

## Tindak lanjut

- Vendor gateway menyatakan insiden serupa mungkin terjadi lagi, tanpa jaminan waktu.
- Usul "ulangi otomatis bila lambat" ditolak oleh Tim Pembayaran karena risiko penagihan ganda (lihat kebijakan pembayaran).
- Pemantauan kini menampilkan panel penggunaan koneksi database per instance.
