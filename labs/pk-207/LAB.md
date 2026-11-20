# PK-207: Gateway pembayaran melambat, halaman lain ikut macet

> Aturan umum, alur kerja, label Issue, dan templat komentar penutupan ada di [`../MINGGU-20-26Nov2026.md`](../MINGGU-20-26Nov2026.md).
> Tiap anggota kelompok memegang satu tiket. Satu tiket hanya diambil satu orang di satu kelompok.

*Folder lab: `labs/pk-207` · Data: skala M*

> **Sinta, Tim Pembayaran (Kamis, 11.00):** Kemarin gateway pembayaran sempat melambat sampai dua detik. Selama itu halaman lain yang tidak ada hubungannya dengan checkout ikut macet.

> **Eko, Senior Developer (11.15):** Itu poolnya kekecilan. Naikkan `maximum-pool-size` dari 10 ke 100, beres.

**Yang diharapkan**

- Waktu koneksi database yang dipakai per checkout (total waktu pemakaian koneksi dibagi jumlah checkout, dari metrik `hikaricp.connections.usage` pada aplikasi) turun minimal 70% dari pengukuran awal.
- Perilaku bisnis checkout tidak berubah: stok, saldo, pesanan, dan kode status 200, 402, 409.
- Tidak mengubah pengaturan layanan tiruan (WireMock).

## Perintah

1. `task lab LAB=pk-207`: menyalakan infrastruktur dan mengisi data (lihat skala di atas).
2. `task app` di terminal terpisah.
3. `task reproduce LAB=pk-207` sekali untuk pemanasan, lalu tiga kali untuk pengukuran.
   Ringkasan tersimpan di `labs/pk-207/out/baseline-table.md`.
4. Setelah perbaikanmu, ulangi pengukuran dengan cara yang sama dan bandingkan.
5. Commit semua perubahan, lalu `task evidence LAB=pk-207` dan lampirkan berkas bukti di PR. Jangan meng-commit berkas bukti.

Gunakan huruf kecil pada nilai `LAB` (`pk-207`).

## Paket konteks

Dokumen di folder [`konteks/`](konteks/). Tidak semua isinya relevan, dan beberapa isinya menjadi batasan yang harus kamu penuhi.

- [`01-kebijakan-pembayaran.md`](konteks/01-kebijakan-pembayaran.md)
- [`02-kapasitas-database.md`](konteks/02-kapasitas-database.md)
- [`03-laporan-insiden-gateway.md`](konteks/03-laporan-insiden-gateway.md)

## Catatan script k6

- Alur dan laju sama dengan skenario checkout di Lab 0 (tambah ke keranjang lalu checkout), 2 req/detik, 3 menit.
- Di akhir run, skrip mencetak total dan rata-rata waktu koneksi database dipakai selama run (dari `/actuator/metrics/hikaricp.connections.usage`). Bagi total itu dengan jumlah request checkout pada ringkasan untuk mendapat waktu koneksi per checkout, dan salin angkanya ke laporanmu. Metrik yang sama ada di Prometheus dan Grafana.
- Checkout mengubah stok dan saldo, jadi run berikutnya makin sering menghasilkan 402 atau 409. Untuk perbandingan yang bersih, jalankan `task reset` lalu `task lab LAB=pk-207` sebelum tiap run.
- Jangan mengubah pengaturan WireMock dan jangan menaikkan ukuran pool koneksi: keduanya diatur oleh batasan di paket konteks.
- Data skala M.
- Beban memakai laju tetap (model terbuka), tanpa threshold lulus atau gagal. Run ini mengukur.
- Jalankan dari root repo. Hasil mentah ada di `labs/pk-207/out/` dan tidak ikut Git.
- `RATE` dan `DURATION` bisa diubah lewat variabel lingkungan (macOS dan Linux: `RATE=1 task reproduce LAB=pk-207`; PowerShell: `$env:RATE=1`) atau `-e RATE=1` pada `k6 run` langsung. Catat perubahan itu di laporanmu.
