# Tiket latihan membedah tiket

Setiap tiket menggambarkan **gejala dari sudut pandang pengguna atau tim**, bukan penyebabnya.
Tugasmu memahami apa yang terjadi, mengumpulkan bukti dari lebih dari satu sumber, menulis
hipotesis, dan mencatat yang belum kamu ketahui. **Jangan memperbaiki apa pun.**

Cara mengerjakan ada di [`LAB.md`](LAB.md), dan templat laporan di [`LAPORAN.md`](LAPORAN.md).

## Pembagian

- **#4821 (pemanasan):** dikerjakan oleh **semua anggota** kelompok.
- **#4822 sampai #4831 (tambahan, 10 tiket):** kelompok beranggota *N* mengerjakan **minimal
  *N* tiket tambahan**; tiap anggota memegang **paling sedikit satu**. Boleh mengambil lebih.
  Dibagi dengan undi atau memilih. Satu tiket hanya diambil satu orang di satu kelompok.
- **#4831 (integrasi)** hanya boleh diambil bila kelompok mengambil sedikitnya tiga tiket lain,
  dan dikerjakan setelah rekan menyerahkan temuannya.

Untuk setiap tiket: daftarkan sebagai Issue dengan penanggung jawab (templat "Tiket kerja"),
dan tulis laporan sendiri di `labs/latihan/LAPORAN-<nomor tiket>.md`.

Tiket tambahan: ukur dengan beban Lab 0 (`task reproduce`) atau skrip k6 sederhana buatanmu
untuk endpoint itu. Gunakan data **skala M** seperti Lab 0 (`task lab LAB=0`). Waktu dalam WIB.

---

## Tiket pemanasan

**Tiket #4821: Halaman profil toko terasa lambat**

| | |
| --- | --- |
| Dari | Tim Dukungan Penjual |
| Prioritas | Sedang |
| Dilaporkan | Pada **jam tiket** (WIB): jam yang dicetak oleh `task latihan:mulai` sebagai "Tiketmu melaporkan masalah sekitar pukul ..." |
| Komponen | PasarKita, halaman profil toko (`GET /api/sellers/{id}/profile`) |

**Keluhan.** Beberapa penjual menghubungi dukungan dan bilang halaman profil toko mereka
terasa sangat lama dibuka, sampai beberapa detik. Kemarin masih cepat. Keluhan paling banyak
datang dari toko yang produknya banyak.

**Yang kami tahu.**
- Halaman lain (katalog, pencarian, keranjang) tidak dikeluhkan.
- Tidak ada rilis kode yang tercatat hari ini. Kami tidak bisa memastikan apakah ada perubahan
  konfigurasi.
- Belum jelas apakah semua toko terdampak atau hanya sebagian.

**Yang diminta.** Pahami apa yang sebenarnya terjadi dan di lapisan mana. Kumpulkan bukti dari
lebih dari satu sumber, tulis hipotesismu, dan catat apa yang belum kamu ketahui. **Jangan
memperbaiki apa pun.**

**Catatan.**
- Waktu pada tiket ini dalam WIB. Alat ukur kita mungkin memakai zona waktu lain.
- Tidak semua yang muncul di log dan grafik berhubungan dengan tiket ini.
- Daftarkan tiket ini sebagai Issue di repo kelompok (templat "Tiket kerja"), dengan kata-katamu
  sendiri, dan beri penanggung jawab.

---

## Tiket tambahan

**Tiket #4822: Halaman pesanan di dasbor admin terasa kurang gesit**
Dari: Tim Operasional. Prioritas: Sedang. Komponen: `GET /api/admin/orders`.
Keluhan: staf operasional merasa daftar pesanan tidak segesit halaman lain, terutama saat
berpindah halaman. Jumlah pesanan terus bertambah dan mereka bertanya apakah halaman ini akan
makin lambat.
Yang kami tahu: tidak ada error. Belum dicoba membandingkan halaman awal dengan halaman jauh.
Yang diminta: pahami apa yang sebenarnya terjadi dan bagaimana biayanya berubah saat data
bertambah. Kumpulkan bukti dari lebih dari satu sumber, tulis hipotesis, catat yang belum
diketahui. Jangan memperbaiki apa pun.

**Tiket #4823: Login terasa lebih lama dari perkiraan**
Dari: Tim Produk. Prioritas: Sedang. Komponen: `POST /api/auth/login`.
Keluhan: pengguna merasa proses masuk butuh waktu, terutama saat banyak orang masuk bersamaan
(mis. saat promosi). Tim produk bertanya waktu itu dihabiskan di mana.
Yang kami tahu: tidak ada error. Catatan lama menyebut login lambat "wajar karena pengamanan",
tetapi belum ada yang membuktikan seberapa besar bagian itu.
Yang diminta: pecah ke mana waktu satu login pergi, lengkap dengan bukti dan hipotesis.
Jangan memperbaiki apa pun.

**Tiket #4824: Penjual besar mengeluh halaman invoice**
Dari: Tim Dukungan Penjual. Prioritas: Sedang. Komponen: `GET /api/sellers/{id}/invoices`.
Keluhan: beberapa penjual dengan riwayat transaksi banyak bilang halaman invoice mereka
kadang lama, sedangkan penjual kecil tidak mengeluh. Laporan rata-rata terlihat baik.
Yang kami tahu: tidak ada error. Belum jelas apakah semua request lambat atau hanya sebagian.
Yang diminta: tunjukkan perbedaan antara penjual besar dan kecil dengan bukti, dan jelaskan
mengapa rata-rata bisa menyesatkan. Jangan memperbaiki apa pun.

