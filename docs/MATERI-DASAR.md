# Materi dasar PasarKita

Bahan minimal agar kamu paham apa yang kamu ukur dan bisa menjalankan lab tanpa tersangkut.
Materi dibagi menjadi tujuh bagian yang disusun menurut urutan kerja, ditambah satu bagian referensi.

## Cara memakai materi

- **Angka pertama pada nomor bagian adalah nomor berkas.** Contoh: bagian `6.1.3` ada di `materi/06-diagnosa-dan-latihan.md`.
- Setiap bagian dibuka dengan **tujuan, prasyarat, dan kemampuan yang kamu dapat**, dan ditutup dengan tautan ke bagian sebelum dan sesudahnya.
- Topik konsep (bagian 1, 3, 4, 5, 6) diakhiri **pertanyaan cek diri** (`> Cek diri: ...`). Jawab dulu sebelum lanjut. Topik prosedural (bagian 2 dan 7) berisi langkah dan perintah yang dicoba langsung.
- Contoh perintah memakai bash. Di Windows PowerShell 5.1 pakai `curl.exe`, dan ganti `./mvnw` dengan `.\mvnw.cmd`.
- Pegangan umum: ukur dulu, ubah satu hal, ukur lagi.

## Peta isi

| Bagian | Pertanyaan yang dijawab | Kapan dibaca |
| --- | --- | --- |
| [1. Fondasi](materi/01-fondasi.md) | Bagaimana performa diukur? Bagaimana HTTP bekerja? | Pertama, sebelum lab apa pun |
| [2. Lingkungan kerja](materi/02-lingkungan-kerja.md) | Bagaimana memasang dan menjalankan semuanya, dengan atau tanpa Docker? | Hari pertama, saat menyiapkan laptop |
| [3. Memahami sistem](materi/03-memahami-sistem.md) | Bagaimana aplikasi dan basis data bekerja, dan di mana biaya muncul? | Sebelum membaca kode dan hasil ukur |
| [4. Mengukur dan mengamati](materi/04-mengukur-dan-mengamati.md) | Bagaimana mengukur dan mengamati dengan benar? | Sebelum Lab 0 |
| [5. Mutu dan desain](materi/05-mutu-dan-desain.md) | Bagaimana menilai desain, mutu, dan keamanan? | Saat menilai kode atau menyusun target mutu |
| [6. Diagnosa dan latihan](materi/06-diagnosa-dan-latihan.md) | Bagaimana menemukan penyebab masalah, dan melatihnya dengan sengaja? | Saat macet, dan untuk berlatih setelah Lab 0 |
| [7. Menyerahkan hasil](materi/07-menyerahkan-hasil.md) | Bagaimana mengemas hasil menjadi bukti yang sah? | Saat mengumpulkan hasil Lab 0 |
| [Referensi](materi/referensi.md) | Di mana kodenya? Apa arti istilah ini? Perintah apa yang dipakai? | Kapan saja |

## Daftar isi lengkap

**[Bagian 1: Fondasi](materi/01-fondasi.md)**

- 1.1 Cara berpikir tentang performa
- 1.2 HTTP dan REST

**[Bagian 2: Lingkungan kerja](materi/02-lingkungan-kerja.md)**

- 2.1 Prasyarat
- 2.2 Maven dan Maven Wrapper
- 2.3 Docker dan Compose (termasuk gambaran deployment)
- 2.4 Jika Docker tidak bisa dipasang
- 2.5 Menjalankan dari nol
- 2.6 Pemecahan masalah

**[Bagian 3: Memahami sistem](materi/03-memahami-sistem.md)**

- 3.1 Aplikasi: Spring Boot, JPA, transaksi
- 3.2 Database: PostgreSQL, indeks, EXPLAIN

**[Bagian 4: Mengukur dan mengamati](materi/04-mengukur-dan-mengamati.md)**

- 4.1 Observability: metrik, trace, log
- 4.2 k6
- 4.3 Zona waktu: kesadaran dan penyesuaian

**[Bagian 5: Mutu dan desain](materi/05-mutu-dan-desain.md)**

- 5.1 Prinsip desain SOLID
- 5.2 Model mutu ISO/IEC 25010
- 5.3 Keamanan: OWASP

**[Bagian 6: Diagnosa dan latihan](materi/06-diagnosa-dan-latihan.md)**

- 6.1 Diagnosa dan lokalisasi masalah secara sistematis
- 6.2 Belajar dari error: sengaja membuat kesalahan

**[Bagian 7: Menyerahkan hasil](materi/07-menyerahkan-hasil.md)**

- 7.1 Membuat repo kelompok
- 7.2 Git yang dipakai di lab
- 7.3 Alur kerja dengan pull request
- 7.4 Bukti dengan hash
- 7.5 Menulis BASELINE.md

**[Referensi](materi/referensi.md)**

- R.1 Peta kode
- R.2 Kosakata singkat
- R.3 Ringkasan perintah

## Jalur belajar

| Situasi | Baca |
| --- | --- |
| Hari pertama: menyiapkan laptop dan repo kelompok | 7.1, 2.1, 2.2, 2.3 atau 2.4, 2.5, 2.6. Sambil menunggu unduhan: 1.1 |
| Sebelum Lab 0 | 1.1, 1.2, 3.1, 3.2, 4.1, 4.2, 4.3 |
| Saat mengerjakan Lab 0 | 4.2 (cara menjalankan), 6.1 jika macet, 7.1 sampai 7.5 untuk menyiapkan repo dan mengumpulkan |
| Setelah Lab 0, minggu 2 sampai 6 | 5.1, 5.2, 5.3, lalu 6.2 untuk latihan |

## Cari berdasarkan masalah

| Masalahmu | Buka |
| --- | --- |
| Alat tidak dikenali atau instalasi gagal | 2.1, 2.6, lalu 6.1.3 dan 6.1.5 |
| `./mvnw` tidak jalan | 2.2 |
| Docker tidak bisa dipasang | 2.4 dan `infra/native/README.md` |
| Port bentrok atau container mati | 6.1.5, 6.1.6 |
| Aplikasi tidak mau start | 6.1.3 (lapisan 5 sampai 7), 6.1.5 |
| Hasil k6 aneh (banyak 404, 402, 409, atau `dropped_iterations`) | 4.2, 6.1.5 |
| Grafana kosong atau jam tidak cocok dengan log | 4.1, 4.3, 6.1.5 |
| Tidak paham sebuah istilah | R.2 |
| Ingin tahu kode mana yang mengurus sesuatu | R.1 |
| Menulis `BASELINE.md` | 7.5 |
| Ingin berlatih membaca error | 6.2 |

## Dokumen terkait

- [`DEPLOYMENT.md`](DEPLOYMENT.md): diagram deployment, tabel port, dan varian tanpa Docker.
- [`../README.md`](../README.md): mulai cepat dan daftar perintah.
- [`../labs/0/LAB.md`](../labs/0/LAB.md): instruksi Lab 0.
- [`../infra/native/README.md`](../infra/native/README.md): menjalankan tanpa Docker.
