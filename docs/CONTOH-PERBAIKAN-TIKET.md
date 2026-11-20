# Contoh ujung ke ujung: memperbaiki tiket #4821 dari awal sampai ditutup

> **Yang nyata:** tiket #4821 (`labs/latihan/TIKET.md`), kode PasarKita, perintah, keluaran, dan **semua angka**. Semuanya
> diukur pada 20 November 2026, 17.55 sampai 18.18 WIB, di satu laptop: Intel Core i5-5250U (2 core, 4 thread), RAM 8 GB,
> macOS 12, jalur tanpa Docker, data **skala S** (200 penjual, 5.000 produk).
>
> **Yang simulasi:** nama orang (Ayu, Bimo, Citra), percakapan, dan isi kepala mereka. Setiap angka yang mereka sebut berasal
> dari pengukuran di atas. Angkamu akan berbeda karena mesinmu berbeda; polanya yang penting.
>
> #4821 adalah tiket pemanasan yang sudah kamu diagnosis. Di sini ia dikerjakan dengan alur tiket 20-26 November 2026:
> diagnosis **lalu perbaikan**, PR, dua peninjau, dan komentar penutupan. Kasus ini berbeda dari PK-201 sampai PK-208.
> Perbaikan di bawah tidak masuk ke templat; kode di repo tetap seperti semula.

## Pemeran

| Orang | Peran |
| --- | --- |
| **Ayu** | Pemilik tiket #4821 |
| **Bimo** | Peninjau 1 |
| **Citra** | Peninjau 2 |

---

## 1. Membaca tiket dan memprediksi

**Dilakukan.** Ayu membaca #4821 di `labs/latihan/TIKET.md`, membuat Issue dari templat "Tiket kerja", memasang label `triage`,
dan menyalin `docs/templat/JURNAL-DIAGNOSIS.md`. Sebelum menjalankan apa pun, ia menulis prediksi:

| # | Dugaan (belum terbukti) | Yang akan diukur | Hasil yang diharapkan |
| --- | --- | --- | --- |
| 1 | Query produk untuk toko besar lambat | `pg_stat_statements` untuk request profil penjual 1 | Query produk puluhan sampai ratusan ms |
| 2 | Ada perubahan konfigurasi kemarin | Tabel `feature_flag`, kode yang membaca flag | Satu flag berubah nilai |

> **Ayu berpikir:** *"Tiketnya bilang 'kemarin masih cepat', 'tidak ada rilis kode', dan 'tidak bisa memastikan apakah ada
> perubahan konfigurasi'. Kalau kodenya tidak berubah tapi perilakunya berubah, yang berubah pasti data atau konfigurasi.
> Dugaan 1 aku tulis karena itu yang pertama terlintas, bukan karena aku yakin."*

## 2. Memilah sinyal

Semua sinyal di bawah diambil dari teks tiket #4821 dan dari log aplikasi.

| Sinyal | Kategori | Alasan |
| --- | --- | --- |
| Profil toko lambat, sampai beberapa detik | Gejala utama | Bisa direproduksi dengan `labs/latihan/profile.k6.js` |
| Keluhan paling banyak dari toko yang produknya banyak | Gejala utama (selektif) | Petunjuk bahwa biayanya sebanding dengan jumlah produk |
| Halaman lain tidak dikeluhkan | Petunjuk lokasi | Masalah ada di jalur profil, bukan di seluruh sistem |
| Kemarin masih cepat, tidak ada rilis kode | Petunjuk perubahan | Yang berubah bukan kode |
| WARN `Property 'pasarkita.legacy.timeout' is deprecated` di log | Noise | Muncul tiap 30 detik, juga saat semuanya cepat (17 baris di log selama pengukuran awal) |

> **Ayu berpikir:** *"'Kemarin masih cepat' biasanya aku curigai sebagai kebetulan waktu. Tapi di sini ia bukan kebetulan,
> karena tiket sendiri menyebut kemungkinan perubahan konfigurasi. Aku belum memvonisnya; aku cari buktinya."*

## 3. Reproduksi: laptopnya ikut tumbang

