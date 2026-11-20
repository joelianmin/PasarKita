# Contoh ujung ke ujung: satu tiket perbaikan dari awal sampai ditutup

> **Seluruhnya rekaan.** Tiket #4900, kartu "Total penjualan", tabel ringkasannya, dokumen konteks, nama orang, dan
> semua angka di sini dibuat untuk contoh. Endpoint dasbor penjual **tidak ada** di PasarKita, jadi perintah ditulis untuk
> menunjukkan bentuknya, bukan untuk kamu jalankan. Kasusnya sengaja berbeda dari tiket PK-201 sampai PK-208.
>
> Tujuannya memperlihatkan **apa yang dilakukan dan apa yang dipikirkan** tiap orang di setiap langkah, termasuk
> dugaan yang keliru dan koreksinya. Panduan di baliknya: `docs/materi/09-dari-diagnosis-ke-perbaikan.md`.

## Pemeran

| Orang | Peran | Catatan |
| --- | --- | --- |
| **Ayu** | Pemilik tiket #4900 | Sudah menjalankan Lab 0, pertama kali memperbaiki kode |
| **Bimo** | Peninjau 1 | Memegang tiket lain di kelompok yang sama |
| **Citra** | Peninjau 2 | Memegang tiket lain di kelompok yang sama |
| Pengajar | Membaca Issue, PR, dan komentar penutupan | Tidak ikut mengerjakan |

Kelompok contoh beranggota tiga orang, jadi dua peninjau berasal dari kelompok sendiri.

---

## Tiket dan paket konteks

**#4900 · Kartu "Total penjualan" di dasbor penjual lambat**

> **Dewi, Dukungan Penjual (Senin, 10.00):** Penjual besar bilang kartu "Total penjualan" di dasbor mereka butuh dua sampai tiga detik. Penjual kecil tidak mengeluh.
>
> **Pak Hadi, Senior Developer (10.20):** Pasti karena RAM server kurang. Naikkan saja ke 32 GB, atau pasang read replica.
>
> **Rina, Product (10.35):** Mulai lambat sejak rilis hari Jumat.

**Yang diharapkan:** p95 kartu turun minimal 70% untuk penjual terbesar, tanpa mengubah angka yang ditampilkan.

Paket konteks berisi tiga berkas:

- `kebijakan-keuangan.md` v1.2: angka di dasbor harus **sama persis** dengan laporan keuangan; tampilan tidak boleh
  **tertinggal lebih dari 1 menit**.
- `catatan-rilis-jumat.md`: rilis Jumat hanya mengubah warna tombol dan teks bantuan.
- `tren-penjual.md`: invoice penjual terbesar naik tiga kali lipat dalam tiga bulan.

---

## Hari 1 (Jumat): membaca, merencanakan, memprediksi

### 09.05 · Rencana sesi (seluruh kelompok)

**Dilakukan.** Bimo membuka Issue "Rencana sesi" dari templat. Ketiganya mengisi tujuan, tiket, ukuran, dan risiko dalam
15 menit. Ayu menulis tiket #4900 dengan ukuran **L**.

> **Ayu berpikir:** *"Ini pertama kali aku mengubah kode, bukan cuma mengukur. Langkahnya baru, jadi naik satu ukuran dari M
> ke L, sesuai panduan rencana. Kalau ternyata cepat, aku tulis saja di refleksi."*
>
> **Bimo berpikir:** *"Risiko terbesar kami bukan kodenya, tapi tinjauan PR. Kalau tiga PR selesai bersamaan hari Selasa,
> kami saling menunggu. Aku tulis risiko itu dengan tandanya: 'belum ada PR terbuka pada Senin sore'."*

### 09.25 · Issue tiket dan prediksi (Ayu)

**Dilakukan.** Ayu membuat Issue dari templat "Tiket kerja", memasang label `triage`, dan menyalin
`docs/templat/JURNAL-DIAGNOSIS.md` menjadi jurnalnya. Ia menulis prediksi **sebelum** membuka alat apa pun:

| # | Dugaan (belum terbukti) | Yang akan diukur | Hasil yang diharapkan |
| --- | --- | --- | --- |
| 1 | Query kartu tidak memakai indeks, jadi memindai seluruh tabel invoice | `EXPLAIN (ANALYZE, BUFFERS)` query kartu untuk penjual 1 | Seq Scan pada `invoice` |
| 2 | Rilis Jumat ikut berperan | Isi catatan rilis, jam mulai keluhan | Ada perubahan di jalur kartu |

