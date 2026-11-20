# PK-208: Parameter halaman yang salah memicu error server

> Aturan umum, alur kerja, label Issue, dan templat komentar penutupan ada di [`../MINGGU-20-26Nov2026.md`](../MINGGU-20-26Nov2026.md).
> Tiap anggota kelompok memegang satu tiket. Satu tiket hanya diambil satu orang di satu kelompok.

*Folder lab: `labs/pk-208` · Data: skala M*

> **Tomi, Tim Pemantauan (Jumat, 14.00):** Alarm 5xx berbunyi lagi. Setelah dilacak, asalnya request dengan `size=0` dan `page=-1`. Ada juga satu request dengan `size` sangat besar yang membuat server melambat.

> **Eko, Senior Developer (14.15):** Batasi saja `size` maksimal 20 di semua endpoint berhalaman, beres.

**Yang diharapkan**

- Nilai `page` dan `size` yang tidak sah ditolak dengan kode 4xx dan pesan yang jelas di semua endpoint berhalaman, bukan 500.
- Permintaan sah yang dipakai sekarang tetap berhasil.
- Permintaan dengan `size` sangat besar ditolak cepat tanpa memuat data (kurang dari 50 ms).
- p95 dengan `size=20` tidak lebih dari 10% di atas baseline Lab 0 kelompokmu.

## Perintah

1. `task lab LAB=pk-208`: menyalakan infrastruktur dan mengisi data (lihat skala di atas).
2. `task app` di terminal terpisah.
3. `task reproduce LAB=pk-208` sekali untuk pemanasan, lalu tiga kali untuk pengukuran.
   Ringkasan tersimpan di `labs/pk-208/out/baseline-table.md`.
4. Setelah perbaikanmu, ulangi pengukuran dengan cara yang sama dan bandingkan.
5. Commit semua perubahan, lalu `task evidence LAB=pk-208` dan lampirkan berkas bukti di PR. Jangan meng-commit berkas bukti.

Gunakan huruf kecil pada nilai `LAB` (`pk-208`).

## Paket konteks

Dokumen di folder [`konteks/`](konteks/). Tidak semua isinya relevan, dan beberapa isinya menjadi batasan yang harus kamu penuhi.

- [`01-kontrak-mitra-logistik.md`](konteks/01-kontrak-mitra-logistik.md)
- [`02-notulen-operasional-ekspor.md`](konteks/02-notulen-operasional-ekspor.md)
- [`03-pedoman-api-internal.md`](konteks/03-pedoman-api-internal.md)

## Catatan script k6

- Skrip mengirim lima jenis parameter halaman (size 20, size 200, size 0, page -1, size 5000) ke empat endpoint berhalaman, 3 putaran, lalu mencatat kode status dan rata-rata waktu tiap kasus di `out/baseline-table.md`.
- Sebelum perbaikan, `size=5000` pada `/api/admin/orders` bisa memakan beberapa detik atau lebih per request. Sabar menunggu run selesai.
- Kolom "Diharapkan" di tabel adalah patokan dari tiket dan paket konteks, bukan hasil sebelum perbaikanmu.
- Data skala M.
- Beban memakai laju tetap (model terbuka), tanpa threshold lulus atau gagal. Run ini mengukur.
- Jalankan dari root repo. Hasil mentah ada di `labs/pk-208/out/` dan tidak ikut Git.
- `RATE` dan `DURATION` bisa diubah lewat variabel lingkungan (macOS dan Linux: `RATE=1 task reproduce LAB=pk-208`; PowerShell: `$env:RATE=1`) atau `-e RATE=1` pada `k6 run` langsung. Catat perubahan itu di laporanmu.