**Dilakukan.** Ayu menyalakan kondisi latihan dan mengukur dengan skrip bawaan (2 req/detik, 3 menit, 30% request ke penjual 1):

```text
$ task latihan:mulai
Kondisi latihan DINYALAKAN (jalur tanpa Docker) pukul 17.57.31 WIB.

$ k6 run labs/latihan/profile.k6.js
checks_failed......: 57.80% 174 out of 301
http_req_duration..: avg=30.22s med=30.01s p(95)=57.5s p(99)=1m0s max=1m0s
```

> **Ayu berpikir:** *"58% gagal dan median 30 detik? Itu bukan 'beberapa detik' lagi. Request menumpuk sampai kena batas 60
> detik k6. Artinya laptopku tidak sanggup melayani 2 request per detik dalam kondisi ini, jadi angkanya mengukur antrean,
> bukan halaman profil. `LAB.md` sudah bilang: kalau laptop tidak kuat, pakai `RATE=1` dan catat di laporan."*

Ia mengulang dengan `RATE=1`:

```text
$ RATE=1 k6 run labs/latihan/profile.k6.js
checks_failed......: 0.00%   0 out of 181
http_req_duration..: avg=1.02s med=733.67ms p(95)=2.5s p(99)=3.19s max=3.95s
```

Sebagai pembanding, sebelum kondisi dinyalakan (2 req/detik, 1 menit): **p50 17,9 ms, p95 56,3 ms**, tanpa error.

## 4. Dua sumber lain: database dan CPU

**Dilakukan.** Ayu mengosongkan `pg_stat_statements`, mengirim 10 request ke profil penjual 1, lalu melihat query yang keluar:

```text
 calls | mean_ms | query
    10 |   0.231 | select s1_0.id,s1_0.city,s1_0.name from seller s1_0 where s1_0.id=$1
    10 |   0.242 | select p1_0.id,p1_0.description,p1_0.name,p1_0.price,p1_0.seller_id,p1_0.sku from product ...
     3 |   0.022 | select enabled from feature_flag where name = $1
```

Selama beban `RATE=1`, CPU proses aplikasi (dari `ps`) melonjak sampai **191,6%** (dua core penuh), sedangkan hanya **satu**
koneksi database yang aktif.

> **Ayu berpikir:** *"Dugaan 1 gugur. Dua query per request, masing-masing di bawah 1 ms. Database tidak sibuk sama sekali,
> tapi CPU aplikasi hampir habis. Jadi waktunya dihabiskan di dalam aplikasi, bukan di database. Dan ada query ketiga yang
> menarik: `feature_flag`. Itu dugaan 2."*

## 5. Menelusuri kode

**Dilakukan.** Ayu membuka `SellerProfileController` (Bagian 8.5 menunjuk ke sini) dan menemukan:

```java
boolean dynamicThumbnail = flags.isEnabled("seller.dynamic-thumbnail");
List<ProductCard> cards = products.findTop60BySellerIdOrderByIdAsc(id).stream()
        .map(p -> new ProductCard(p.getId(), p.getName(), p.getPrice(),
                dynamicThumbnail ? thumbnails.thumbnailFor(p.getId()) : "/images/" + p.getId() + ".jpg"))
```

Lalu `ThumbnailService.thumbnailFor`: membuat gambar **1600 x 1600**, mewarnainya, mengecilkannya ke 200 x 200 dengan
interpolasi bicubic, lalu hanya membaca **satu piksel** untuk membentuk string URL. Itu dilakukan untuk **setiap produk**,
sampai 60 per request.

Di database, `select name, enabled from feature_flag` menunjukkan `seller.dynamic-thumbnail` bernilai `true`.

Ayu mengukur per penjual dengan `curl`, 10 request berurutan tanpa beban lain:

| Penjual | Produk di profil | Median |
| --- | --- | --- |
| 1 | 60 | 1.479 ms |
| 2 | 25 | 576 ms |
| 77 | 25 | 913 ms |

