# Pengumuman: persiapan perkuliahan PasarKita

- **Pertemuan:** Jumat, 6 November 2026, pukul 09.00-10.40 dan 13.00-16.40 WIB, **online**
- **Tautan sesi:** N/A
- **Beban belajar:** + 380 menit belajar mandiri
- **Topik:** Backend-1
- **Repo materi:** [github.com/joelianmin/PasarKita](https://github.com/joelianmin/PasarKita)

## Siapkan sebelum kelas

1. **Pasang alat:**
   [JDK 21](https://adoptium.net/temurin/releases/?version=21),
   [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Windows memakai backend WSL2),
   [Git](https://git-scm.com/downloads),
   [k6](https://grafana.com/docs/k6/latest/set-up/install-k6/), dan
   [Task](https://taskfile.dev/installation/). Panduan lengkapnya ada di
   [README, bagian Prasyarat](https://github.com/joelianmin/PasarKita/blob/main/README.md#prasyarat)
   dan [materi bagian 2.1](https://github.com/joelianmin/PasarKita/blob/main/docs/materi/02-lingkungan-kerja.md#21-prasyarat).
2. **Beri memori Docker minimal 6 GB.** Di Windows atur lewat `%UserProfile%\.wslconfig`, lalu
   jalankan `wsl --shutdown`. Docker Desktop terbaru di macOS membutuhkan macOS 14 atau lebih
   baru; kalau macOS-mu lebih lama, hubungi pengajar sebelum kelas.
3. **Unduh dependensi dan image di rumah.** Setelah repo di-clone, jalankan `task warmup`.
   Unduhannya besar. Jangan menunggu sesi dimulai, dan jangan mengunduh sambil berbagi layar.
4. **Periksa lingkungan:** `task doctor`. Semua baris harus `OK`. Kirim keluaran yang gagal ke
   pengajar sebelum kelas, bukan saat kelas.
5. **Sisakan ruang disk yang lega.** Data lab dan image Docker memakan beberapa GB; data skala M
   untuk Lab 0 lebih besar lagi.
6. **Siapkan akun GitHub.** Satu anggota tiap kelompok membuat repo kelompok dari templat
   (langkah di
   [materi bagian 7.1](https://github.com/joelianmin/PasarKita/blob/main/docs/materi/07-menyerahkan-hasil.md#71-membuat-repo-kelompok)):
   pilih **Private**, undang anggota kelompok dan akun pengajar
   ([`joelianmin`](https://github.com/joelianmin)).
7. **Baca materi.** Mulai dari
   [daftar isi materi](https://github.com/joelianmin/PasarKita/blob/main/docs/MATERI-DASAR.md), lalu
   [bagian 1 (fondasi)](https://github.com/joelianmin/PasarKita/blob/main/docs/materi/01-fondasi.md) dan
   [bagian 2 (lingkungan kerja)](https://github.com/joelianmin/PasarKita/blob/main/docs/materi/02-lingkungan-kerja.md).
   Pertanyaan "cek diri" di akhir tiap topik adalah cara menguji pemahamanmu.
8. **Siapkan laptop dengan baterai penuh dan charger.** Sesi berlangsung berjam-jam, dan
   pengukuran paling baik dilakukan saat laptop tercolok.
9. **Siapkan untuk sesi online:** koneksi stabil, headset, dan kemampuan berbagi layar
   (termasuk izin yang dibutuhkan sistem operasimu). Uji sebelum sesi, bukan saat sesi.

## Aturan main

- Pada Lab 0 dan latihan membedah tiket, tugasnya **mengukur dan mencatat. Jangan memperbaiki
  apa pun.**
- `GROUP_SEED` kelompok diberikan pengajar. Isi di `group.env` sebagai commit pertama dan
  **jangan diubah setelah itu**.
- **Commit dulu, baru `task evidence`.** File bukti dilampirkan ke pull request, tidak
  di-commit. Aturan lengkapnya di
  [materi bagian 7.4](https://github.com/joelianmin/PasarKita/blob/main/docs/materi/07-menyerahkan-hasil.md#74-bukti-dengan-hash).
- Saat mengukur, tutup aplikasi berat lain dan jangan menjalankan hal lain di laptop. Berbagi
  layar dan video call memakai CPU, jadi matikan kamera dan tutup aplikasi rapat tambahan saat
  run pengukuran berlangsung, lalu catat bahwa sesi online berjalan.
- Repo kelompok harus **privat**.

## Kalau macet

1. Buka [daftar isi materi](https://github.com/joelianmin/PasarKita/blob/main/docs/MATERI-DASAR.md#cari-berdasarkan-masalah),
   tabel **"Cari berdasarkan masalah"**.
2. Ikuti
   [metode diagnosa (bagian 6.1)](https://github.com/joelianmin/PasarKita/blob/main/docs/materi/06-diagnosa-dan-latihan.md#61-diagnosa-dan-lokalisasi-masalah-secara-sistematis):
   baca pesan error sampai habis, cari lapisan yang pertama tidak sehat, catat buktinya.
3. Saat bertanya (atau berbagi layar), sertakan: pesan error persis, keluaran `task doctor`,
   langkah yang kamu lakukan, dan apa yang sudah kamu coba. "Tidak jalan" saja tidak bisa dibantu.

## Tidak bisa memakai Docker?

Hubungi pengajar. Ada
[jalur alternatif tanpa Docker](https://github.com/joelianmin/PasarKita/blob/main/infra/native/README.md),
tetapi baru diuji di macOS, dan `task test` tidak berjalan di jalur itu.

## Kontak

Group WA Kelas.

## Daftar tautan

- Repo materi: https://github.com/joelianmin/PasarKita
- Daftar isi materi: https://github.com/joelianmin/PasarKita/blob/main/docs/MATERI-DASAR.md
- Pasang alat (materi 2.1): https://github.com/joelianmin/PasarKita/blob/main/docs/materi/02-lingkungan-kerja.md#21-prasyarat
- Repo kelompok dari templat (materi 7.1): https://github.com/joelianmin/PasarKita/blob/main/docs/materi/07-menyerahkan-hasil.md#71-membuat-repo-kelompok
- Diagnosa masalah (materi 6.1): https://github.com/joelianmin/PasarKita/blob/main/docs/materi/06-diagnosa-dan-latihan.md#61-diagnosa-dan-lokalisasi-masalah-secara-sistematis
- Tanpa Docker: https://github.com/joelianmin/PasarKita/blob/main/infra/native/README.md
- JDK 21: https://adoptium.net/temurin/releases/?version=21
- Docker Desktop: https://www.docker.com/products/docker-desktop/
- Git: https://git-scm.com/downloads
- k6: https://grafana.com/docs/k6/latest/set-up/install-k6/
- Task: https://taskfile.dev/installation/
