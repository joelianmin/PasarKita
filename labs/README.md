# Daftar lab

| Lab | Petunjuk | Script beban | Data | Status |
| --- | --- | --- | --- | --- |
| Lab 0: Baseline | [`0/LAB.md`](0/LAB.md) | [`0/reproduce.k6.js`](0/reproduce.k6.js) | Skala M | Tersedia |
| Latihan: membedah tiket | [`latihan/LAB.md`](latihan/LAB.md) | [`latihan/profile.k6.js`](latihan/profile.k6.js) | Skala S | Tersedia |
| Lab minggu 2–6 | - | - | - | Ditambahkan bertahap |

Setiap lab punya folder sendiri berisi `LAB.md` (petunjuk), script k6, dan `out/` (hasil
mentah k6, tidak ikut Git).

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