> **Ayu berpikir:** *"Ini menjawab 'toko yang produknya banyak'. Penjual 1 menampilkan 60 produk, penjual 2 hanya 25. 60 dibagi
> 25 itu 2,4; 1.479 dibagi 576 itu 2,6. Hampir sebanding: biayanya per produk. Penjual 77 lebih lambat dari penjual 2 padahal
> produknya sama. Itu belum bisa aku jelaskan; aku tulis sebagai ketidakpastian, bukan aku abaikan.*
>
> *Penyebabnya sekarang jelas: flag menyala, dan tiap produk membuat gambar besar hanya untuk satu string. Tapi siapa yang
> menyalakan flag dan kapan? Tabel `feature_flag` hanya punya kolom `name` dan `enabled`, tanpa waktu perubahan. Jam
> perubahannya tidak bisa aku buktikan dari database. Itu keterbatasan, dan aku tulis."*

Ia mengganti label Issue ke `diagnosis` dan menulis di jurnal: *"Dugaan 1 salah (query di bawah 1 ms). Dugaan 2 benar:
`seller.dynamic-thumbnail` menyala; waktunya di pembuatan gambar per produk."*

## 6. Memilih perbaikan

**Dilakukan.** Ayu menulis "Selesai bila" di Issue, karena tiket ini tidak menyebut target:

- Respons profil **sama persis** dengan sekarang (flag menyala), byte demi byte, untuk beberapa penjual.
- p95 pada 2 req/detik kembali setara kondisi flag mati (sekitar 56 ms di laptop ini), tanpa error.

> **Ayu berpikir:** *"Kenapa respons harus sama dengan kondisi flag menyala, bukan flag mati? Karena aku tidak tahu siapa yang
> menyalakan flag dan apakah fiturnya memang diinginkan. Mematikan flag mengubah isi respons; itu keputusan produk, bukan
> keputusanku. Aku catat sebagai asumsi, supaya peninjau bisa membantahnya."*

Ia memeriksa dugaan itu. Respons penjual 2 saat flag mati dan menyala:

```text
mati : {'id': 1, 'name': 'Sambal Kemasan Besar #1', 'price': 280900.0, 'thumbnail': '/images/1.jpg'}
nyala: {'id': 1, 'name': 'Sambal Kemasan Besar #1', 'price': 280900.0, 'thumbnail': '/thumbnails/1-ff92bbe3.jpg'}
```

Field lain sama; hanya `thumbnail` yang berbeda. Lalu ia membandingkan alternatif:

| Alternatif | Akar masalah | Respons tetap sama? | Catatan |
| --- | --- | --- | --- |
| **A.** Matikan flag | Tidak, hanya menghindari | **Tidak**: `thumbnail` kembali `/images/<id>.jpg` | Cepat sebagai mitigasi darurat, tapi keputusan produk |
| **B.** Simpan hasil per produk (cache) | Sebagian: tiap produk tetap mahal sekali | Ya | Request pertama tiap produk tetap lambat; memori bertambah sebanding jumlah produk |
| **C.** Buat gambar langsung 200 x 200, tanpa 1600 x 1600 | Ya: pekerjaan mahal dihapus | **Belum tahu**, harus diuji | Tanpa state, tanpa memori tambahan |
| **D.** Buat 60 gambar secara paralel | Tidak: total CPU sama | Ya | Laptop sudah jenuh di 191% CPU; paralel memperburuk saat beban |

> **Ayu berpikir:** *"Aku hampir menolak C, karena aku yakin gambar yang dibuat langsung kecil pasti beda warnanya dengan
> gambar besar yang dikecilkan, jadi URL-nya berubah. Tapi itu dugaan, bukan bukti. Aku uji saja."*

**Dilakukan.** Ayu menulis program kecil yang menjalankan kedua cara untuk produk yang sama dan membandingkan URL-nya:

```text
produk 1: sekarang /thumbnails/1-ff92bbe3.jpg | langsung kecil /thumbnails/1-ff92bbe3.jpg
URL berbeda: 0 dari 60 produk
60 thumbnail: sekarang 1007 ms, langsung kecil 18 ms
```

