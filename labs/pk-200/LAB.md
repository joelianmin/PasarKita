# PK-200: Profil toko lambat sejak thumbnail dinamis dinyalakan

> Aturan umum, alur kerja, label Issue, dan templat komentar penutupan ada di [`../MINGGU-20-26Nov2026.md`](../MINGGU-20-26Nov2026.md).
> Tiap anggota kelompok memegang satu tiket. Satu tiket hanya diambil satu orang di satu kelompok.

*Folder lab: `labs/pk-200` · Data: skala S · Tiket contoh, lanjutan dari tiket #4821 (`labs/latihan/TIKET.md`) · Dikerjakan di https://github.com/joelianmin/PasarKitaGood*

> **Maya, Account Manager (Senin, 10.05):** Toko Raksasa dan beberapa toko lain mengeluh halaman profil toko mereka butuh beberapa detik untuk dibuka. Diagnosisnya sudah ada di tiket #4821.

> **Eko, Senior Developer (10.20):** Matikan saja flag thumbnail-nya, beres.

> **Dimas, QA (10.40):** Atau tambah server aplikasi supaya kuat?

**Yang diharapkan**

- p95 `GET /api/sellers/{id}/profile` pada 2 req/detik dengan flag `seller.dynamic-thumbnail` menyala tidak lebih dari dua kali p95 saat flag mati, diukur di laptop yang sama.
- Tidak ada request yang gagal pada 2 req/detik.
- Respons profil tidak berubah selama flag menyala.

## Perintah

1. `task lab LAB=pk-200`: menyalakan infrastruktur dan mengisi data (lihat skala di atas).
2. `task app` di terminal terpisah.
3. `task reproduce LAB=pk-200` sekali untuk pemanasan, lalu tiga kali untuk pengukuran.
   Ringkasan tersimpan di `labs/pk-200/out/baseline-table.md`.
4. Setelah perbaikanmu, ulangi pengukuran dengan cara yang sama dan bandingkan.
5. Commit semua perubahan, lalu `task evidence LAB=pk-200` dan lampirkan berkas bukti di PR. Jangan meng-commit berkas bukti.

Gunakan huruf kecil pada nilai `LAB` (`pk-200`).

## Paket konteks

Dokumen di folder [`konteks/`](konteks/). Tidak semua isinya relevan, dan beberapa isinya menjadi batasan yang harus kamu penuhi.

- [`01-keputusan-produk-thumbnail.md`](konteks/01-keputusan-produk-thumbnail.md)
- [`02-thread-cdn.md`](konteks/02-thread-cdn.md)
- [`03-anggaran-infrastruktur.md`](konteks/03-anggaran-infrastruktur.md)

## Catatan script k6

- Tiket contoh. Pengerjaan lengkapnya (Issue, jurnal, PR, tinjauan, bukti, komentar penutupan) ada di https://github.com/joelianmin/PasarKitaGood.
- Data skala S (200 penjual), sama seperti tiket #4821. Nyalakan kondisi dengan `task latihan:mulai` dan matikan dengan `task latihan:selesai`.
- Endpoint: `GET /api/sellers/{id}/profile`, laju 2 req/detik, 3 menit, sekitar 30% request ke penjual 1. Ringkasan dipisah: penjual 1 dan penjual lain.
- Bila laptopmu jenuh (banyak request gagal) saat flag menyala, ukur juga dengan `RATE=1` dan catat keduanya.
- Beban memakai laju tetap (model terbuka), tanpa threshold lulus atau gagal. Run ini mengukur.
- Jalankan dari root repo. Hasil mentah ada di `labs/pk-200/out/` dan tidak ikut Git.
- `RATE` dan `DURATION` bisa diubah lewat variabel lingkungan (macOS dan Linux: `RATE=1 task reproduce LAB=pk-200`; PowerShell: `$env:RATE=1`) atau `-e RATE=1` pada `k6 run` langsung. Catat perubahan itu di laporanmu.
