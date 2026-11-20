# PK-205: Daftar invoice penjual besar lambat

> Aturan umum, alur kerja, label Issue, dan templat komentar penutupan ada di [`../MINGGU-20-26Nov2026.md`](../MINGGU-20-26Nov2026.md).
> Tiap anggota kelompok memegang satu tiket. Satu tiket hanya diambil satu orang di satu kelompok.

*Folder lab: `labs/pk-205` · Data: skala M*

> **Maya, Account Manager (Selasa, 13.10):** Toko Raksasa, penjual terbesar kita, mengeluh halaman daftar invoice mereka lama sekali terbuka. Penjual kecil tidak ada yang mengeluh.

> **Andi, DBA kontrak (13.40):** Tabel invoice sudah dua juta baris. Biasanya ini karena tabelnya bloat. Saya jadwalkan `VACUUM FULL` malam ini.

**Yang diharapkan**

- p95 `GET /api/sellers/1/invoices` turun minimal 70%, tanpa memperlambat penjual lain.
- Tidak ada operasi yang mengunci tabel invoice lebih dari 1 detik, pada jam berapa pun.

## Perintah

1. `task lab LAB=pk-205`: menyalakan infrastruktur dan mengisi data (lihat skala di atas).
2. `task app` di terminal terpisah.
3. `task reproduce LAB=pk-205` sekali untuk pemanasan, lalu tiga kali untuk pengukuran.
   Ringkasan tersimpan di `labs/pk-205/out/baseline-table.md`.
4. Setelah perbaikanmu, ulangi pengukuran dengan cara yang sama dan bandingkan.
5. Commit semua perubahan, lalu `task evidence LAB=pk-205` dan lampirkan berkas bukti di PR. Jangan meng-commit berkas bukti.

Gunakan huruf kecil pada nilai `LAB` (`pk-205`).

## Paket konteks

Dokumen di folder [`konteks/`](konteks/). Tidak semua isinya relevan, dan beberapa isinya menjadi batasan yang harus kamu penuhi.

- [`01-sla-operasional.md`](konteks/01-sla-operasional.md)
- [`02-riset-ux-halaman-invoice.md`](konteks/02-riset-ux-halaman-invoice.md)
- [`03-laporan-dba-oktober.md`](konteks/03-laporan-dba-oktober.md)

## Catatan script k6

- Endpoint: `GET /api/sellers/{id}/invoices?page=0&size=20`, laju 2 req/detik, 3 menit. Sekitar seperempat request ke penjual 1 (Toko Raksasa), sisanya ke penjual lain. Ringkasan dipisah dua baris supaya keduanya terlihat.
- Data skala M.
- Beban memakai laju tetap (model terbuka), tanpa threshold lulus atau gagal. Run ini mengukur.
- Jalankan dari root repo. Hasil mentah ada di `labs/pk-205/out/` dan tidak ikut Git.
- `RATE` dan `DURATION` bisa diubah lewat variabel lingkungan (macOS dan Linux: `RATE=1 task reproduce LAB=pk-205`; PowerShell: `$env:RATE=1`) atau `-e RATE=1` pada `k6 run` langsung. Catat perubahan itu di laporanmu.