> **Ayu berpikir:** *"Dugaan 1 rasanya kuat, karena gejalanya mirip yang sering dibahas: lambat untuk data besar. Tapi materi
> bilang prediksi tidak dinilai benar atau salah. Jadi aku tulis saja, lalu aku cari satu ukuran yang bisa menggugurkannya."*

### 09.50 · Membaca paket konteks (Ayu)

**Dilakukan.** Ayu membaca tiga berkas konteks dan mengisi daftar asumsi.

| Batasan | Sumber | Jenis | Dampak pada pilihan |
| --- | --- | --- | --- |
| Angka sama persis dengan laporan keuangan | `kebijakan-keuangan.md` v1.2, poin 1 | Keras | Perbaikan harus bisa dibuktikan menghasilkan angka yang sama |
| Tertinggal paling lama 1 menit | `kebijakan-keuangan.md` v1.2, poin 2 | Keras | Cache berumur beberapa menit tidak boleh |
| Rilis Jumat hanya warna dan teks | `catatan-rilis-jumat.md` | Konteks | Menggugurkan dugaan 2 sebelum mengukur |
| Invoice penjual terbesar naik 3x dalam 3 bulan | `tren-penjual.md` | Konteks | Masalah memburuk bila dibiarkan |
| "Naikkan RAM" dan "read replica" | Thread Pak Hadi | Pendapat | Tidak mengikat; perlu bukti |

> **Ayu berpikir:** *"Dugaan 2 sudah gugur tanpa mengukur: rilisnya tidak menyentuh kartu. Aku tidak menghapus barisnya di
> jurnal, aku tandai 'gugur, sumber: catatan rilis'. Batas 1 menit itu penting, karena itu menolak cara paling gampang:
> cache."*

### 10.30 · Triage sinyal (Ayu)

| Sinyal | Kategori | Alasan dan bukti |
| --- | --- | --- |
| Kartu lambat untuk penjual besar, tidak untuk penjual kecil | Gejala utama | Bisa direproduksi; akan diukur |
| Keluhan masuk ke Dukungan | Akibat | Muncul karena gejala |
| "Mulai lambat sejak rilis Jumat" | Kebetulan waktu | Catatan rilis tidak menyentuh kartu; data penjual besar tumbuh 3x |
| "RAM kurang" | Dugaan keliru (sementara) | Belum diukur; akan dicek memori dan CPU |
| "Pasang read replica" | Solusi titipan | Usul perbaikan sebelum penyebab diketahui |

> **Ayu berpikir:** *"Untuk 'RAM kurang' aku tulis 'sementara', karena belum ada angkanya. Jangan memvonis dugaan senior
> hanya karena aku tidak suka usulnya. Buktinya harus dari ukuran."*

Ayu mengganti label Issue dari `triage` ke `diagnosis` setelah reproduksi berhasil (di bawah).

---

## Hari 1 (Jumat siang): reproduksi dan diagnosis

### 13.00 · Reproduksi (Ayu)

**Dilakukan.** Ayu mengukur kartu untuk penjual terbesar (id 1) dan penjual kecil, 2 req/detik, 3 menit, run pertama dibuang
sebagai pemanasan. Angka rekaan:

| Penjual | p50 | p95 | Error |
| --- | --- | --- | --- |
| Penjual 1 (terbesar) | 1.620 ms | 2.410 ms | 0% |
| Penjual kecil | 18 ms | 60 ms | 0% |

Memori bebas di server 60%, CPU database 18%.

> **Ayu berpikir:** *"'RAM kurang' sekarang benar-benar gugur: memori bebas 60%. Dan jarak 2.410 ms lawan 60 ms terlalu
> besar untuk masalah mesin, karena mesinnya sama untuk keduanya. Yang berbeda hanya datanya."*

### 13.40 · Menelusuri kode dan SQL (Ayu)

**Dilakukan.** Ayu membaca controller kartu dan repository-nya, lalu memakai `pg_stat_statements` seperti Bagian 8.3.
Satu request menghasilkan **satu query**:

```sql
select coalesce(sum(amount), 0) from invoice where seller_id = $1
```

Lalu `EXPLAIN (ANALYZE, BUFFERS)` untuk penjual 1 (rekaan, diringkas):

```text
Aggregate  (actual time=1580 ms)
  -> Index Scan using idx_invoice_seller on invoice  (rows=381.204)
```

