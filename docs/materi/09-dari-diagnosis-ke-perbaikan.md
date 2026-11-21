# Bagian 9: Dari diagnosis ke perbaikan

*Memilah sinyal, membaca batasan, memilih perbaikan, dan menutup tiket*

> **Tujuan.** Memakai tiga keterampilan baru di tiket 20–26 Nov 2026: memilah sinyal di tiket, mencatat batasan dari paket konteks, dan memilih satu perbaikan dengan alasan yang bisa diperiksa.  
> **Prasyarat.** Bagian 6.1 (diagnosa), Bagian 8 (lima langkah satu tiket), dan Lab 0 sudah kamu jalankan.  
> **Setelah bagian ini kamu bisa:**
> - menulis dugaan sebelum menyentuh alat, lalu membandingkannya dengan temuan
> - memilah isi tiket ke enam kategori sinyal
> - mencatat batasan beserta sumbernya, dan membedakan batasan keras dari pendapat
> - membandingkan dua alternatif perbaikan dengan kriteria yang sama
> - menulis komentar penutupan yang bisa diperiksa orang lain
>
> Contoh di bagian ini memakai tiket nyata **#4821** (pemanasan yang sudah kamu diagnosis). Angkanya diukur sungguhan pada 20 dan 21 November 2026 di laptop i5-5250U (2 core), RAM 8 GB, tanpa Docker, data skala S, dan semuanya bisa kamu periksa di repo contoh [PasarKitaGood](https://github.com/joelianmin/PasarKitaGood). Angkamu akan berbeda; polanya yang penting. Kasusnya berbeda dari tiket 20–26 Nov 2026.
> Pengerjaan #4821 (diagnosis) dan tiket contoh **PK-200** (perbaikannya) dari awal sampai ditutup, lengkap dengan apa yang dilakukan dan dipikirkan pemilik tiket dan dua peninjau: [`../CONTOH-PERBAIKAN-TIKET.md`](../CONTOH-PERBAIKAN-TIKET.md).

---

## 9.1 Apa yang berubah dari latihan membedah tiket

| | Latihan membedah tiket | Tiket 20–26 Nov 2026 |
| --- | --- | --- |
| Hasil | Laporan diagnosis | Diagnosis **dan perbaikan** dalam satu PR |
| Aturan | Jangan memperbaiki apa pun | Perbaiki, dan buktikan hasilnya |
| Konteks | Tiket saja | Tiket **dan paket konteks** (notulen, thread, kebijakan) |
| Pendapat di tiket | Bukan masalah | Sebagian keliru, dan harus kamu pilah |
| Peninjau | Pengajar | Dua persetujuan: rekan kelompok, atau teman dari kelompok lain bila kelompokmu kecil |

Pegangan umum tidak berubah: **ukur dulu, ubah satu hal, ukur lagi.** Yang bertambah adalah pertanyaan sebelum mengubah: *apa yang boleh dan tidak boleh diubah, dan mengapa pilihanku lebih baik daripada alternatifnya?*

AI boleh membantu. Yang diperiksa adalah alasanmu: harus merujuk bukti dari mesinmu dan berkas konteks yang benar-benar kamu baca, dan kamu harus bisa menjelaskannya sendiri (lihat `docs/RUBRIK.md`).

---

## 9.2 Prediksi dulu

Sebelum membuka Grafana atau k6, tulis di jurnal diagnosis (`docs/templat/JURNAL-DIAGNOSIS.md`):

- dugaan penyebab, satu atau dua, ditandai "belum terbukti"
- apa yang akan kamu ukur untuk membuktikan atau menggugurkannya
- hasil yang kamu harapkan dari tiap ukuran

Prediksi **tidak dinilai benar atau salah**. Gunanya membuat kamu sadar saat dugaanmu meleset, dan itu informasi paling berharga di awal. Di akhir, tambahkan satu baris: *dugaan awal benar, sebagian, atau salah, dan apa yang mengubahnya.*

---

## 9.3 Memilah sinyal di tiket

Setiap kalimat di tiket adalah **sinyal**, tetapi sinyal tidak sama nilainya. Beri tiap sinyal satu dari enam kategori:

| Kategori | Ciri | Cara memeriksa |
| --- | --- | --- |
| **Gejala utama** | Terukur, bisa direproduksi, dan berhubungan langsung dengan keluhan | Reproduksi dengan k6 atau curl |
| **Akibat** | Muncul karena gejala utama (antrean keluhan, CPU naik) | Apakah hilang bila gejala utama hilang? |
| **Kebetulan waktu** | Terjadi bersamaan dengan gejala, tetapi tidak menyebabkannya | Bandingkan jam kejadian dengan jam perubahan, dan lihat apakah perubahannya menyentuh jalur yang lambat |
| **Noise** | Selalu ada, tidak berubah saat gejala muncul atau hilang | Apakah muncul juga saat sistem sehat? |
| **Dugaan keliru** | Penjelasan yang terdengar masuk akal tetapi bertentangan dengan bukti | Cari satu pengukuran yang menggugurkannya |
| **Solusi titipan** | Usul perbaikan yang datang sebelum penyebab diketahui | Apa yang dikatakannya tentang penyebab? Apakah ada buktinya? |

