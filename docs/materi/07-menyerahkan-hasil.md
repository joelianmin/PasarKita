# Bagian 7: Menyerahkan hasil

*Git, bukti, dan BASELINE.md*

> **Tujuan.** Mengemas hasil Lab 0 menjadi pull request dengan bukti yang sah dan catatan baseline yang bisa diperiksa orang lain.  
> **Prasyarat.** Bagian 4 (hasil pengukuran) dan 6.1 (jika perlu mendiagnosa sebelum mengumpulkan).  
> **Setelah bagian ini kamu bisa:**
> - memakai Git untuk cabang, commit, dan pull request
> - membuat file bukti yang sah dengan urutan yang benar
> - menulis `BASELINE.md` dengan temuan yang dipisahkan dari tafsiran

---

## 7.1 Git yang dipakai di lab

Git menyimpan riwayat perubahan sebagai deretan **commit**. Tiga area yang perlu kamu
bedakan: *working tree* (file yang kamu edit), *staging* (perubahan yang akan masuk commit
berikutnya), dan *repository* (commit yang sudah tersimpan).

| Tujuan | Perintah |
| --- | --- |
| Lihat apa yang berubah | `git status` |
| Lihat isi perubahan | `git diff` (belum di-stage), `git diff --staged` (sudah di-stage) |
| Buat cabang kerja | `git switch -c lab0-baseline` |
| Pilih file untuk commit | `git add BASELINE.md` (hindari `git add .` tanpa melihat `git status`) |
| Simpan commit | `git commit -m "Lab 0: catatan baseline"` |
| Lihat riwayat | `git log --oneline -10` |
| Batalkan perubahan satu file yang belum di-commit | `git restore <file>` |
| Keluarkan file dari staging | `git restore --staged <file>` |
| Kirim cabang ke remote | `git push -u origin lab0-baseline` |

`git switch` dan `git restore` baru ada sejak Git 2.23. Dengan Git yang lebih lama
(`git --version`), pakai `git checkout -b <cabang>` sebagai pengganti `git switch -c`,
`git checkout -- <file>` sebagai pengganti `git restore <file>`, dan
`git reset HEAD <file>` sebagai pengganti `git restore --staged <file>`.

Kebiasaan yang baik:

- **Satu commit, satu maksud.** Pesan commit menjelaskan *apa* dan *kenapa* dalam satu
  kalimat, bukan "update" atau "fix".
- **Periksa sebelum commit.** `git status` dan `git diff` memastikan hanya file yang kamu
  maksud yang ikut, dan tidak ada password atau token (OWASP A02, A05).
- **Jangan menulis ulang riwayat yang sudah dikirim.** Hindari `git push --force` pada cabang
  yang dilihat orang lain.
- File besar atau hasil mentah tidak perlu di-commit. `.gitignore` sudah mengecualikan
  `target/` dan `labs/*/out/*`.

---

## 7.2 Alur kerja dengan pull request (PR)

1. Pastikan cabang utama bersih dan terbaru, lalu buat cabang baru dari sana.
2. Kerjakan dan commit bertahap.
3. Dorong cabang ke remote dan buka PR ke cabang utama.
4. Isi deskripsi PR: tujuan, apa yang diubah, cara memeriksanya, dan lampiran bukti.
5. Tanggapi komentar peninjau dengan commit tambahan di cabang yang sama. PR ikut
   terbarui otomatis.
6. Setelah disetujui, PR digabung (*merge*) oleh pihak yang berwenang.

Deskripsi PR yang baik menjawab: *apa yang berubah, kenapa, bagaimana kamu tahu itu
benar,* dan *apa yang belum selesai*. Untuk Lab 0, "bagaimana kamu tahu" adalah file bukti
dan tabel baseline.

---

## 7.3 Bukti dengan hash

- `task evidence LAB=0` membuat file bukti di `labs/0/out/` berisi hash SHA-256 yang
  mengikat: nomor lab, commit, `GROUP_SEED`, waktu, dan isi `summary.json`.
- **Commit dulu sebelum membuat bukti.** Jika masih ada perubahan yang belum di-commit,
  file bukti mencantumkan "ADA (bukti tidak sah untuk penilaian)".
- Folder `labs/*/out/` diabaikan Git (kecuali `.gitkeep`), jadi file bukti **tidak ikut
  commit**. Lampirkan secara manual ke PR (misalnya diunggah di deskripsi atau komentar).
- Bukti bergantung pada hasil k6 terakhir. Jangan menjalankan `task reproduce` lagi setelah
  run pengukuran terbaikmu kalau belum membuat buktinya.
