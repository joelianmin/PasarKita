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
> Seluruh contoh di bagian ini **rekaan**: tiket #4900 dan dokumennya tidak ada di PasarKita dan tidak sama dengan tiket 20–26 Nov 2026.
> Contoh nyata satu tiket dari awal sampai ditutup (tiket #4821, diukur sungguhan), lengkap dengan apa yang dilakukan dan dipikirkan pemilik tiket dan dua peninjau: [`../CONTOH-PERBAIKAN-TIKET.md`](../CONTOH-PERBAIKAN-TIKET.md).

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

### Contoh (rekaan): tiket #4900

> **Dewi, Dukungan Penjual (Senin, 10.00):** Penjual besar bilang kartu "Total penjualan" di dasbor mereka butuh dua sampai tiga detik. Penjual kecil tidak mengeluh.
>
> **Pak Hadi, Senior Developer (10.20):** Pasti karena RAM server kurang. Naikkan saja ke 32 GB, atau pasang read replica.
>
> **Rina, Product (10.35):** Mulai lambat sejak rilis hari Jumat.

Hasil ukur yang kamu kumpulkan: p95 kartu 2,4 detik untuk penjual terbesar dan 60 ms untuk penjual kecil; CPU database 18%; memori bebas 60%; log memuat `WARN header X-Legacy deprecated` setiap menit.

| Sinyal | Kategori | Alasan |
| --- | --- | --- |
| Kartu lambat untuk penjual besar, tidak untuk penjual kecil | Gejala utama | Terukur, bisa direproduksi, selektif |
| Antrean keluhan di Dukungan | Akibat | Ada karena gejala di atas |
| "Mulai lambat sejak rilis Jumat" | Kebetulan waktu | Catatan rilis hanya mengubah warna tombol dan teks bantuan, tidak menyentuh jalur ini |
| "RAM server kurang" | Dugaan keliru | Memori bebas 60% dan CPU database 18% |
| "Pasang read replica" | Solusi titipan | Tidak ada bukti penyebab; bila masalahnya di cara menghitung, replica menyalin masalah itu |
| WARN header deprecated tiap menit | Noise | Muncul juga saat sistem sehat |

Perhatikan: mengabaikan sinyal bukan berarti menghapusnya. Kategori dan alasannya ditulis, agar peninjau bisa tidak setuju dengan alasan yang terlihat.

---

## 9.4 Membaca paket konteks

Paket konteks adalah dokumen bisnis yang menyertai tiket. Isinya bercampur: ada yang menjadi **batasan keras**, ada yang **konteks**, ada yang **pendapat**, dan ada yang **tidak relevan**.

| Jenis | Ciri | Contoh |
| --- | --- | --- |
| **Batasan keras** | Kebijakan, kontrak, SLA, keputusan resmi; melanggarnya berarti perbaikanmu ditolak | "Angka dasbor harus sama persis dengan laporan keuangan" |
| **Konteks** | Menjelaskan situasi tanpa melarang apa pun | "Invoice penjual terbesar naik tiga kali lipat dalam tiga bulan" |
| **Pendapat** | Usulan seseorang, bisa benar bisa tidak | "Naikkan RAM" |
| **Tidak relevan** | Benar, tetapi tidak menyentuh tiketmu | Jadwal on-call akhir tahun |

Cara membacanya:

1. **Catat batasan di daftar asumsi** dengan kolom: batasan, sumber (berkas dan bagiannya), jenis, dan dampaknya pada pilihanmu.
2. **Periksa tanggal dan versi.** Apakah dokumen itu masih berlaku sekarang? Apakah ada dokumen lebih baru yang menggantikannya?
3. **Periksa siapa yang berwenang.** Keputusan resmi mengikat. Usulan di thread tidak mengikat.
4. **Perhatikan apa yang tidak disebut.** Batasan yang hanya muncul di satu kalimat di tengah dokumen tetap batasan.
5. **Jangan mengambil batasan dari ingatan.** Tulis sumbernya, agar peninjau bisa membukanya.

### Lanjutan contoh #4900

| Batasan | Sumber | Jenis | Dampak |
| --- | --- | --- | --- |
| Angka sama persis dengan laporan keuangan | `kebijakan-keuangan.md` v1.2 | Keras | Perbaikan harus bisa dibuktikan menghasilkan angka yang sama |
| Tampilan tidak boleh tertinggal lebih dari 1 menit | `kebijakan-keuangan.md` v1.2 | Keras | Cache beberapa menit tidak boleh |
| Invoice penjual terbesar naik 3 kali dalam 3 bulan | `tren-penjual.md` | Konteks | Masalah akan memburuk bila dibiarkan |
| "Naikkan RAM" | Thread Pak Hadi | Pendapat | Tidak mengikat; sudah digugurkan oleh ukuran memori |

---

## 9.5 Membandingkan dua alternatif

Tiket 20–26 Nov 2026 meminta **minimal dua alternatif**. Tujuannya bukan memilih yang paling canggih, tetapi menunjukkan bahwa pilihanmu lahir dari pertimbangan.

Kriteria yang sama untuk setiap alternatif:

1. **Akar masalah.** Apakah ini memperbaiki penyebab yang kamu buktikan, atau hanya menutupinya?
2. **Batasan.** Apakah semua batasan keras di 9.4 tetap terpenuhi?
3. **Biaya dan risiko.** Perubahan skema, migrasi, kompleksitas baru, risiko data salah.
4. **Bisa dibatalkan?** Seberapa mudah mundur bila ternyata keliru.
5. **Efek ke pihak lain.** Pengguna lain, tim lain, kontrak yang sudah ada.

### Lanjutan contoh #4900

| Alternatif | Akar masalah | Batasan | Biaya dan risiko | Bisa dibatalkan |
| --- | --- | --- | --- | --- |
| **A.** Tabel ringkasan harian, diperbarui saat invoice terbit | Ya: kartu tidak lagi menjumlahkan ratusan ribu baris tiap dibuka | Terpenuhi bila ringkasan selalu cocok dengan invoice (perlu dibuktikan) | Migrasi dan menjaga konsistensi | Sedang |
| **B.** Cache 10 menit di aplikasi | Menutupi | **Melanggar** batas 1 menit | Angka basi | Mudah |
| **C.** Read replica | Tidak | Replika bisa tertinggal, berisiko melanggar batas 1 menit | Biaya infrastruktur, sulit dibatalkan | Sulit |

Pilihan: **A**, dengan satu syarat yang harus dibuktikan di PR (angka kartu sama dengan jumlah invoice untuk beberapa penjual). B dan C ditolak dengan alasan tertulis. Menolak alternatif dengan alasan yang bisa diperiksa sama bernilainya dengan memilih satu.

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
| Akar masalah | "Query lambat" | "Kartu menjumlahkan semua invoice penjual tiap dibuka; penjual terbesar punya ratusan ribu baris (p95 2,4 detik, 60 ms untuk penjual kecil)" |
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
