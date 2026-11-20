# Panduan: mengambil pembaruan dari templat

Repo kelompokmu dibuat dari templat PasarKita. Saat pengajar menambah lab, tiket, atau materi di templat,
repo kelompokmu **tidak berubah sendiri**. Panduan ini mengambil pembaruan itu tanpa menyentuh pekerjaanmu.
Satu orang di kelompok mengerjakannya lewat satu PR, lalu semua anggota mengambil hasilnya.

## Yang diambil dan yang tidak

| Diambil dari templat | Tetap milikmu (tidak disentuh) |
| --- | --- |
| `Taskfile.yml`, `infra/`, `tools/`, `labs/`, `docs/`, `.github/`, `README.md` | `group.env`, `BASELINE.md`, `KONTRIBUSI.md`, `src/`, `pom.xml` |
| Folder lab baru, materi baru, templat Issue dan PR | `labs/latihan/LAPORAN-*.md`, laporan dan jurnal yang kamu tulis, `labs/*/out/` |

Berkas yang kamu buat sendiri dan tidak ada di templat tidak terhapus.

**Pernah mengubah `Taskfile.yml`, `infra/`, atau berkas alat lain?** (misalnya mengikuti materi 2.7). Perubahan
itu akan tertimpa. Simpan dulu: `git diff Taskfile.yml infra > perubahanku.patch`, lalu terapkan lagi sesudah
langkah 6 dengan `git apply perubahanku.patch` bila masih diperlukan. Jangan meng-commit `perubahanku.patch`.

## Langkah (oleh satu orang, per pembaruan)

1. Pastikan tidak ada pekerjaan yang belum di-commit, lalu ambil `main` terbaru:
   ```bash
   git status
   git switch main
   git pull
   ```
2. Tambahkan templat sebagai remote. Cukup **sekali**; kalau muncul `remote templat already exists`, lewati:
   ```bash
   git remote add templat https://github.com/joelianmin/PasarKita.git
   ```
3. Unduh isi templat (templat publik, tidak perlu login):
   ```bash
   git fetch templat
   ```
4. Buat cabang kerja:
   ```bash
   git switch -c perbarui-templat
   ```
5. Lihat perubahan pada berkas alat yang paling mungkin pernah kamu ubah. Kosong berarti aman:
   ```bash
   git diff --stat HEAD templat/main -- Taskfile.yml infra tools
   ```
6. Ambil berkas dari templat:
   ```bash
   git checkout templat/main -- Taskfile.yml infra tools labs docs .github README.md
   ```
7. Periksa dan commit:
   ```bash
   git status
   git commit -m "Ambil pembaruan templat"
   git push -u origin perbarui-templat
   ```
8. Buka PR dari `perbarui-templat` ke `main` di GitHub, minta satu rekan melihatnya, lalu gabungkan (merge).
   Kelompok satu orang boleh menggabungkannya sendiri.
9. **Semua anggota**, setelah PR digabungkan:
   ```bash
   git switch main
   git pull
   ```
10. Periksa hasilnya: `task --list` harus menampilkan perintah yang sama dengan templat, dan `labs/` harus berisi
    folder lab terbaru. Jalankan `task doctor` bila ragu.

Git versi lama (di bawah 2.23) tidak punya `git switch`. Ganti `git switch main` dengan `git checkout main`, dan
`git switch -c perbarui-templat` dengan `git checkout -b perbarui-templat`. Cek versimu dengan `git --version`.

## Setelah mengambil pembaruan

- Buat label baru yang dibutuhkan lab terbaru di **Issues, Labels** (lihat Langkah 0 di
  [panduan tiket](PANDUAN-TIKET.md)). Label tidak ikut terambil karena tersimpan di GitHub, bukan di berkas.
- Baca `labs/README.md` untuk daftar lab dan skala data yang dipakai.

## Jika bermasalah

| Gejala | Penyebab dan cara memperbaiki |
| --- | --- |
| `fatal: couldn't find remote ref templat/main` atau `invalid reference` | `git fetch templat` belum berhasil. Ulangi langkah 3, lalu cek `git branch -r`. |
| `error: pathspec ... did not match any file(s)` | Satu nama pada langkah 6 tidak ada di templat. Hapus nama itu dari perintah, jalankan lagi. |
| `git status` menampilkan banyak berkas berubah di luar daftar di atas | Jangan di-commit. `git switch main`, hapus cabang (`git branch -D perbarui-templat`), lalu ulangi dari langkah 4. |
| Perubahanmu pada `Taskfile.yml` hilang | Terapkan `perubahanku.patch` (lihat bagian atas). |

## Jangan

- **`git merge templat/main` atau `git pull templat main`.** Repo kelompokmu dan templat tidak punya riwayat
  bersama, sehingga gabungan memunculkan konflik di hampir semua berkas.
- **`git push --force`.** Riwayat dan jejak kerja kelompok bisa hilang (lihat [panduan push](PANDUAN-PUSH.md)).
- Mengubah `group.env` atau meng-commit berkas bukti.
