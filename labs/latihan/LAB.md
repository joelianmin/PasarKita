# Latihan: membedah tiket

**Konteks.** Sebuah tiket masuk ke tim PasarKita. Sebelum ada yang mengubah kode, tugasmu
adalah **memahami apa yang sebenarnya terjadi**: di lapisan mana masalahnya, apa buktinya,
dan apa yang belum kamu ketahui. Latihan ini melatih diagnosa, bukan perbaikan.

> **Teks tiket** diberikan pengajar (materi minggu 1). Tempel di sini atau di laporanmu
> sebelum mulai, lengkap dengan waktu kejadian yang disebut di dalamnya.

**Tugas.** Dari gejala pada tiket, tentukan lapisan tempat waktu atau kegagalan muncul,
kumpulkan bukti dari lebih dari satu sumber, dan tulis hipotesismu. **Jangan memperbaiki
apa pun.**

## Langkah singkat

1. Siapkan lingkungan dengan data skala S (`SCALE=S`), karena script beban memakai 200 penjual.
   - Dengan Docker: `task up PROFILE=core,observability` lalu `task seed SCALE=S`.
   - Tanpa Docker: `task native:up` lalu `task native:seed SCALE=S`.
2. Jalankan aplikasi di terminal terpisah: `task app`.
3. Pengajar menyiapkan kondisi latihan dan menyalakan traffic latar belakang
   (`task traffic`). Tunggu sampai pengajar mengatakan siap.
4. Buka Grafana (http://localhost:3000), dashboard "PasarKita Overview". **Dashboard memakai
   zona waktu UTC.** Cocokkan dengan waktu pada tiket sebelum menyimpulkan apa pun.
5. Jalankan beban untuk halaman yang disebut tiket:
   `k6 run labs/latihan/profile.k6.js` (5 request per detik selama 3 menit).
6. Diagnosa dengan metode di [bagian 6.1](../../docs/materi/06-diagnosa-dan-latihan.md):
   persempit dari luar ke dalam (k6, metrik server, trace, query database, sumber daya).
7. Tulis laporan.

Tidak semua yang muncul di log dan grafik berhubungan dengan tiket. Memilah mana yang
relevan adalah bagian dari latihan.

## Yang dikumpulkan

Laporan singkat (sekitar satu halaman) berisi:

| Bagian | Isi |
| --- | --- |
| Gejala | Apa yang dikeluhkan, kapan (dalam UTC), siapa yang terdampak |
| Reproduksi | Langkah dan perintah yang kamu jalankan, serta hasilnya |
| Bukti | Minimal tiga sumber berbeda (misalnya k6, Grafana, trace, `pg_stat_statements`), masing-masing dengan angka dan lokasi |
| Hipotesis | Satu atau dua dugaan penyebab, jelas ditandai belum terbukti |
| Yang diabaikan | Apa yang kamu temukan tapi kamu anggap tidak relevan, dan alasannya |
| Langkah berikutnya | Uji yang akan membuktikan atau menggugurkan hipotesis |

Pisahkan **fakta** dari **tafsiran**. Rumus bantu di
[bagian 7.5](../../docs/materi/07-menyerahkan-hasil.md): pengamatan, bukti, hipotesis,
dampak, cara memverifikasi.

## Bantuan

- Macet di lingkungan: [bagian 2.6](../../docs/materi/02-lingkungan-kerja.md) dan
  [6.1.5](../../docs/materi/06-diagnosa-dan-latihan.md).
- Cara membaca hasil k6: [bagian 4.2](../../docs/materi/04-mengukur-dan-mengamati.md).
- Cara membaca metrik dan trace: [bagian 4.1](../../docs/materi/04-mengukur-dan-mengamati.md).