- Urutan yang benar: ukur, tulis `BASELINE.md`, `git add` dan `git commit`, **lalu**
  `task evidence LAB=0`, lampirkan, buka PR.
- Jangan memperbaiki apa pun di Lab 0. Tugasnya mencatat kondisi awal.

---

## 7.4 Menulis BASELINE.md

`BASELINE.md` di root repo adalah catatan kondisi awal sistem, pembanding semua perbaikan
di minggu 2–6. Pakai template dari materi minggu 1 jika ada; kerangka di bawah adalah
panduan isinya.

```markdown
# Baseline PasarKita – Lab 0

## 1. Identitas dan lingkungan
- Kelompok, anggota, tanggal, commit (`git rev-parse --short HEAD`)
- GROUP_SEED, skala data (S/M), jumlah baris order/produk/customer
- Laptop: OS, CPU, RAM; memori yang dialokasikan ke Docker
- Versi: Java, Docker, k6, PostgreSQL

## 2. Metode
- Urutan: 1 run pemanasan (dibuang) + 3 run pengukuran, durasi dan laju tiap endpoint
- Kondisi: aplikasi lain ditutup? daya/charger? data direset sebelum run?
- Hal yang sengaja tidak dilakukan (tidak ada perbaikan)

## 3. Hasil per endpoint
Tabel dari `labs/0/out/baseline-table.md` untuk tiap run, lalu ringkasan
(median dan rentang antar run). Kolom "Query/request" diisi dari log SQL.

## 4. Database
- 5 query teratas dari `pg_stat_statements` (calls, total ms, mean ms)
- Hasil `EXPLAIN (ANALYZE, BUFFERS)` untuk query yang dicurigai

## 5. Sumber daya
- CPU, memori JVM, pemakaian pool koneksi, dengan tangkapan layar Grafana
  (sebutkan rentang waktu UTC-nya)

## 6. Temuan yang mencurigakan (minimal 3)
Untuk tiap temuan: Pengamatan, Bukti, Hipotesis, Dampak, Cara memverifikasi.

## 7. Keterbatasan dan ketidakpastian

## 8. Lampiran
- Nama file bukti, daftar perintah yang dijalankan
```

**Cara menulis temuan yang baik**

| Bagian | Isi | Contoh bentuk |
| --- | --- | --- |
| Pengamatan | Fakta berangka dengan satuan | "`GET /api/x` p95 = `<angka>` ms, p50 = `<angka>` ms, 3 run konsisten" |
| Bukti | Dari mana angkanya, agar bisa diperiksa | "Baris ke-N `baseline-table.md`; panel Grafana X pukul `<jam>` UTC" |
| Hipotesis | Dugaan penyebab, jelas bertanda belum terbukti | "Diduga `<penyebab>` karena `<alasan>`" |
| Dampak | Siapa atau apa yang terpengaruh | "Pengguna di `<alur>` menunggu lebih lama" |
| Cara memverifikasi | Langkah yang akan dilakukan di lab berikutnya | "Bandingkan jumlah query dengan jumlah baris yang dikembalikan" |

**Kesalahan yang sering terjadi**

- **Mencampur fakta dan tafsiran.** "Endpoint lambat karena tidak ada indeks" adalah
  tafsiran. Tulis pengamatan dan buktinya dulu, hipotesis di kolom terpisah.
- **Satu run dianggap kebenaran.** Tunjukkan variasi antar run. Kalau tiga run berbeda
  jauh, itu sendiri sebuah temuan.
- **Hanya rata-rata.** Sertakan p50, p95, dan p99 (bagian 1.1).
- **Angka tanpa satuan atau sumber.** "Cepat" atau "lambat" tidak bisa dibandingkan nanti.
- **Mengklaim penyebab pasti.** Lab 0 mencatat kecurigaan, tidak membuktikannya.
- **Mencoba memperbaiki sambil mengukur.** Itu merusak baseline.
- **Tidak menyebut keterbatasan.** Laptop yang sibuk, data skala berbeda, dan layanan luar
  tiruan memengaruhi angka. Sebutkan.

> Cek diri: sebuah temuan berbunyi "Checkout lambat". Ubah menjadi pengamatan, bukti,
> hipotesis, dan cara verifikasi yang masing-masing bisa diperiksa orang lain.

---

*Sebelumnya: [Bagian 6](06-diagnosa-dan-latihan.md) · [Daftar isi](../MATERI-DASAR.md) · Berikutnya: [Referensi](referensi.md)*
