# Panduan: membuat rencana dan menyesuaikannya

Rencana yang dibuat cepat dan diubah ketika kenyataan berbeda adalah keterampilan inti seorang engineer.
Rencana awalmu hampir pasti meleset, dan itu tidak apa-apa. Yang diperhatikan pengajar:

1. seberapa **cepat** kelompok punya rencana tertulis (sebelum mulai bekerja),
2. seberapa **jujur** rencana disesuaikan ketika kenyataan berbeda,
3. seberapa **kuat alasannya**: bisa ditunjuk buktinya, bukan sekadar pendapat.

Ketepatan rencana awal tidak dinilai. Menyembunyikan penyimpangan lebih buruk daripada rencana yang meleset.
Tidak mengubah rencana karena memang tidak perlu juga sah; jangan membuat perubahan hanya supaya ada.
AI boleh membantu, tetapi alasanmu harus merujuk hal yang benar-benar terjadi di kelompokmu (jam, commit, hasil ukur),
dan kamu harus bisa menjelaskannya sendiri.

## Satu tempat saja: Issue

Rencana tidak punya berkas sendiri. Semuanya ada di Issue, tempat tiketmu sudah hidup:

| Apa | Di mana |
| --- | --- |
| Rencana sesi (tujuan, daftar tiket, risiko, titik cek) | **Satu Issue "Rencana sesi"** per kelompok per sesi (templat tersedia) |
| Rincian satu tiket (langkah, ukuran, selesai bila) | **Issue tiket itu sendiri** |
| Status kemajuan | Kotak centang di kedua Issue (GitHub menampilkan "3 of 5") |
| Alasan perubahan | **Komentar** di Issue rencana |
| Riwayat | Riwayat suntingan dan komentar GitHub (tidak perlu menyalin apa pun) |

Langkah 0 (sekali saja): buat label `rencana` di repo kelompok (Issues → Labels → New label), seperti label `tiket`.

## Membuat rencana (15 menit)

Buka Issues → New issue → **Rencana sesi**.

| Menit | Kerjakan |
| --- | --- |
| 0-3 | **Tujuan sesi** dan **waktu efektif** |
| 3-8 | **Tiket sesi ini**: tautkan Issue tiket, tentukan penanggung jawab dan ukuran S, M, atau L |
| 8-11 | **Selesai bila** di tiap Issue tiket: bukti apa yang harus ada |
| 11-14 | **Risiko dan rencana B**: minimal dua, lengkap dengan tandanya |
| 14-15 | **Titik cek** (jam), lalu klik Submit |

Aturan emas: **tulis dulu, sempurnakan nanti.** Rencana kasar yang terbit dalam 15 menit lebih berguna
daripada rencana rapi yang selesai setelah satu jam.

Tentang ukuran: tidak perlu menit per langkah. Patokannya, satu tiket yang kamu kerjakan setelah Lab 0 sekitar 90 menit (M).
Bila langkahnya baru bagimu, naikkan satu ukuran. Sisakan **cadangan bersama** (mis. 30 menit), karena kelompok yang
menjadwalkan 100% waktunya pasti terpaksa mengubah rencana.

## Selama sesi: titik cek

Di jam titik cek, tulis satu komentar singkat di Issue rencana (lima menit, tiga baris):

```text
Cek 14.30
Selesai: <apa yang benar-benar selesai sejak cek terakhir>
Berikutnya: <apa yang dikerjakan sampai cek berikutnya>
Hambatan: <apa yang menghalangi, atau "tidak ada">
```

Kalau ada hambatan lebih dari 20 menit, jangan menunggu titik cek: tulis di Issue "Macet" (templat tersedia).

## Mengubah rencana

Ubah rencana kalau salah satu terjadi:

| Pemicu | Contoh |
| --- | --- |
| Satu langkah memakan jauh lebih lama dari perkiraan | Mengukur 55 menit padahal diperkirakan 30 |
| Bukti baru mengubah arah | Dugaan awal gugur setelah SQL dibaca |
| Hambatan lebih dari 20 menit | Docker tidak mau menyala |
| Waktu atau ruang lingkup berubah | Sesi dipotong, ada tiket dibatalkan |
| Orang atau ketergantungan berubah | Anggota berhalangan, rekan terlambat menyerahkan temuan |

