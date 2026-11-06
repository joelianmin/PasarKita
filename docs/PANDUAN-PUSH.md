# Panduan: kapan dan bagaimana push

Push yang rutin membuat kerja kelompok terlihat: pengajar bisa membantu saat kamu masih
mengerjakan, bukan setelah semuanya selesai, dan setiap anggota punya jejak kontribusinya
sendiri. Panduan ini berlaku untuk Lab 0, latihan membedah tiket, dan lab berikutnya.

## Ritme push

| # | Kapan | Caranya |
| --- | --- | --- |
| 1 | **Commit pertama:** isi `GROUP_SEED` di `group.env`, lalu push | Jangan diubah setelahnya |
| 2 | **Saat tiket didaftarkan:** Issue dibuat dan diberi penanggung jawab | Lihat [panduan tiket](PANDUAN-TIKET.md) |
| 3 | **Di awal pengerjaan:** buat cabang kerja, push, dan buka **PR draf** | PR draf boleh masih kosong |
| 4 | **Tiap selesai satu langkah:** satu run pengukuran, satu temuan, satu bagian laporan | Commit kecil, push segera |
| 5 | **Saat macet:** push pekerjaanmu dulu, lalu buat Issue "Macet / ada error" | Pengajar bisa melihat kodenya |
| 6 | **Sebelum `task evidence`:** commit semua perubahan dan push | Lalu buat bukti dan lampirkan ke PR |
| 7 | **Akhir sesi:** push terakhir, lalu ubah PR draf menjadi siap ditinjau | Jangan menutup laptop dengan pekerjaan yang belum di-push |

Aturan praktis: kalau pekerjaanmu hilang dari laptop sekarang juga, berapa lama yang ikut
hilang? Push sebelum jawabannya lebih dari satu jam.

## Perintah yang dipakai

```bash
git switch -c lab0                       # cabang kerja (sekali)
git add <berkas>                         # pilih berkas, hindari "git add ."
git commit -m "Catat run 1 skala M"      # pesan jelas, tanpa seed
git push -u origin lab0                  # push pertama dari cabang ini
git push                                 # push berikutnya
```

PR draf: di GitHub, **Compare & pull request**, lalu pilih **Create draft pull request**.
Kalau sudah siap, tekan **Ready for review**.

## Setiap anggota memakai akunnya sendiri

Jejak per orang hanya terbaca kalau commit tertaut ke akun GitHub pelakunya.

1. Atur nama dan email di laptop yang kamu pakai:
   ```bash
   git config user.name "Nama Kamu"
   git config user.email "email-yang-terdaftar-di-github"
   ```
   Email harus yang terdaftar di akun GitHub-mu (Settings, Emails), atau alamat
   `...@users.noreply.github.com` dari halaman yang sama. Kalau tidak, commit muncul tanpa akun.
2. **Satu laptop dipakai bersama?** Orang yang mengetik meng-commit, dan anggota lain yang ikut
   bekerja ditulis di akhir pesan commit setelah satu baris kosong:
   ```
   Catat run 1 skala M

   Co-authored-by: Nama Rekan <email-rekan@contoh.com>
   ```
3. Pembagian kerja yang tidak terlihat di commit (misalnya satu menganalisis, satu mengetik)
   ditulis di Issue atau deskripsi PR.

## Jangan

- **`git push --force` dan menulis ulang riwayat** (`rebase`, `reset --hard` lalu push paksa).
  Jejak kerja hilang, dan buktimu ikut hilang.
- **Menulis `GROUP_SEED` di pesan commit,** di Issue, PR, atau komentar.
- **Meng-commit file bukti** (`task evidence`). Lampirkan ke PR.
- **Mengerjakan semuanya lalu push sekali di akhir.** Itu menghilangkan kesempatan dibantu.

## Daftar cek sebelum menutup sesi

- [ ] Semua perubahan sudah di-commit dan di-push (`git status` bersih, `git push` tidak
      menolak)
- [ ] PR ada dan menautkan Issue tiket (`Closes #nomor`)
- [ ] Commit-mu tampil dengan akun GitHub-mu sendiri
- [ ] Hambatan yang belum selesai sudah ditulis di Issue atau bagian "Belum selesai" di PR