> **Ayu berpikir:** *"Dugaanku salah lagi, dan untung aku uji. Tapi 60 produk belum cukup untuk bilang 'selalu sama'. Warnanya
> hanya bergantung pada `productId * 37 % 255`. Karena 37 dan 255 tidak punya faktor bersama, produk 1 sampai 255 mencakup
> semua 255 kemungkinan warna. Kalau 255 itu sama, semua produk sama."*

```text
URL berbeda: 0 dari 255 produk
```

Pilihan: **C**. A ditolak karena mengubah respons, B karena tidak menghapus biaya, D karena memperburuk CPU yang sudah jenuh.

## 7. Perbaikan dan pengukuran ulang

**Dilakukan.** Ayu membuat cabang, mengubah satu berkas, dan meng-commit sebelum mengukur:

```diff
-import java.awt.geom.AffineTransform;
-import java.awt.image.AffineTransformOp;
 import java.awt.image.BufferedImage;

 @Service
 class ThumbnailService {

+    private static final int UKURAN = 200;
+
+    // Gambar langsung dibuat 200x200, tidak lagi 1600x1600 lalu dikecilkan.
+    // URL yang dihasilkan sama persis untuk semua 255 nilai shade (diuji sebelum perubahan ini).
     String thumbnailFor(Long productId) {
-        BufferedImage source = new BufferedImage(1600, 1600, BufferedImage.TYPE_INT_RGB);
-        Graphics2D graphics = source.createGraphics();
+        BufferedImage thumbnail = new BufferedImage(UKURAN, UKURAN, BufferedImage.TYPE_INT_RGB);
+        Graphics2D graphics = thumbnail.createGraphics();
         try {
             int shade = (int) (productId * 37 % 255);
-            graphics.setPaint(new GradientPaint(0, 0, new Color(shade, 120, 200), 1600, 1600, Color.WHITE));
-            graphics.fillRect(0, 0, 1600, 1600);
+            graphics.setPaint(new GradientPaint(0, 0, new Color(shade, 120, 200), UKURAN, UKURAN, Color.WHITE));
+            graphics.fillRect(0, 0, UKURAN, UKURAN);
         } finally {
             graphics.dispose();
         }
-        AffineTransformOp scale = new AffineTransformOp(
-                AffineTransform.getScaleInstance(0.125, 0.125), AffineTransformOp.TYPE_BICUBIC);
-        BufferedImage thumbnail = scale.filter(source, null);
         return "/thumbnails/" + productId + "-" + Integer.toHexString(thumbnail.getRGB(100, 100)) + ".jpg";
     }
 }
```

Aplikasi dijalankan ulang, flag **tetap menyala**, lalu diukur dengan skrip, laju, dan durasi yang sama:

| Ukuran | Sebelum (flag menyala) | Sesudah (flag menyala) |
| --- | --- | --- |
| 2 req/detik, 3 menit | 57,8% gagal, p50 30,0 s, p95 57,5 s | 0% gagal, p50 18,2 ms, p95 28,9 ms, p99 53,8 ms |
| 1 req/detik, 3 menit | p50 733,7 ms, p95 2,5 s, p99 3,19 s | p50 23,9 ms, p95 65,8 ms, p99 522,0 ms |
| Profil penjual 1, median `curl` | 1.479 ms | 20 ms |
| CPU aplikasi selama beban (`ps`) | sampai 191,6% | 0,5% sampai 7,1% |
| Query database per request | 2 (di bawah 1 ms) | 2 (di bawah 1 ms) |

Respons sebelum dan sesudah, untuk penjual 1, 2, dan 77, dibandingkan dengan `cmp`: **sama persis**.

> **Ayu berpikir:** *"p95 di 2 req/detik sekarang 28,9 ms, bahkan di bawah kondisi flag mati (56,3 ms). Tapi itu dua run
> berbeda dengan durasi berbeda, jadi aku tidak mengklaim 'lebih cepat dari sebelum flag'. Yang aku klaim: setara, dan
> tanpa error. Lalu p99 522 ms di run 1 req/detik. Itu satu-dua request lambat. Run itu yang pertama setelah aplikasi
> dijalankan ulang, jadi dugaanku JIT belum panas. Dugaan, belum terbukti: aku tulis begitu."*