> **Ayu berpikir:** *"Dugaan 1-ku salah, dan itu temuan penting. Indeksnya **dipakai**: Index Scan, bukan Seq Scan. Masalahnya
> bukan cara mencari baris, tapi **jumlah baris yang harus dijumlahkan**: 381 ribu baris setiap kali kartu dibuka. Indeks
> apa pun tetap harus membaca semua baris itu, karena yang diminta memang totalnya. Kalau tadi aku langsung menambah indeks,
> aku memperbaiki hal yang tidak rusak."*

Ia menulis di jurnal, bagian "Dugaan awal vs temuan": *"Dugaan 1 salah: indeks sudah dipakai. Penyebabnya volume yang
dijumlahkan per request, dan volume itu tumbuh 3x dalam 3 bulan."*

### 14.30 · Komentar titik cek (Ayu, di Issue rencana)

```text
Cek 14.30
Selesai: #4900 reproduksi dan EXPLAIN; dugaan 1 (indeks) gugur, penyebab: 381 ribu baris dijumlahkan tiap request
Berikutnya: #4900 bandingkan alternatif
Hambatan: tidak ada
```

---

## Hari 2-3 (Sabtu-Senin): memilih dan membuat perbaikan

### Membandingkan alternatif (Ayu)

| Alternatif | Akar masalah | Batasan | Biaya dan risiko | Bisa dibatalkan |
| --- | --- | --- | --- | --- |
| **A.** Tabel ringkasan per penjual, ditambah setiap invoice terbit dalam transaksi yang sama | Ya: kartu membaca 1 baris | Sama persis bila penambahan selalu satu transaksi dengan invoice; tidak tertinggal | Migrasi baru, satu `UPDATE` tambahan per invoice | Sedang: hapus tabel, kembali ke query lama |
| **B.** Cache total 10 menit | Menutupi | **Melanggar** batas 1 menit | Angka basi | Mudah |
| **C.** Read replica | Tidak: replica menjalankan query yang sama | Replica bisa tertinggal | Biaya infrastruktur | Sulit |
| **D.** Indeks baru | Tidak: indeks sudah dipakai (EXPLAIN 13.40) | Terpenuhi | Rendah | Mudah |

> **Ayu berpikir:** *"D aku masukkan dengan sengaja, karena itu dugaan awalku. Menulis kenapa ia ditolak, dengan bukti
> EXPLAIN, lebih berguna daripada pura-pura tidak pernah memikirkannya. Pilihan A, tapi A punya satu janji yang harus aku
> buktikan: angkanya sama persis. Kalau penambahan ringkasan dan invoice tidak dalam satu transaksi, suatu hari angkanya
> bisa selisih, dan itu melanggar batasan keras."*

### Senin 10.00 · Perubahan rencana (Ayu, di Issue rencana)

```text
Perubahan 10.00
Berubah: #4900 tetap L, tetapi langkah "isi awal tabel ringkasan" ditambah (45 menit)
Pemicu: tabel ringkasan baru kosong; tanpa isi awal, kartu menampilkan 0 untuk penjual lama (ditemukan saat uji lokal 09.40)
Dikorbankan: tinjauan silang untuk tiket Bimo digeser dari Senin sore ke Selasa pagi
```

> **Ayu berpikir:** *"Ini hampir lolos. Kalau aku tidak menguji dengan penjual yang sudah lama punya invoice, kartu akan
> menampilkan 0. Migrasinya harus mengisi ringkasan dari data invoice yang sudah ada, satu kali, dalam berkas migrasi versi
> baru, bukan mengubah V1."*

### Senin 14.00 · Mengukur ulang (Ayu)

**Dilakukan.** Ayu commit dulu, lalu menjalankan pengukuran dengan skrip, data, dan laju yang sama seperti sebelumnya.
Ia juga menulis satu query pembanding untuk lima penjual:

```sql
select s.seller_id, s.total as ringkasan, (select sum(amount) from invoice i where i.seller_id = s.seller_id) as hitung_ulang
from ringkasan_penjual s where s.seller_id in (1, 2, 50, 300, 1200);
```

Hasil rekaan: kelima baris sama persis. p95 penjual 1 turun dari 2.410 ms menjadi 14 ms; penjual kecil tetap sekitar 12 ms.

> **Ayu berpikir:** *"Lima penjual sama, termasuk yang terbesar dan yang paling baru. Tapi itu sesudah migrasi. Aku juga perlu
> satu bukti bahwa invoice **baru** menambah ringkasan dengan benar: buat satu checkout, lalu bandingkan lagi. Kalau tidak,
> peninjau pasti menanyakannya."*