Tidak perlu dicatat: selisih beberapa menit, atau urutan dua langkah kecil yang ditukar.

Caranya dua langkah, tanpa tabel dan tanpa berkas baru:

1. **Sunting isi Issue rencana** supaya mencerminkan rencana yang berlaku sekarang (daftar tiket, ukuran, jam).
2. **Tulis komentar** dengan tiga baris ini:

```text
Perubahan 15.05
Berubah: <apa tepatnya, dari apa menjadi apa>
Pemicu: <apa yang terjadi, dengan buktinya: jam, commit, keluaran perintah, isi Issue>
Dikorbankan: <apa yang kita lepas atau tunda sebagai gantinya, atau "tidak ada">
```

| Lemah | Kuat |
| --- | --- |
| "Kurang waktu" | "Langkah ukur 55 menit, bukan 30. `task seed` skala M memakan 17 menit (log 13.48). Dikorbankan: tinjau silang digeser ke 15.30" |
| Tanpa jam | Ada jam, commit, atau hasil ukur sebagai pemicu |
| Hanya menyebut keputusan | Menyebut apa yang dikorbankan |
| Rencana berubah, tidak ada komentar | Perubahan dicatat segera setelah diputuskan |

Kalau ada pilihan lain yang kamu tolak dan itu penting, satu kalimat di baris "Berubah" sudah cukup.

## Akhir sesi: refleksi (3 baris)

Satu komentar penutup di Issue rencana, lalu tutup Issue-nya:

```text
Refleksi
Ukuran vs kenyataan: <mana yang paling meleset, berapa selisihnya>
Perubahan yang paling berguna: <apa dan mengapa>
Kami akan mengubah: <satu hal pada cara merencanakan sesi berikutnya>
```

Satu hal terakhir itu dibawa ke Issue rencana sesi berikutnya.

## Daftar periksa sebelum Submit

- [ ] Tujuan punya ukuran yang bisa diperiksa
- [ ] Setiap tiket ditautkan, punya penanggung jawab dan ukuran
- [ ] Ada cadangan waktu bersama
- [ ] Setiap tiket punya "Selesai bila" berupa bukti
- [ ] Minimal dua risiko, masing-masing dengan tanda dan rencana B
- [ ] Jam titik cek tertulis
- [ ] Semua anggota sudah membaca Issue-nya

## Contoh

Skenario: Kelompok contoh beranggota tiga orang (Ayu, Bimo, Citra), sesi 13.00 sampai 17.00 WIB, waktu efektif 180 menit.
Ayu memegang tiket #4800 (detail produk, dari Bagian 8.4). Bimo dan Citra memegang "tiket B" dan "tiket C".
Isi tiketnya sengaja tidak ditulis; yang dilihat adalah mutu rencananya.

### Contoh 1: kuat

Issue **[Rencana] 13 Nov, tiga laporan masuk PR**:

```markdown
## Tujuan sesi
Tiga laporan tiket masuk PR, masing-masing berisi angka k6, hasil pg_stat_statements, dan daftar kelas yang dibaca.
Diperiksa dengan membuka PR pukul 17.00.

## Waktu
- 13.00-17.00 WIB. Efektif 180 menit.
- Cadangan bersama: 30 menit untuk saling meninjau laporan (15.30-16.00).

## Tiket sesi ini
- [x] #3 (@ayu), ukuran M
- [ ] #4 (@bimo), ukuran L (seed skala M belum selesai)
- [ ] #5 (@citra), ukuran L (menunggu temuan rekan)

## Risiko dan rencana B
- Seed skala M lama | Tanda: `task seed` belum selesai 13.30 | B: Bimo mulai dari baca kode dulu, ukur menyusul
- Docker tidak menyala | Tanda: `task doctor` gagal | B: pakai jalur tanpa Docker (materi 2.4); tanya di grup bila >20 menit

## Titik cek
14.30, 15.30, 16.30
```

Komentar selama sesi:

