# Daftar lab

| Lab | Petunjuk | Script beban | Data | Status |
| --- | --- | --- | --- | --- |
| Lab 0: Baseline | [`0/LAB.md`](0/LAB.md) | [`0/reproduce.k6.js`](0/reproduce.k6.js) | Skala M | Tersedia |
| Latihan: membedah tiket | [`latihan/LAB.md`](latihan/LAB.md) | [`latihan/profile.k6.js`](latihan/profile.k6.js) | Skala S | Tersedia |
| 20–26 Nov 2026: akses data (`pk-201` sampai `pk-208`, contoh `pk-200`) | [`MINGGU-20-26Nov2026.md`](MINGGU-20-26Nov2026.md), `LAB.md` di tiap folder | `pk-20x/reproduce.k6.js` | M, L untuk `pk-204` dan `pk-206` | Disiapkan |
| Lab berikutnya | - | - | - | Ditambahkan bertahap |

Setiap lab punya folder sendiri berisi `LAB.md` (petunjuk), script k6, dan `out/` (hasil
mentah k6, tidak ikut Git).

## Skala data

| Skala | Penjual | Produk | Pelanggan | Order | Dipakai untuk |
| --- | --- | --- | --- | --- | --- |
| S | 200 | 5.000 | 10.000 | 50.000 | Latihan ringan |
| M | 2.000 | 50.000 | 200.000 | 1.000.000 | Lab 0 dan lab yang menyebut skala M |
| L | 20.000 | 500.000 | 2.000.000 | 1.000.000 | Lab yang menyebut skala L (data lebih besar, seed lebih lama, disk lebih besar) |

`task seed SCALE=...` menolak nilai selain S, M, dan L. `task lab LAB=<nomor>` memilih skala sesuai lab.

## Perintah yang dipakai

| Tujuan | Dengan Docker | Tanpa Docker |
| --- | --- | --- |
| Siapkan lingkungan lab | `task lab LAB=0` | `task native:up`, lalu `task native:seed SCALE=M` |
| Jalankan aplikasi | `task app` | `task app` |
| Jalankan beban lab | `task reproduce LAB=0` | `task reproduce LAB=0` |
| Buat file bukti | `task evidence LAB=0` | `task evidence LAB=0` |

Daftar lengkap ada di [`../docs/materi/referensi.md`](../docs/materi/referensi.md).

## Sebelum mulai

1. Baca [`../docs/MATERI-DASAR.md`](../docs/MATERI-DASAR.md) untuk jalur belajar.
2. Pastikan `task doctor` lolos (atau gunakan jalur tanpa Docker, lihat
   [`../infra/native/README.md`](../infra/native/README.md)).
3. Kumpulkan hasil dengan alur di
   [bagian 7](../docs/materi/07-menyerahkan-hasil.md): commit dulu, baru `task evidence`.

Jangan memperbaiki apa pun di Lab 0 dan latihan tiket. Tugasnya mengukur dan mendiagnosa.
