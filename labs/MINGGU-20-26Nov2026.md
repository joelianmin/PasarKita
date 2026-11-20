# Tiket 20 Nov - 26 Nov 2026 (Akses Data)

## Cara mengerjakan tiket

Setiap tiket adalah satu lab. Setiap anggota memegang tepat satu tiket sebagai pemilik, jadi kelompok beranggota N (satu sampai tujuh orang) mengambil N tiket berbeda dari delapan tiket di bawah. Satu tiket hanya diambil satu orang di satu kelompok, dan satu kelompok tidak boleh mengambil PK-201 dan PK-203 sekaligus. Tiket ditulis seperti percakapan sungguhan antara orang-orang di PasarKita, jadi tidak semua pendapat di dalamnya benar. Tugasmu menemukan akar masalah dan membuktikannya, bukan menjalankan saran yang ada di tiket. Materi pendukung: `docs/materi/09-dari-diagnosis-ke-perbaikan.md`, dan contoh satu tiket dari awal sampai ditutup: `docs/CONTOH-PERBAIKAN-TIKET.md`. Templat jurnal diagnosis dan daftar asumsi: `docs/templat/JURNAL-DIAGNOSIS.md`.

### Alur kerja

1. **Prediksi.** Sebelum menyentuh perangkat, tulis dugaanmu tentang penyebab masalah di jurnal diagnosis.
2. **Baca paket konteks.** Setiap tiket punya paket konteks bisnis berisi dokumen seperti notulen rapat, thread chat, dan kebijakan. Catat setiap batasan yang kamu temukan beserta sumbernya di daftar asumsi. Tidak semua isi paket relevan.
3. **Triage.** Klasifikasi setiap sinyal di tiket: gejala utama, akibat, kebetulan waktu, noise, dugaan keliru, atau solusi titipan.
4. **Reproduksi.** Siapkan lab dengan `task lab LAB=<folder lab>`, ukur dengan `task reproduce LAB=<folder lab>` (contoh: `LAB=pk-201`, huruf kecil), dan tulis cara mereproduksi masalahnya yang bisa diulang orang lain: skrip k6, SQL, atau curl (test JUnit hanya bila Docker tersedia).
5. **Diagnosis dan perbaikan.** Temukan akar masalah dengan alat ukur dari minggu 1, bandingkan minimal dua alternatif, lalu buka PR.
6. **Penutupan.** Setelah PR mendapat dua persetujuan (aturan peninjau di bawah), jalankan `task evidence LAB=<folder lab>` dan tutup tiket dengan komentar penutupan.

Target di setiap tiket dihitung relatif terhadap baseline Lab 0 kelompokmu, kecuali disebutkan lain. Tidak ada sesi tanya jawab bisnis: semua batasan ada di paket konteks, dan kamu yang harus menemukannya. Batasan yang terlewat akan ditemukan dua peninjau PR dan pengajar saat membaca komentar penutupanmu, jadi catat tiap batasan beserta sumbernya di daftar asumsi.

### Peninjau PR

Tiap PR butuh **dua persetujuan** sebelum tiket ditutup, dan pemilik tiket tidak boleh menyetujui PR-nya sendiri. Dua persetujuan itu datang dari:

| Anggota kelompok | Peninjau |
| --- | --- |
| 3 sampai 7 orang | Dua anggota lain di kelompok |
| 2 orang | Rekan satu kelompok dan **satu teman dari kelompok lain** |
| 1 orang | **Dua teman dari kelompok lain** |

Peninjau memakai daftar periksa di Bagian 9.7 dan menulis persetujuan sebagai review "Approve" di GitHub, dengan komentar yang menyebut apa yang diperiksa.

**Mencari teman peninjau (kelompok satu atau dua orang).**

- Carilah teman dari kelompok lain di awal sesi, dan tawarkan **tukar tinjau**: kamu meninjau PR mereka, mereka meninjau PR-mu. Kelompok kecil lain biasanya paling mudah diajak.
- Undang temanmu sebagai collaborator dengan akses **Read** di repo kelompokmu (Settings, Collaborators). Peninjau hanya membaca dan memberi review. Repo privat memuat `group.env`, jadi peninjau dari luar kelompok **tidak boleh membagikan isi repo**, termasuk `GROUP_SEED`, kepada siapa pun.
- Dua peninjau dari luar kelompok sebaiknya orang yang berbeda dan tidak memegang tiket yang sama denganmu.
- Bila sampai **Selasa 24 November 2026, 18.00 WIB** kamu belum menemukan teman, pasang label `review` pada Issue tiketmu. Pengajar akan menggantikan peninjau yang kurang, supaya PR-mu tidak tertahan.

### Label issue

| Label | Arti |
| --- | --- |
| `triage` | Sinyal sedang diklasifikasi dan dugaan sedang disusun |
| `diagnosis` | Gejala sudah direproduksi; akar masalah sedang dicari |
| `review` | PR perbaikan sudah dibuka dan menunggu dua persetujuan |
| `selesai` | Bukti sudah dibuat dan komentar penutupan sudah ditulis |

### Template komentar penutupan

```markdown
## Penutupan

**Akar masalah:** <satu sampai dua kalimat>

**Sinyal di tiket:**
| Sinyal | Status | Bukti |
| --- | --- | --- |

**Batasan dari paket konteks:**
| Batasan | Sumber (berkas) | Bagaimana dipenuhi |
| --- | --- | --- |

**Alternatif yang dipertimbangkan:** <minimal dua, beserta alasan memilih atau menolak>

**Perbaikan:** PR #<nomor>

**Hasil:**
| Metrik | Sebelum | Sesudah |
| --- | --- | --- |

**Bukti:** <nama file dari task evidence>

**Tindak lanjut:** <hal yang sengaja dibiarkan atau perlu dikerjakan nanti>
```