**Tiket #4825: Beban database tinggi saat katalog dibuka**
Dari: Tim Infrastruktur. Prioritas: Sedang. Komponen: `GET /api/products`.
Keluhan: halaman katalog terasa cepat bagi pengguna, tetapi tim infrastruktur melihat
database sibuk setiap kali katalog banyak dibuka. Mereka khawatir ini bermasalah saat
pengunjung bertambah.
Yang kami tahu: tidak ada error, dan latensi tampak baik.
Yang diminta: pahami apa yang terjadi di database tiap satu permintaan katalog, dan seberapa
besar dampaknya saat laju naik. Jangan memperbaiki apa pun.

**Tiket #4826: Pencarian produk makin berat dibanding katalog**
Dari: Tim Produk. Prioritas: Sedang. Komponen: `GET /api/products/search`.
Keluhan: pencarian terasa lebih berat daripada menjelajah katalog. Tim produk ingin tahu apa
yang terjadi bila jumlah produk naik sepuluh kali lipat.
Yang kami tahu: tidak ada error. Belum ada pengujian pada ukuran data yang berbeda.
Yang diminta: jelaskan di mana waktu pencarian dihabiskan, dan perkirakan dengan alasan
bagaimana perilakunya saat data membesar. Tandai mana pengamatan dan mana dugaan.
Jangan memperbaiki apa pun.

**Tiket #4827: Checkout, apakah layanan luar yang menahan?**
Dari: Tim Pembayaran. Prioritas: Tinggi. Komponen: `POST /api/checkout`.
Keluhan: checkout terasa butuh waktu lebih lama daripada halaman lain. Tim pembayaran
bertanya apakah penyebabnya ada di aplikasi kita atau di layanan pihak ketiga, dan apa
dampaknya bila layanan itu melambat.
Yang kami tahu: tidak ada error pada beban sekarang. Belum pernah diuji saat layanan luar
melambat.
Yang diminta: pisahkan waktu yang dihabiskan di aplikasi, di database, dan di layanan luar;
jelaskan apa yang bisa terjadi bila layanan luar melambat. Jangan memperbaiki apa pun, dan
jangan mengubah pengaturan layanan tiruan.

**Tiket #4828: Dasbor admin terasa membeku saat meminta daftar pesanan yang sangat besar**
Dari: Tim Operasional. Prioritas: Sedang. Komponen: `GET /api/admin/orders`.
Keluhan: seorang staf mencoba mengambil daftar pesanan dalam satu halaman yang sangat besar
(untuk diekspor) dan dasbor terasa membeku sebentar. Dukungan bertanya apakah hal seperti itu
bisa mengganggu pengguna lain.
Yang kami tahu: permintaan itu akhirnya berhasil. Belum diketahui apakah ada batas yang berlaku.
Yang diminta: ukur bagaimana waktu respons dan jumlah data yang dikirim berubah saat ukuran
halaman membesar, dan jelaskan risikonya bagi sistem. Gunakan lebih dari satu sumber bukti.
Jangan memperbaiki apa pun.

**Tiket #4829: Keranjang berisi data yang tidak masuk akal**
Dari: Tim Dukungan Pelanggan. Prioritas: Sedang. Komponen: `POST /api/cart/items`.
Keluhan: dukungan menemukan beberapa isi keranjang yang tidak masuk akal. Belum jelas seberapa
luas dan bagaimana bisa terjadi.
Yang kami tahu: tidak ada error di sisi pelanggan.
Yang diminta: tunjukkan bagaimana data seperti itu bisa masuk, apa dampaknya pada langkah
berikutnya (misalnya checkout), dan buktinya. Kerjakan hanya di database lab milikmu sendiri di
laptopmu, jangan di lingkungan bersama atau milik orang lain. Jangan memperbaiki apa pun.

**Tiket #4830: Beberapa input yang salah berujung error server**
Dari: Tim Pemantauan. Prioritas: Sedang. Komponen: seluruh API.
Keluhan: pemantauan mencatat sejumlah respons 5xx yang ternyata berasal dari input pengguna
yang salah, bukan kegagalan sistem. Ini mengacaukan alarm.
Yang kami tahu: sebagian input yang salah dijawab 4xx dengan pesan jelas, sebagian lain tidak.
Yang diminta: petakan secara sistematis input salah mana (parameter, isi permintaan, tipe)
yang menghasilkan 4xx dan mana yang menghasilkan 5xx, dengan bukti, dan jelaskan mengapa itu
mengganggu pemantauan. Jangan memperbaiki apa pun.

**Tiket #4831: Mana yang harus diperbaiki lebih dulu?** (tiket integrasi)
Dari: Pimpinan Produk. Prioritas: Tinggi. Komponen: tiket lain yang diambil kelompokmu.
Keluhan: beberapa tiket masuk bersamaan dan tim hanya bisa memperbaiki dua dalam sprint ini.
Yang diminta: setelah rekan menyerahkan temuan, susun ringkasan satu halaman: apa temuan tiap
tiket, mana yang mempengaruhi pengguna paling banyak, mana yang risikonya paling besar bila
dibiarkan, dan dua yang kamu sarankan beserta alasannya. Tandai mana fakta dan mana
pertimbangan. Jangan memperbaiki apa pun.
