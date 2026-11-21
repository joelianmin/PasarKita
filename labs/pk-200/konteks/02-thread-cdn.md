# Thread #infra-cdn

**Rudi (Front-end), 13 Nov 2026, 09.15**
CDN menyimpan gambar thumbnail berdasarkan URL persisnya. Kalau `<kode warna>` di URL berubah, CDN menganggapnya gambar baru dan membuat ulang semuanya dari awal.

**Tomi (Pemantauan), 09.22**
Terakhir kali itu terjadi, lalu lintas ke server gambar naik delapan kali lipat selama dua hari. Mohon jangan sampai terulang.

**Bayu (Product), 09.30**
Dicatat. URL thumbnail yang sudah ada dianggap kontrak dengan CDN dan aplikasi mobile.

**Rudi, 09.41**
Satu lagi: aplikasi menampilkan thumbnail profil dalam ukuran 200 x 200 piksel. Gambar 1600 x 1600 hanya dipakai untuk fitur zoom di halaman detail produk.
