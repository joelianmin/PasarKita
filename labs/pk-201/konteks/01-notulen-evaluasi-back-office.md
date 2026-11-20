# Notulen Rapat Evaluasi Back-office

**Tanggal:** 18 September 2026 · **Peserta:** Sari (Operasional), Bayu (Product), Rudi (Front-end), Lina (QA)

## 1. Halaman daftar order

- Kolom yang tampil saat ini: **Nomor order, Nama pelanggan, Total, Status, Tanggal order, Jumlah item**. Tim operasional menyatakan keenam kolom ini sudah cukup dan tidak perlu ditambah.
- Urutan default: order terbaru di atas.
- Tim operasional sering melompat ke nomor halaman tertentu saat menelusuri order lama, jadi **navigasi nomor halaman beserta total halaman tetap dibutuhkan**.
- Filter status paling sering dipakai untuk PAID dan SHIPPED.

## 2. Permintaan lain (masuk backlog)

- Ekspor daftar order ke CSV. Prioritas rendah.
- Warna label status disamakan dengan aplikasi pelanggan. Ditangani tim desain.

## 3. Tindak lanjut

- Rudi mendokumentasikan field yang dibaca front-end back-office.
- Lina menambahkan skenario uji untuk navigasi halaman.
