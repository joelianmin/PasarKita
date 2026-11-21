# Contoh ujung ke ujung: #4821 dan PK-200 di repo PasarKitaGood

Repo contoh **[PasarKitaGood](https://github.com/joelianmin/PasarKitaGood)** dibuat dari templat ini dan dikerjakan seperti
repo kelompok: rencana sesi, diagnosis tiket **#4821**, mengambil pembaruan templat, lalu perbaikan tiket contoh **PK-200**
(`labs/pk-200`, lanjutan #4821), sampai semua Issue tertutup.

> **Yang nyata:** tiket, paket konteks, kode, perintah, Issue, PR, commit, dan **semua angka**. Diukur pada 20 dan 21 November
> 2026 di laptop Intel Core i5-5250U (2 core, 4 thread), RAM 8 GB, macOS 12, tanpa Docker, data skala S.
>
> **Yang diperankan:** satu akun memerankan **Ayu** (pemilik tiket), **Bimo** dan **Citra** (peninjau). Isi kepala mereka di
> bawah ditulis sebagai penjelasan langkah; setiap angka yang disebut ada di Issue, PR, atau jurnal yang ditautkan.
> Angkamu akan berbeda karena mesinmu berbeda; polanya yang penting.

## Peta

| # | Langkah | Tempat |
| --- | --- | --- |
| 1 | Rencana sesi, Cek, Perubahan, Refleksi | [Issue #1](https://github.com/joelianmin/PasarKitaGood/issues/1) |
| 2 | Diagnosis #4821 | [Issue #2](https://github.com/joelianmin/PasarKitaGood/issues/2), [PR #3](https://github.com/joelianmin/PasarKitaGood/pull/3), [`LAPORAN-4821.md`](https://github.com/joelianmin/PasarKitaGood/blob/main/labs/latihan/LAPORAN-4821.md) |
| 3 | Mengambil pembaruan templat | [PR #4](https://github.com/joelianmin/PasarKitaGood/pull/4) |
| 4 | Perbaikan PK-200 | [Issue #5](https://github.com/joelianmin/PasarKitaGood/issues/5), [PR #6](https://github.com/joelianmin/PasarKitaGood/pull/6), [`JURNAL.md`](https://github.com/joelianmin/PasarKitaGood/blob/main/labs/pk-200/JURNAL.md) |

Urutan waktu lengkap semua pemangku kepentingan (anggota kelompok, pelapor, Product, CDN, Platform), dari dokumen
konteks sampai sesudah penutupan: [`KRONOLOGI.md`](https://github.com/joelianmin/PasarKitaGood/blob/main/KRONOLOGI.md).
Tindakan dan angkanya nyata; pikiran tokoh tiket dan paket konteks adalah fiksi yang masuk akal dan ditandai *(fiktif)*.

---

## 1. Rencana sesi (Issue #1)

**Dilakukan.** Ayu menulis rencana sebelum mulai: tujuan yang bisa diperiksa, tiga tiket berukuran, tiga risiko dengan tanda
dan rencana B, jam titik cek.

> **Ayu berpikir:** *"Risiko yang paling mungkin bukan kodenya, tapi laptopku jenuh saat kondisi latihan menyala, dan
> pembaruan templat untuk PK-200 belum tersedia. Keduanya aku tulis dengan tandanya, supaya aku tahu kapan rencana B berlaku."*

Keduanya benar-benar terjadi. Sesi terbelah semalam karena pembaruan templat baru tersedia keesokan pagi, dan itu dicatat
sebagai komentar **Perubahan** (berubah, pemicu, dikorbankan), bukan disembunyikan.

## 2. Diagnosis #4821 (Issue #2, PR #3)

**Dilakukan.** Issue ditulis ulang dengan kata-kata sendiri, PR draf dibuka sebelum mengukur, lalu:

| Ukuran | Hasil |
| --- | --- |
| Kondisi mati, 2 req/detik | p50 11,4 ms, p95 18,8 ms, 0 gagal |
| Kondisi menyala, 2 req/detik | **12,5% gagal**, p95 40 s: laptop jenuh |
| Kondisi menyala, 1 req/detik | p50 613 ms, p95 1,67 s, 0 gagal |
| Prometheus, panel p95 (UTC) | 15 sampai 20 ms sampai 12.19 UTC, lalu 1,7 sampai 3,5 s; katalog tidak berubah |
| `pg_stat_statements` | 2 query per request, di bawah 1 ms |

> **Ayu berpikir:** *"Dugaan pertamaku 'query produk lambat'. Dua query di bawah 1 ms menggugurkannya. Yang naik justru CPU
> aplikasi. Angka 2 req/detik tidak aku pakai sebagai ukuran gejala, karena itu mengukur antrean di laptopku, bukan halaman
> profil."*
>
> **Bimo berpikir (tinjauan):** *"Prometheus bilang CPU 35%, `ps` bilang 339%. Itu bukan kontradiksi, tapi pembaca akan
> bingung. Satuannya harus ditulis."* Ia meminta perubahan; Ayu memperbaikinya di commit terpisah.
>
> **Citra berpikir (tinjauan):** *"Bimo sudah memeriksa angka. Aku periksa jam: 19.18.58 WIB = 12.18.58 UTC, dan titik 12.20
> di panel mewakili jendela 12.19 sampai 12.20. Cocok."*

Latihan ini tidak mengubah kode, sesuai aturannya.

## 3. Mengambil pembaruan templat (PR #4)

`labs/pk-200` belum ada di repo kelompok. Ayu mengikuti `docs/PANDUAN-PEMBARUAN.md` lewat cabang dan PR; laporan #4821 dan
`group.env` tidak tersentuh.

## 4. Perbaikan PK-200 (Issue #5, PR #6, `labs/pk-200/JURNAL.md`)

### Prediksi lebih dulu

Ayu menulis tiga prediksi **sebelum** mengukur: (1) waktunya habis di `ThumbnailService.thumbnailFor`; (2) mematikan flag
memulihkan kecepatan tapi mengubah respons; (3) membuat gambar langsung kecil akan mengubah URL thumbnail.

### Paket konteks dan triage

| Dari paket konteks | Jenis | Dampaknya |
| --- | --- | --- |
| Flag tetap menyala (keputusan produk) | Keras | Usul "matikan flag" bukan perbaikan |
| URL thumbnail tidak boleh berubah (CDN) | Keras | Perbaikan harus menghasilkan URL yang sama persis |
| Profil menampilkan 200 x 200; 1600 x 1600 hanya untuk zoom | Konteks | Profil tidak butuh gambar besar |
| Tidak ada anggaran server tambahan | Keras | Usul "tambah server" tidak tersedia |

> **Ayu berpikir:** *"Dua orang di tiket memberi solusi sebelum ada yang menyebut penyebab. Dua-duanya solusi titipan, dan
> dua-duanya ditolak oleh dokumen konteks, bukan oleh pendapatku."*

### Menguji prediksi

| Prediksi | Uji | Hasil |
| --- | --- | --- |
| 1. Waktu di `thumbnailFor` | 60 panggilan di luar aplikasi | 934 ms: **benar** |
| 2. Matikan flag mengubah respons | JSON flag mati vs menyala, 3 penjual | `thumbnail` berbeda di 60, 25, 25 produk: **benar** |
| 3. Gambar langsung kecil mengubah URL | `labs/pk-200/BandingThumbnail.java`, semua 255 warna | 0 dari 255 berbeda, 16 ms: **salah** |

> **Ayu berpikir:** *"Kalau aku tidak menguji prediksi 3, aku akan menolak perbaikan terbaik dan memilih cache, yang hanya
> menutupi biayanya. Warna hanya bergantung pada `productId * 37 % 255`; karena 37 dan 255 tidak punya faktor bersama, produk
> 1 sampai 255 mencakup semua kemungkinan. Jadi 255 bukan sampel, tapi seluruhnya."*

### Test dulu, baru kode

1. Commit `48cab1c`: `ThumbnailServiceTest` membandingkan layanan dengan salinan cara lama untuk 255 warna dan tiga id besar.
   **Lulus pada kode lama.**
2. Commit `0f51d95`: `ThumbnailService` membuat gambar langsung 200 x 200. **Test yang sama tetap lulus.**

### Ukur ulang dan bukti

| 2 req/detik, flag menyala | Patokan flag mati | Sebelum | Sesudah |
| --- | --- | --- | --- |
| Penjual 1 p95 | 22 ms | sekitar 57 s, 40,7% gagal | **39 ms**, 0 gagal |
| Penjual lain p95 | 23 ms | sekitar 55 s, 35,9% gagal | **25 ms**, 0 gagal |
| CPU aplikasi (`ps`, per core) | - | 137 sampai 299% | 0,8 sampai 9,3% |

Respons sebelum dan sesudah untuk tiga penjual: sama persis (`cmp`). `task evidence LAB=pk-200` dijalankan dari commit
`0f51d95` dengan pohon kerja bersih, dan berkasnya dilampirkan di komentar PR, tidak di-commit.

> **Bimo berpikir (tinjauan):** *"Test-nya ditulis sebelum kode diubah; itu yang membuatnya bernilai. Bukti dari `0f51d95`,
> dan commit sesudahnya hanya jurnal. Satu hal: selisih run Jumat dan Sabtu belum dijelaskan. Itu harus masuk tindak lanjut."*
>
> **Citra berpikir (tinjauan):** *"Patokan dan hasil diukur di hari yang sama, jadi target 2 x patokan sah. Penjual 1 masih 17
> ms di atas patokan, dan PR tidak mengklaim lebih dari itu."*

Komentar penutupan Issue #5 memakai templat lengkap: akar masalah, sinyal, batasan dengan berkas sumber, alternatif, hasil,
bukti, dan empat tindak lanjut.

---

## Yang patut ditiru

| Kebiasaan | Di mana |
| --- | --- |
| Risiko ditulis dengan tanda, lalu rencana diubah secara terbuka saat terjadi | Issue #1 |
| Laju diturunkan saat laptop jenuh, dengan alasan tertulis | Issue #2, laporan bukti 2 |
| Dugaan yang gugur tetap ditulis | Laporan #4821, jurnal PK-200 |
| Batasan diambil dari dokumen konteks dan dicatat dengan sumbernya | Jurnal PK-200 bagian 2 |
| Prediksi yang terasa pasti tetap diuji, dengan cakupan penuh | `BandingThumbnail.java`, 255 warna |
| Test patokan ditulis sebelum kode diubah | Commit `48cab1c` lalu `0f51d95` |
| Bukti dari commit yang bersih, dilampirkan, tidak di-commit | PR #6 |
| Peninjau memeriksa hal yang berbeda dan menyebut apa yang diperiksa | PR #3, PR #6 |
| Hal yang belum terjelaskan masuk tindak lanjut | Penutupan Issue #5 |

*Kembali ke: [Bagian 9](materi/09-dari-diagnosis-ke-perbaikan.md) · [Bagian 8](materi/08-contoh-pengerjaan-tiket.md) · [Daftar isi](MATERI-DASAR.md)*