Ia membuat satu checkout, membandingkan ulang, dan angka tetap sama. Lalu membuka PR, memasang label `review` di Issue.

---

## Hari 4 (Selasa): tinjauan PR

### 08.30 · Tinjauan Bimo

**Dilakukan.** Bimo memakai daftar periksa Bagian 9.7, membaca PR dari atas: akar masalah, daftar asumsi, alternatif, lalu
berkas yang berubah. Ia meminta perubahan (**Request changes**):

```text
1. Bukti "sama persis" hanya untuk lima penjual dan satu checkout. Bagaimana dengan invoice yang dibatalkan?
   Di laporan keuangan, invoice batal tidak dihitung. Apakah ringkasan juga dikurangi?
2. Di migrasi, isi awal ringkasan dan pembuatan tabel ada di satu berkas. Kalau isi awal gagal di tengah,
   apakah tabelnya setengah terisi?
```

> **Bimo berpikir:** *"Angkanya meyakinkan, tapi daftar asumsi Ayu bilang 'sama persis dengan laporan keuangan'. Aku buka
> lagi `kebijakan-keuangan.md`: laporan keuangan tidak menghitung invoice batal. Kalau aplikasinya punya pembatalan,
> ringkasan harus ikut turun. Aku tidak tahu jawabannya, jadi aku tanya, bukan menuduh. Dan aku tidak menulis 'LGTM' hanya
> karena p95-nya bagus."*

### 10.15 · Ayu menanggapi

**Dilakukan.** Ayu memeriksa kode: ada jalur pembatalan invoice yang belum ia sentuh. Ia menambah pengurangan ringkasan di
jalur itu, dalam transaksi yang sama, lalu menambah satu pembanding untuk invoice batal. Untuk poin 2, ia menjelaskan bahwa
Flyway menjalankan satu berkas migrasi dalam satu transaksi di PostgreSQL, jadi tidak ada keadaan setengah terisi; ia
menautkan baris log migrasi sebagai bukti.

> **Ayu berpikir:** *"Bimo benar, dan aku tidak akan menemukannya sendiri karena aku tidak pernah menguji pembatalan.
> Ini yang dimaksud 'peninjau memeriksa, bukan sekadar menyetujui'. Untuk poin 2, aku tidak cukup bilang 'aman'; aku
> tunjukkan buktinya."*

### 13.00 · Tinjauan Citra

**Dilakukan.** Citra memeriksa batasan kedua yang belum disentuh Bimo: tertinggal paling lama 1 menit. Ia membaca bahwa
ringkasan ditambah dalam transaksi yang sama dengan invoice, jadi begitu transaksi selesai, kartu sudah benar. Ia menjalankan
ulang skrip ukur di laptopnya sendiri dan mendapat p95 18 ms (berbeda sedikit dari Ayu karena mesinnya berbeda).
Ia menyetujui (**Approve**):

```text
Diperiksa: batasan 1 menit (ringkasan ditambah dalam transaksi yang sama, tidak ada jeda), angka sebelum-sesudah
memakai skrip yang sama (saya ulang: p95 18 ms di laptop saya), alternatif B ditolak dengan alasan batasan yang benar.
Tidak diperiksa: beban checkout bersamaan dalam jumlah besar; menurut saya cukup ditulis sebagai tindak lanjut.
```

> **Citra berpikir:** *"Bimo sudah memeriksa angka dan pembatalan. Kalau aku memeriksa hal yang sama lagi, tinjauanku tidak
> menambah apa-apa. Aku ambil batasan yang belum disentuh siapa pun, dan aku ulangi pengukurannya di mesinku. Aku juga jujur
> soal apa yang tidak aku periksa."*

### 15.30 · Bimo menyetujui

Bimo membaca perubahan Ayu, menjalankan pembanding invoice batal, dan menyetujui dengan komentar yang menyebut apa yang ia
periksa. PR punya dua persetujuan.

---

## Hari 5 (Rabu): bukti dan penutupan

### 09.00 · Bukti (Ayu)

**Dilakukan.** Ayu memastikan `git status` bersih, menjalankan pengukuran akhir, lalu `task evidence`. Berkas bukti
dilampirkan ke PR.

> **Ayu berpikir:** *"Hampir saja aku `git add .` dan ikut meng-commit berkas bukti di folder `out/`. Untung `out/` diabaikan
> Git, tapi tetap aku pilih berkas satu per satu. Dan bukti dibuat sesudah commit, karena bukti dari pohon kerja yang kotor
> tidak sah."*