Ia membuka PR berisi diff di atas dan memasang label `review`.

## 8. Tinjauan Bimo

**Dilakukan.** Bimo membaca PR dari atas: akar masalah, asumsi, alternatif, lalu diff. Ia meminta perubahan:

```text
1. "Sama persis" untuk 255 warna: bagaimana kalau productId sangat besar, misalnya di atas 2 miliar?
   productId * 37 bisa melewati batas int.
2. Respons dibandingkan untuk tiga penjual dengan cmp. Itu bagus. Tolong tempel perintahnya di PR,
   supaya aku bisa mengulangnya.
```

> **Bimo berpikir:** *"Argumen 255 warna itu rapi, tapi aku harus memeriksa asumsinya, bukan mengaguminya. `productId` bertipe
> `Long`, `37` int; hasilnya long, jadi tidak meluap. Tapi aku tidak yakin, jadi aku tanya."*

**Ayu menanggapi:** `productId` adalah `Long`, sehingga `productId * 37` dihitung sebagai `long` dan baru diubah ke `int`
setelah `% 255`; hasilnya selalu 0 sampai 254. Perhitungan warna tidak diubah oleh perbaikan ini, sehingga perilakunya untuk
id besar sama dengan sebelumnya. Ia menempel perintah `cmp` dan program pembanding di PR.

Bimo menjalankan perintahnya sendiri, lalu menyetujui dengan komentar yang menyebut apa yang ia periksa.

## 9. Tinjauan Citra

**Dilakukan.** Citra memeriksa hal yang belum disentuh Bimo: hasil di bawah beban dan angka yang janggal.

```text
Diperiksa: run 2 req/detik sesudah perbaikan (0 dari 361 gagal, p95 28,9 ms), CPU turun dari sampai 191,6% ke
paling tinggi 7,1%, diff hanya satu berkas dan tidak menyentuh controller.
Pertanyaan: p99 522 ms dan max 2,1 s di run 1 req/detik. Di run 2 req/detik yang lebih berat, p99 hanya 53,8 ms.
Kenapa run yang lebih ringan punya ekor lebih panjang?
```

> **Citra berpikir:** *"Kalau aku hanya melihat p95, aku melewatkan ini. Run yang lebih ringan seharusnya tidak lebih buruk.
> Penjelasan Ayu (JIT dingin setelah restart) masuk akal karena urutannya cocok: run 1 req/detik dijalankan pertama. Tapi
> itu belum dibuktikan, jadi aku tidak memintanya diperbaiki sekarang. Cukup ditulis sebagai tindak lanjut."*

Ayu menambahkan ke PR bahwa run 1 req/detik adalah run pertama setelah restart (pemanasan 30 detik), dan mencatat pengujian
ulang dengan pemanasan lebih lama sebagai tindak lanjut. Citra menyetujui. PR punya dua persetujuan.

## 10. Bukti dan penutupan

> **Ayu berpikir:** *"`task evidence` membaca `labs/<lab>/out/summary.json`, tapi `profile.k6.js` di latihan tidak menulis
> berkas itu. Untuk latihan, bukti utamanya keluaran k6 dan `cmp` yang aku tempel di PR. Skrip lab 20-26 November sudah
> menulis `summary.json`, jadi di sana `task evidence` berjalan. Aku tulis keterbatasan ini daripada mengarang bukti."*

**Komentar penutupan (Ayu):**

