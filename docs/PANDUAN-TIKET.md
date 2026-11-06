# Panduan: mendaftarkan tiket sebagai Issue

Tiket dari pengajar kamu daftarkan sendiri sebagai Issue di repo kelompok. Tujuannya melatih
cara kerja tim, bukan sekadar menyalin: menulis jelas, memilah prioritas, membagi kerja, dan
melaporkan kemajuan.

## Langkah

1. **Baca tiketnya sampai habis**, lalu tulis ulang dengan kata-katamu sendiri.
2. Di repo kelompok: **Issues, New issue, Tiket kerja**. Isi semua bagian.
3. **Satu tiket, satu Issue.** Jangan menggabungkan dua gejala.
4. Isi **Assignees**. Setiap anggota memegang minimal satu tiket. Yang mendaftarkan tidak harus
   yang mengerjakan, dan penulisnya sebaiknya bergantian.
5. Pastikan label `tiket` terpasang (templat sudah memasangnya).
6. Kerjakan di cabang sendiri, lalu buka PR dengan `Closes #<nomor>` di deskripsinya. Issue
   tertutup lewat PR, bukan ditutup manual.
7. Beri **komentar progres** di Issue saat ada temuan atau hambatan, dengan waktu dalam WIB.
   Kalau macet, buat Issue terpisah dari templat **Macet / ada error**.

## Judul dan isi yang baik

| Kurang baik | Lebih baik |
| --- | --- |
| "Aplikasi lambat" | "Halaman katalog p95 1,2 detik pada skala M, 21 query per request" |
| "Error di checkout" | "POST /payments/authorize gagal dengan RST_STREAM: Stream cancelled" |
| "Perbaiki database" | "Pencarian produk melakukan sequential scan pada tabel produk" |

Pisahkan **pengamatan** (yang kamu lihat dan ukur) dari **dugaan** (yang kamu kira penyebabnya).
Dugaan boleh ditulis, tetapi beri tanda.

## Catatan

- Pada Lab 0 dan latihan membedah tiket, tugasnya mengukur dan mencatat. Jangan memperbaiki
  kode.
- Jangan menulis `GROUP_SEED` di Issue, komentar, PR, atau pesan commit.
- Penanggung jawab per tiket dipakai untuk melihat pembagian kerja. Kalau satu laptop dipakai
  bersama, tulis di Issue siapa yang mengetik dan siapa yang menganalisis.
