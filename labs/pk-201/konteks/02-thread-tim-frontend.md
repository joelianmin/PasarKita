# Thread #backoffice-dev

**Rudi (Front-end), 21 Sep 2026, 10.12**
Sesuai tindak lanjut rapat, ini field dari respons `GET /api/admin/orders` yang dibaca halaman daftar order:

- `content[].id`
- `content[].customer.name`
- `content[].total`
- `content[].status`
- `content[].createdAt`
- `content[].lines`, hanya dihitung panjangnya untuk kolom "Jumlah item"
- `page.number` dan `page.totalPages` untuk navigasi halaman

Field lain di respons tidak kami pakai. Tolong jangan ubah nama atau struktur field yang kami baca, karena rilis front-end back-office hanya sebulan sekali.

**Lina (QA), 10.20**
Dicatat. Kalau `lines` diganti angka langsung, front-end perlu rilis ulang, ya?

**Rudi, 10.24**
Betul. Untuk sekarang anggap struktur di atas sebagai kontrak.

**Bayu (Product), 10.31**
Oke. Sekalian, tim desain minta ikon status diganti bulan depan, tapi itu murni front-end.