### 09.30 · Komentar penutupan (Ayu, di Issue)

```markdown
## Penutupan

**Akar masalah:** kartu menjumlahkan semua invoice penjual setiap kali dibuka; penjual 1 punya 381 ribu invoice
(p95 2.410 ms lawan 60 ms untuk penjual kecil). Indeks sudah dipakai, jadi masalahnya volume, bukan pencarian.

**Sinyal di tiket:**
| Sinyal | Status | Bukti |
| --- | --- | --- |
| Lambat untuk penjual besar | Gejala utama | k6 2.410 ms vs 60 ms |
| Sejak rilis Jumat | Kebetulan waktu | catatan-rilis-jumat.md: hanya warna dan teks |
| RAM kurang | Dugaan keliru | memori bebas 60%, CPU database 18% |
| Read replica | Solusi titipan | replica menjalankan query yang sama |

**Batasan dari paket konteks:**
| Batasan | Sumber (berkas) | Bagaimana dipenuhi |
| --- | --- | --- |
| Sama persis dengan laporan keuangan | kebijakan-keuangan.md v1.2 poin 1 | Ringkasan berubah dalam transaksi yang sama dengan invoice terbit dan batal; pembanding 5 penjual + 1 checkout + 1 pembatalan sama persis |
| Tertinggal paling lama 1 menit | kebijakan-keuangan.md v1.2 poin 2 | Tidak ada jeda: ringkasan benar begitu transaksi selesai |

**Alternatif yang dipertimbangkan:** B (cache 10 menit) ditolak karena melanggar batas 1 menit; C (read replica) ditolak
karena tidak menyentuh penyebab; D (indeks) ditolak karena EXPLAIN menunjukkan indeks sudah dipakai.

**Perbaikan:** PR #12

**Hasil:**
| Metrik | Sebelum | Sesudah |
| --- | --- | --- |
| p95 penjual 1 | 2.410 ms | 14 ms |
| p95 penjual kecil | 60 ms | 12 ms |
| Query per request | 1 (381 ribu baris dibaca) | 1 (1 baris dibaca) |

**Bukti:** evidence-<waktu>.md (terlampir di PR #12)

**Tindak lanjut:** beban checkout bersamaan dalam jumlah besar belum diuji (baris ringkasan penjual yang sama diperbarui
banyak transaksi); dicatat oleh Citra saat tinjauan.
```

Ayu mengganti label ke `selesai`. Issue tertutup lewat PR (`Closes #`).

### 10.00 · Refleksi (seluruh kelompok, di Issue rencana)

```text
Refleksi
Ukuran vs kenyataan: #4900 diperkirakan L, nyata sekitar L ditambah 45 menit untuk isi awal ringkasan yang terlewat di rencana
Perubahan yang paling berguna: tinjauan Bimo soal invoice batal; tanpa itu angka akan selisih begitu ada pembatalan
Kami akan mengubah: menulis "data lama" dan "jalur pembatalan" sebagai risiko tetap di setiap rencana perbaikan
```

> **Ayu berpikir:** *"Kalau dihitung ulang, yang paling menentukan bukan kode, tapi tiga hal: menulis prediksi lalu berani
> menggugurkannya, membaca batasan sebelum memilih, dan mau menerima tinjauan yang menemukan lubang. p95 14 ms itu hasilnya,
> bukan pekerjaannya."*

---

## Yang patut ditiru dari contoh ini

| Kebiasaan | Di mana terlihat |
| --- | --- |
| Prediksi ditulis sebelum mengukur, lalu digugurkan dengan bukti | 09.25 dan 13.40 |
| Dugaan keliru dan solusi titipan ditolak dengan angka, bukan dengan opini | 13.00, tabel alternatif |
| Batasan dari konteks dicatat dengan sumbernya, lalu menentukan pilihan | 09.50, tabel alternatif |
| Alternatif yang ditolak tetap ditulis, termasuk dugaan awal sendiri | Alternatif D |
| Rencana diubah dengan pemicu dan apa yang dikorbankan | Senin 10.00 |
| Peninjau memeriksa hal yang belum diperiksa, dan jujur soal yang tidak diperiksa | Tinjauan Bimo dan Citra |
| Commit dulu, baru bukti; bukti tidak di-commit | Rabu 09.00 |
| Hal yang sengaja dibiarkan ditulis sebagai tindak lanjut | Komentar penutupan |

*Kembali ke: [Bagian 9](materi/09-dari-diagnosis-ke-perbaikan.md) · [Daftar isi](MATERI-DASAR.md)*