Kesalahan umum: menganggap "yang paling dulu disebut" atau "yang disebut orang paling senior" sebagai penyebab. Urutan di tiket tidak mengandung informasi tentang penyebab.

### Contoh: tiket #4821

Teks tiket (dari `labs/latihan/TIKET.md`, diringkas): halaman profil toko terasa sangat lama, sampai beberapa detik; kemarin masih cepat; keluhan paling banyak dari toko yang produknya banyak; halaman lain tidak dikeluhkan; tidak ada rilis kode hari ini, tetapi belum bisa dipastikan apakah ada perubahan konfigurasi.

Hasil ukur (kondisi latihan menyala, `RATE=1`, 3 menit): p50 613 ms, p95 1,67 s. Saat kondisi mati (2 req/detik): p50 11,4 ms, p95 18,8 ms. Database: dua query per request, masing-masing di bawah 1 ms. CPU proses aplikasi (`ps`, per core) 62 sampai 93% pada 1 req/detik dan sampai 339,5% pada 2 req/detik. Log memuat `WARN Property 'pasarkita.legacy.timeout' is deprecated` tiap 30 detik.

| Sinyal | Kategori | Alasan |
| --- | --- | --- |
| Profil lambat, sampai beberapa detik | Gejala utama | Terukur dan bisa direproduksi dengan `labs/latihan/profile.k6.js` |
| Toko dengan produk banyak paling lambat | Gejala utama (selektif) | Penjual 1 (60 produk tampil) median 1.403 ms, penjual 2 dan 77 (25 produk) 665 dan 592 ms: kira-kira sebanding jumlah produk |
| CPU aplikasi sampai 339,5% (per core) | Akibat | Muncul karena pekerjaan per produk; turun ke paling tinggi 9,3% setelah perbaikan (PK-200) |
| "Kemarin masih cepat", tidak ada rilis kode | **Bukan** kebetulan waktu | Tiket sendiri menyebut kemungkinan perubahan konfigurasi, dan flag `seller.dynamic-thumbnail` memang menyala |
| "Pasti database lambat" (dugaan yang sering muncul di laporan latihan) | Dugaan keliru | Dua query, masing-masing di bawah 1 ms; hanya satu koneksi aktif saat beban |
| WARN `legacy.timeout` tiap 30 detik | Noise | Muncul juga saat profil cepat |

#4821 tidak memuat solusi titipan. Tiket 20–26 Nov 2026 hampir selalu memuatnya, biasanya dari orang yang terdengar berwenang.

Perhatikan: mengabaikan sinyal bukan berarti menghapusnya. Kategori dan alasannya ditulis, agar peninjau bisa tidak setuju dengan alasan yang terlihat.

---

## 9.4 Membaca paket konteks

Paket konteks adalah dokumen bisnis yang menyertai tiket. Isinya bercampur: ada yang menjadi **batasan keras**, ada yang **konteks**, ada yang **pendapat**, dan ada yang **tidak relevan**.

