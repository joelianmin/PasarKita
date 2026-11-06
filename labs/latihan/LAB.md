# Latihan: membedah tiket

**Konteks.** Sebuah tiket masuk ke tim PasarKita. Sebelum ada yang mengubah kode, tugasmu
adalah **memahami apa yang sebenarnya terjadi**: di lapisan mana masalahnya, apa buktinya,
dan apa yang belum kamu ketahui. Latihan ini melatih diagnosa, bukan perbaikan.

> **Teks tiket** ada di [`TIKET.md`](TIKET.md). Baca tiketmu sebelum mulai, lengkap dengan
> waktu kejadian yang disebut di dalamnya.

**Tugas.** Dari gejala pada tiket, tentukan lapisan tempat waktu atau kegagalan muncul,
kumpulkan bukti dari lebih dari satu sumber, dan tulis hipotesismu. **Jangan memperbaiki
apa pun.**

## Tiket pemanasan dan tiket tambahan

- **Tiket pemanasan (#4821)** dikerjakan oleh **semua anggota** untuk membiasakan alur ini.
- **Tiket tambahan (#4822 sampai #4831, 10 tiket)**: kelompok beranggota *N* mengerjakan
  **minimal *N* tiket**, tiap anggota paling sedikit satu, boleh lebih (undi atau pilih).
  Teksnya ada di [`TIKET.md`](TIKET.md).
- Daftarkan tiap tiket sebagai Issue (templat "Tiket kerja") dengan penanggung jawab. Tiap
  anggota menulis laporannya sendiri di `labs/latihan/LAPORAN-<nomor tiket>.md`.
- Tiket tambahan memakai data **skala M** seperti Lab 0 (`task lab LAB=0`), karena sebagian
  gejalanya baru terlihat pada data sebesar itu. Tiket #4821 memakai skala S, seperti di bawah.
- Langkah 3 (`task latihan:mulai`) hanya untuk tiket #4821. Tiket tambahan memakai perilaku
  aplikasi apa adanya, jadi tidak ada kondisi yang perlu dinyalakan.

## Langkah singkat (tiket #4821)

1. Siapkan lingkungan dengan data skala S (`SCALE=S`), karena script beban memakai 200 penjual.
   - Dengan Docker: `task up PROFILE=core,observability` lalu `task seed SCALE=S`.
   - Tanpa Docker: `task native:up` lalu `task native:seed SCALE=S`.
2. Jalankan aplikasi di terminal terpisah: `task app`.
3. Jalankan `task latihan:mulai` kapan pun kelompokmu siap; tidak perlu bersamaan dengan
   kelompok lain. Perintah itu mencetak jam kondisi dinyalakan dan jam masalah dilaporkan pada
   tiketmu (keduanya WIB); **catat kedua jam itu**. Buka terminal lain dan jalankan traffic
   latar belakang: `task traffic DURATION=3h`. Tunggu sekitar 10 menit sampai grafik di
   Grafana menunjukkan perubahan.
4. Buka Grafana (http://localhost:3000), dashboard "PasarKita Overview". **Dashboard memakai
   zona waktu UTC**, sedangkan jam yang dicetak tadi WIB. Ubah ke UTC sebelum mencocokkan,
   dan cocokkan dengan waktu pada tiket sebelum menyimpulkan apa pun.
5. Jalankan beban untuk halaman yang disebut tiket:
   `k6 run labs/latihan/profile.k6.js` (2 request per detik selama 3 menit). Kalau laptopmu
   terasa tidak kuat, kurangi dengan `RATE=1` dan catat itu di laporan.
6. Diagnosa dengan metode di [bagian 6.1](../../docs/materi/06-diagnosa-dan-latihan.md):
   persempit dari luar ke dalam (k6, metrik server, trace, query database, sumber daya).
7. Tulis laporan di `labs/latihan/LAPORAN-4821.md` (salin dari `LAPORAN.md`), commit, dan push.
   Buka PR dengan `Closes #<nomor Issue tiket>`.

Setelah selesai, jalankan `task latihan:selesai` dan hentikan `task traffic`.

Tidak semua yang muncul di log dan grafik berhubungan dengan tiket. Memilah mana yang
relevan adalah bagian dari latihan.

## Yang dikumpulkan

Laporan singkat (sekitar satu halaman) per tiket, berupa berkas Markdown
`labs/latihan/LAPORAN-<nomor tiket>.md` di repo kelompok (salin dari `LAPORAN.md`). Isinya:

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