```markdown
## Penutupan

**Akar masalah:** flag `seller.dynamic-thumbnail` menyala; untuk setiap produk di profil (sampai 60),
`ThumbnailService` membuat gambar 1600x1600 lalu mengecilkannya hanya untuk membaca satu piksel.
Biayanya CPU aplikasi (sampai 191,6%), bukan database (2 query, masing-masing di bawah 1 ms).

**Sinyal di tiket:**
| Sinyal | Status | Bukti |
| --- | --- | --- |
| Lambat, sampai beberapa detik | Gejala utama | k6 RATE=1: p50 733,7 ms, p95 2,5 s |
| Toko dengan produk banyak paling lambat | Gejala utama | 60 produk 1.479 ms, 25 produk 576 ms (rasio 2,6 vs 2,4) |
| Kemarin cepat, tidak ada rilis kode | Petunjuk perubahan konfigurasi | feature_flag = true |
| WARN legacy.timeout | Noise | muncul tiap 30 detik, juga saat cepat |

**Batasan:**
| Batasan | Sumber | Bagaimana dipenuhi |
| --- | --- | --- |
| Respons tidak berubah (asumsi: flag dinyalakan dengan sengaja) | Selesai bila di Issue | cmp penjual 1, 2, 77 sama persis; URL sama untuk 255 dari 255 warna |

**Alternatif yang dipertimbangkan:** A (matikan flag) mengubah thumbnail di respons; B (cache) tidak
menghapus biaya pertama; D (paralel) menambah beban CPU yang sudah jenuh. Dipilih C.

**Perbaikan:** PR berisi perubahan ThumbnailService

**Hasil (laptop i5-5250U, skala S, flag menyala):**
| Metrik | Sebelum | Sesudah |
| --- | --- | --- |
| 2 req/detik: gagal / p95 | 57,8% / 57,5 s | 0% / 28,9 ms |
| 1 req/detik: p50 / p95 | 733,7 ms / 2,5 s | 23,9 ms / 65,8 ms |
| CPU aplikasi | sampai 191,6% | paling tinggi 7,1% |

**Bukti:** keluaran k6, pg_stat_statements, dan cmp di PR. task evidence tidak tersedia untuk latihan.

**Tindak lanjut:** (1) p99 522 ms di run pertama setelah restart, dugaan JIT dingin, belum dibuktikan;
(2) siapa dan kapan flag dinyalakan tidak bisa dibuktikan karena feature_flag tidak menyimpan waktu perubahan;
(3) penjual 77 lebih lambat dari penjual 2 dengan jumlah produk sama, belum dijelaskan; (4) belum diukur di skala M.
```

Label diganti ke `selesai`, dan Issue tertutup lewat PR.

> **Ayu berpikir:** *"Dua dugaanku salah: database dan 'gambar kecil pasti beda'. Keduanya aku gugurkan dengan ukuran, bukan
> dengan perasaan. Kalau aku langsung mematikan flag di langkah 3, tiketnya selesai dalam lima menit, dan aku tidak pernah
> tahu bahwa fiturnya bisa tetap ada dengan biaya 18 ms per 60 produk."*

---

## Yang patut ditiru

| Kebiasaan | Di mana |
| --- | --- |
| Prediksi ditulis lebih dulu, lalu diuji, termasuk yang ternyata salah | Langkah 1, 4, dan 6 |
| Laju beban diturunkan ketika laptop jenuh, dan alasannya dicatat | Langkah 3 |
| Lebih dari satu sumber bukti: k6, `pg_stat_statements`, CPU, kode, `curl` | Langkah 3 sampai 5 |
| Keyakinan yang kuat tetap diuji dengan data ("gambar kecil pasti beda") | Langkah 6 |
| Argumen "selalu sama" dibuktikan dengan cakupan penuh (255 warna), bukan sampel | Langkah 6 |
| Hal yang tidak bisa dijelaskan ditulis sebagai ketidakpastian, tidak dibuang | Langkah 5 dan 7 |
| Klaim dibatasi pada yang terukur ("setara", bukan "lebih cepat") | Langkah 7 |
| Peninjau memeriksa asumsi dan angka yang janggal, bukan hanya p95 | Langkah 8 dan 9 |
| Keterbatasan alat ditulis jujur, bukan ditutupi | Langkah 10 |

*Kembali ke: [Bagian 9](materi/09-dari-diagnosis-ke-perbaikan.md) · [Bagian 8](materi/08-contoh-pengerjaan-tiket.md) · [Daftar isi](MATERI-DASAR.md)*
