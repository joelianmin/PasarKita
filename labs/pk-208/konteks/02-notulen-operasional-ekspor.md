# Notulen Rapat Operasional

**Tanggal:** 11 November 2026 · **Peserta:** Sari (Operasional), Tomi (Pemantauan), Bayu (Product)

## 1. Ekspor daftar pesanan

- Seorang staf sempat meminta seluruh pesanan dalam satu halaman untuk diekspor ke CSV, dan dasbor terasa membeku.
- Disepakati: ekspor dilakukan dengan membaca halaman berulang, **bukan** satu permintaan raksasa. Ukuran halaman 200 sudah cukup bagi tim operasional.
- Tidak ada kebutuhan bisnis untuk satu respons lebih dari 200 baris.

## 2. Alarm pemantauan

- Pemantauan mencatat respons 5xx yang berasal dari input pengguna yang salah, dan itu mengacaukan alarm.
- Tomi meminta input yang salah dijawab 4xx. Alarm 5xx hanya boleh berbunyi untuk kegagalan sistem.

## 3. Lain-lain

- Warna label status disamakan dengan aplikasi pelanggan. Ditangani tim desain.
