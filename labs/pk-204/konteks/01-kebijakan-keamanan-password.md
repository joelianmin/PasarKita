# Kebijakan Keamanan Penyimpanan Password

**Versi:** 2.1 · **Berlaku sejak:** 1 Januari 2026 · **Pemilik:** Tim Keamanan Informasi

1. Password pelanggan disimpan dengan **bcrypt, cost minimal 10**.
2. Penurunan cost, atau penggantian algoritma, wajib disetujui Kepala Keamanan Informasi dan dicatat di register risiko.
3. Akun dikunci sementara selama 15 menit setelah 5 kali gagal login berturut-turut.
4. Password tidak boleh dicatat di log dalam bentuk apa pun.

## Catatan revisi berikutnya

Evaluasi algoritma Argon2id dijadwalkan pada kuartal 3 tahun 2027. Sampai saat itu, poin 1 dan 2 tetap berlaku.