```text
Cek 14.30
Selesai: #3 langkah ukur dan baca kode; seed M selesai 13.48
Berikutnya: #3 hipotesis, #4 baca kode
Hambatan: tidak ada

Perubahan 13.52
Berubah: #4 dari ukuran M menjadi L; Bimo mulai baca kode 13.55, bukan 13.35
Pemicu: `task seed` skala M selesai 13.48 (17 menit, commit "catat seed M"); langkah ukur Bimo 55 menit, bukan 30
Dikorbankan: tinjau silang digeser ke 15.30. Memakai skala S kami tolak karena gejalanya lemah dan datanya berbeda dari rekan

Perubahan 15.05
Berubah: di #3, langkah "cari query tambahan" (15 menit) dibatalkan; diganti mengambil trace untuk menguji hipotesis 2
Pemicu: pg_stat_statements menunjukkan 1 query per request, bukan 2 seperti dugaan awal (commit "catat query #3")
Dikorbankan: tidak ada; waktu netral. Laporan menulis dugaan awal dan alasan gugurnya
```

Mengapa kuat: tujuan terukur, ukuran berbeda tiap tiket, ketergantungan dan rencana B bertanda jelas, cadangan waktu ada,
setiap perubahan menyebut bukti dan apa yang dikorbankan, dan semuanya ada di satu tempat.

### Contoh 2: sedang

Issue **[Rencana] 13 Nov**:

```markdown
## Tujuan sesi
Menyelesaikan tiket.

## Tiket sesi ini
- [ ] #3 (@ayu)
- [ ] #4 (@bimo)
- [ ] #5 (@citra)

## Risiko dan rencana B
- Waktu tidak cukup | B: kerjakan secepatnya
```

Komentar: `14.00 Bimo lebih lambat dari rencana, jadi mundur 30 menit. Docker lama.`

Yang sudah baik: rencana tertulis di Issue, penanggung jawab jelas, dan perubahan dicatat dengan jam.
Yang kurang: tujuan tidak bisa diperiksa; tidak ada ukuran, cadangan waktu, atau "selesai bila"; risiko tanpa tanda
dan rencana B hanya "secepatnya"; perubahan tanpa bukti ("lebih lambat" menurut apa?) dan tanpa apa yang dikorbankan.

### Contoh 3: lemah

Tidak ada Issue rencana. Di grup WA tertulis "kami kerjakan tiket masing-masing, target selesai hari ini".
Di akhir sesi hanya dua laporan masuk; tiket C tidak dikerjakan, tanpa catatan kecuali pesan WA "kurang waktu, tiket C kami skip".

Masalahnya: tidak ada tempat bersama yang bisa dibaca pengajar, tidak ada ukuran selesai atau risiko, dan perubahan terjadi
tanpa jam, bukti, atau apa yang dikorbankan. Pengajar tidak bisa membedakan rencana yang realistis dari kelompok yang
kebetulan tidak sempat. Perbaikannya sederhana: tulis keputusan itu sebagai komentar "Perubahan" di Issue rencana.

### Pasangan perubahan: lemah dan kuat

| Situasi | Lemah | Kuat |
| --- | --- | --- |
| Tiket dibatalkan | "Kurang waktu, tiket C kami skip" | "Berubah: #5 dibatalkan. Pemicu: sampai 16.00 baru langkah ukur selesai (60 dari ±105 menit). Dikorbankan: laporan #5 tidak ada; Citra menulis temuan sementara di Issue #5 dan meninjau laporan Ayu" |
| Dugaan berubah | "Ternyata bukan database" | "Berubah: dugaan 1 gugur, diganti dugaan lapisan aplikasi; tambah langkah ambil trace (15 menit). Pemicu: pg_stat_statements 1 query per request (commit abc1234). Dikorbankan: tidak ada" |
| Anggota berhalangan | "Dayu sakit, dialihkan" | "Berubah: bagian ukur #6 dikerjakan Eka, laporan ditulis Dayu bila hadir. Pemicu: Dayu izin sakit 14.10 (pesan di grup). Dikorbankan: cadangan waktu terpakai habis, tinjau silang dibatalkan" |

## Saat melapor ke pengajar

Kalau kamu macet atau melewati titik cek, tautkan Issue rencanamu di Issue "Macet" atau komentar PR.
Pengajar akan membaca rencana, komentar, dan commit-mu lebih dulu, sehingga bantuannya tepat sasaran.