| Jenis | Ciri | Contoh |
| --- | --- | --- |
| **Batasan keras** | Kebijakan, kontrak, SLA, keputusan resmi; melanggarnya berarti perbaikanmu ditolak | "Jangan mengubah pengaturan layanan tiruan" (#4827, `labs/latihan/TIKET.md`) |
| **Konteks** | Menjelaskan situasi tanpa melarang apa pun | "Keluhan paling banyak datang dari toko yang produknya banyak" (#4821) |
| **Pendapat** | Usulan atau dugaan seseorang, bisa benar bisa tidak | "Pasti database lambat" |
| **Tidak relevan** | Benar, tetapi tidak menyentuh tiketmu | WARN `legacy.timeout` di log |

Cara membacanya:

1. **Catat batasan di daftar asumsi** dengan kolom: batasan, sumber (berkas dan bagiannya), jenis, dan dampaknya pada pilihanmu.
2. **Periksa tanggal dan versi.** Apakah dokumen itu masih berlaku sekarang? Apakah ada dokumen lebih baru yang menggantikannya?
3. **Periksa siapa yang berwenang.** Keputusan resmi mengikat. Usulan di thread tidak mengikat.
4. **Perhatikan apa yang tidak disebut.** Batasan yang hanya muncul di satu kalimat di tengah dokumen tetap batasan.
5. **Jangan mengambil batasan dari ingatan.** Tulis sumbernya, agar peninjau bisa membukanya.

### Lanjutan contoh #4821

#4821 tidak punya paket konteks, jadi daftar asumsinya diambil dari teks tiket, petunjuk lab, dan skema database. Tiket 20–26 Nov 2026 punya paket konteks, dan sumber utamamu ada di sana. Tiket perbaikannya, **PK-200**, juga punya paket konteks (`labs/pk-200/konteks/`); daftar asumsi yang diisi darinya ada di jurnal PK-200 di repo [PasarKitaGood](https://github.com/joelianmin/PasarKitaGood/blob/main/labs/pk-200/JURNAL.md).

| Batasan atau asumsi | Sumber | Jenis | Dampak |
| --- | --- | --- | --- |
| Waktu di tiket dalam WIB, Grafana memakai UTC | Catatan #4821 di `labs/latihan/TIKET.md`, langkah 4 `labs/latihan/LAB.md` | Keras (cara membaca bukti) | Jam dicocokkan sebelum menyimpulkan |
| Tidak ada rilis kode; konfigurasi mungkin berubah | Teks #4821 | Konteks | Mengarahkan ke tabel `feature_flag` |
| `feature_flag` hanya menyimpan `name` dan `enabled` | `V1__skema_awal.sql` | Konteks (keterbatasan) | Jam perubahan flag tidak bisa dibuktikan dari database |
| Respons profil tidak boleh berubah selama flag menyala | Asumsi pemilik tiket, ditulis di "Selesai bila" | Asumsi | Mematikan flag ditolak karena mengubah `thumbnail` di respons |

Asumsi tanpa dokumen sah, asal ditulis sebagai asumsi, beserta alasannya, supaya peninjau bisa membantahnya.

---

## 9.5 Membandingkan dua alternatif

Tiket 20–26 Nov 2026 meminta **minimal dua alternatif**. Tujuannya bukan memilih yang paling canggih, tetapi menunjukkan bahwa pilihanmu lahir dari pertimbangan.

Kriteria yang sama untuk setiap alternatif:

1. **Akar masalah.** Apakah ini memperbaiki penyebab yang kamu buktikan, atau hanya menutupinya?
2. **Batasan.** Apakah semua batasan keras di 9.4 tetap terpenuhi?
3. **Biaya dan risiko.** Perubahan skema, migrasi, kompleksitas baru, risiko data salah.
4. **Bisa dibatalkan?** Seberapa mudah mundur bila ternyata keliru.
5. **Efek ke pihak lain.** Pengguna lain, tim lain, kontrak yang sudah ada.

### Lanjutan contoh #4821

Penyebab yang terbukti: dengan flag menyala, `ThumbnailService` membuat gambar 1600 x 1600 lalu mengecilkannya untuk **setiap produk** (sampai 60 per request), hanya untuk membaca satu piksel.

| Alternatif | Akar masalah | Batasan (respons tidak berubah) | Biaya dan risiko | Bisa dibatalkan |
| --- | --- | --- | --- | --- |
| **A.** Matikan flag | Menghindari, tidak memperbaiki | **Melanggar**: `thumbnail` kembali ke `/images/<id>.jpg` | Keputusan produk, bukan keputusan teknis | Mudah |
| **B.** Simpan hasil per produk | Sebagian: tiap produk tetap mahal sekali | Terpenuhi | Request pertama tetap lambat; memori bertambah sebanding jumlah produk | Mudah |
| **C.** Buat gambar langsung 200 x 200 | Ya: pekerjaan mahal dihapus | Terpenuhi, **setelah diuji**: URL sama untuk 255 dari 255 kemungkinan warna | Tanpa state; 60 thumbnail 16 ms lawan 934 ms | Mudah |
| **D.** Buat 60 gambar secara paralel | Tidak: total CPU sama | Terpenuhi | CPU sudah sampai 339,5% (per core); paralel memperburuk saat beban | Mudah |

Pilihan: **C**. Sebelum diuji, pemilik tiket menduga C pasti mengubah URL dan hampir menolaknya. Dugaan itu salah, dan baru ketahuan karena diuji. Menolak atau menerima alternatif dengan alasan yang bisa diperiksa sama bernilainya dengan memilih satu.

> Alternatif "tidak melakukan apa-apa" juga sah bila buktimu menunjukkan tidak ada masalah. Tulis alasannya.

---

## 9.6 Satu perubahan, satu bukti

- **Satu perubahan per commit dan per ukuran.** Kalau tiga hal diubah sekaligus, kamu tidak tahu mana yang bekerja.
- **Ukur dengan cara yang sama** sebelum dan sesudah: skrip k6 yang sama, data yang sama, laju yang sama. Bila mengubah salah satunya, catat.
- **Commit dulu, baru `task evidence`.** Bukti dari pohon kerja yang belum di-commit tidak sah (Bagian 7).
- **Jangan menambah dari yang diminta.** Perbaikan di luar tiket menjadi **tindak lanjut** di komentar penutupan, bukan bagian PR.
- **Hal yang sengaja dibiarkan** ditulis, beserta alasannya. Itu bagian dari mutu kerja.

---

## 9.7 Meninjau PR rekan

Tiap PR mendapat dua persetujuan sebelum ditutup. Pemilik tiket tidak menyetujui PR-nya sendiri. Kelompok tiga orang atau lebih memakai dua anggota lain, kelompok dua orang memakai rekan dan satu teman dari kelompok lain, dan kelompok satu orang memakai dua teman dari kelompok lain (aturan lengkap, termasuk cara mengundang peninjau, di `labs/MINGGU-20-26Nov2026.md`). Peninjau memeriksa, bukan sekadar menyetujui:

- [ ] Akar masalah dibuktikan dengan angka dari lebih dari satu sumber
- [ ] Setiap batasan di daftar asumsi punya sumber yang bisa dibuka, dan perbaikan memenuhinya
- [ ] Minimal dua alternatif ditulis, dengan alasan memilih atau menolak
- [ ] Angka sebelum dan sesudah memakai cara ukur yang sama
- [ ] Perubahan hanya satu hal, dan tidak ada yang diubah di luar tiket
- [ ] Berkas bukti dari `task evidence` dilampirkan, bukan di-commit
- [ ] Hal yang sengaja dibiarkan tercatat sebagai tindak lanjut

Komentar tinjauan yang berguna menunjuk baris atau berkas dan menyebut apa yang kurang. "LGTM" tanpa alasan tidak menolong siapa pun. Peninjau yang menemukan masalah dan menyebutnya dengan jelas menunjukkan keterampilan yang sama pentingnya dengan penulis PR.

---

## 9.8 Komentar penutupan

Komentar penutupan memakai templat di `labs/MINGGU-20-26Nov2026.md`. Beda yang lemah dan yang kuat:

| Bagian | Lemah | Kuat |
| --- | --- | --- |
| Akar masalah | "Gambarnya berat" | "Flag `seller.dynamic-thumbnail` menyala; untuk tiap produk (sampai 60) aplikasi membuat gambar 1600 x 1600 hanya untuk membaca satu piksel. CPU aplikasi sampai 339,5% per core, database dua query di bawah 1 ms (p50 613 ms pada 1 req/detik)" |
| Sinyal | Daftar tanpa alasan | Tabel kategori dengan alasan dan bukti per baris |
| Batasan | "Sudah dipenuhi" | Tabel: batasan, berkas sumber, cara dipenuhi |
| Alternatif | "Cache juga bisa" | Dua atau lebih alternatif, dengan alasan menolak yang merujuk batasan |
| Hasil | "Lebih cepat" | Sebelum dan sesudah: p95, p99, error, query per request, dengan skrip yang sama |
| Tindak lanjut | Kosong | Hal yang sengaja dibiarkan dan alasannya |

---

## 9.9 Jika kamu macet

- **Tidak tahu harus memulai dari mana:** kerjakan 9.2 (prediksi). Menulis dugaan hampir selalu membuka langkah pertama.
- **Dua dokumen konteks tampak bertentangan:** catat keduanya di daftar asumsi, lihat tanggal dan siapa yang berwenang, lalu tulis pilihanmu beserta alasannya.
- **Perbaikanmu melanggar batasan yang baru kamu temukan:** itu temuan, bukan kegagalan. Ubah rencana dan catat di Issue rencana (`docs/PANDUAN-RENCANA.md`): apa yang berubah, pemicunya, dan apa yang dikorbankan.
- **Melewati dua kali perkiraan waktu:** tanya rekan atau pengajar sambil menyertakan apa yang sudah kamu coba, seperti di Bagian 8.6.

> Cek diri 1: seorang senior berkata "pasti karena X, cukup ubah Y", dan satu dokumen konteks melarang Y. Sinyal apa pernyataannya, dan apa yang kamu tulis di komentar penutupan?  
> Cek diri 2: perbaikanmu membuat p95 turun 80%, tetapi kamu tidak menulis satu pun batasan beserta sumbernya. Apa yang belum bisa dibuktikan oleh peninjau?  
> Cek diri 3: kamu menolak sebuah alternatif karena "terlalu rumit". Apa yang harus kamu tambahkan agar penolakan itu bisa diperiksa?

---

*Sebelumnya: [Bagian 8](08-contoh-pengerjaan-tiket.md) · [Daftar isi](../MATERI-DASAR.md) · Berikutnya: [Referensi](referensi.md)*
