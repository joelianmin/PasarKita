# Bagian 6: Diagnosa dan latihan

*Dari gejala ke penyebab*

> **Tujuan.** Menemukan penyebab masalah dengan metode, lalu melatihnya lewat kesalahan yang sengaja dan terkontrol.  
> **Prasyarat.** Bagian 2 dan 4. Bagian 6.2 sebaiknya dikerjakan setelah Lab 0 selesai diukur.  
> **Setelah bagian ini kamu bisa:**
> - menjalankan siklus enam langkah dan memetakan gejala ke lapisan
> - menulis catatan diagnosa dan meminta bantuan dengan bukti
> - merancang eksperimen fault injection dengan hipotesis, lalu memulihkan dan memverifikasi
> - menyusun kamus error dan matriks kegagalan

---

## 6.1 Diagnosa dan lokalisasi masalah secara sistematis

Masalah instalasi dan lingkungan hampir selalu terjadi di **satu lapisan**, tetapi gejalanya
muncul jauh di atasnya. Contoh: k6 melaporkan error, padahal penyebabnya Docker belum
menyala. Kemampuan paling berguna di sini adalah menemukan lapisan yang rusak dengan cepat
dan dengan bukti, bukan mencoba perbaikan satu per satu sampai kebetulan berhasil.

### 6.1.1 Tiga aturan dasar

1. **Akar masalah dulu, perbaikan kemudian.** Jangan mengubah apa pun sebelum kamu bisa
   menjelaskan *kenapa* gejalanya muncul. Perbaikan yang berhasil tanpa penjelasan akan
   kembali sebagai masalah baru.
2. **Satu perubahan pada satu waktu.** Kalau tiga hal diubah sekaligus, kamu tidak tahu mana
   yang membantu dan mana yang menimbulkan masalah baru.
3. **Bukti, bukan firasat.** Setiap kesimpulan harus bisa ditunjuk ke satu perintah dan
   keluarannya.

### 6.1.2 Siklus enam langkah

| Langkah | Yang dilakukan | Hasil yang diharapkan |
| --- | --- | --- |
| 1. Amati | Baca pesan error **sampai habis**. Salin kalimat persisnya, jangan diringkas dari ingatan | Teks error asli |
| 2. Reproduksi | Temukan langkah pasti yang memunculkannya. Apakah selalu terjadi? | Daftar langkah yang bisa diulang |
| 3. Lokalisasi | Persempit sampai satu lapisan (6.1.3 dan 6.1.4) | Nama lapisan yang pertama tidak sehat |
| 4. Hipotesis | Tulis satu kalimat: "Saya kira X penyebabnya karena Y" | Dugaan yang bisa dibuktikan salah |
| 5. Uji minimal | Lakukan satu perubahan terkecil yang menguji dugaan itu | Dugaan terbukti atau gugur |
| 6. Verifikasi dan catat | Ulangi langkah 2. Sudah beres? Catat penyebab dan perbaikannya | Masalah selesai dan terdokumentasi |

**Jika tiga dugaan berturut-turut gagal, berhenti.** Kembali ke langkah 1 dan periksa
asumsimu: lapisan yang kamu anggap sehat, jangan-jangan belum pernah kamu buktikan. Bila
masih buntu, minta bantuan dengan membawa catatan diagnosa (6.1.8).

### 6.1.3 Peta lapisan: dari bawah ke atas

Setiap lapisan bergantung pada yang sebelumnya. Periksa **berurutan** dan berhenti di lapisan
pertama yang tidak sehat. Memperbaiki lapisan atas sementara lapisan bawah rusak hanya
membuang waktu.

```text
 1 Alat      2 Docker     3 Container   4 Port       5 Database
 terpasang → berjalan  →  sehat      →  terjangkau → terisi
                                                       │
 10 Dashboard ← 9 Beban k6 ← 8 Layanan  ← 7 Aplikasi ←─┘ 6 Skema
   metrik        berjalan      tiruan       hidup          dan seed
```

| Lapisan | Pertanyaan | Perintah | Hasil sehat |
| --- | --- | --- | --- |
| 1 Alat terpasang | Semua alat ada di PATH dan versinya benar? | `task doctor`; atau `java -version`, `docker --version`, `k6 version`, `task --version` | Java 21+, semua alat menjawab |
| 2 Docker berjalan | Daemon Docker aktif? | `docker info` | Tidak ada "Cannot connect to the Docker daemon" |
| 3 Container sehat | Container yang dibutuhkan hidup? | `docker ps --format "table {{.Names}}\t{{.Status}}"` | `pasarkita-postgres-1 ... (healthy)`, dan container lain berstatus `Up` |
| 4 Port terjangkau | Port tidak bentrok dan terpetakan? | `task doctor` (daftar port); Windows: `netstat -ano \| findstr :5432`; macOS/Linux: `lsof -i :5432` | Port dipegang oleh container yang benar, bukan program lain |
| 5 Database menjawab | Bisa masuk ke database? | `task psql`, lalu `select 1;` | Mengembalikan `1` |
| 6 Skema dan seed | Migrasi dan data sesuai skala? | di `psql`: `select version, success from flyway_schema_history;` lalu `select count(*) from orders;` | `success = t`; jumlah order sekitar 50.000 (skala S) atau 1.000.000 (skala M) |
| 7 Aplikasi hidup | Aplikasi start dan sehat? | `curl http://localhost:8080/actuator/health` | `{"status":"UP"}` |
| 8 Layanan tiruan | WireMock menjawab? | `curl "http://localhost:8089/tax/rate?productId=1"` | Respons JSON, bukan error koneksi |
| 9 Beban berjalan | Satu request manual berhasil sebelum k6? | `curl -s -o /dev/null -w "%{http_code} %{time_total}\n" http://localhost:8080/api/products/1` | `200` dan waktu wajar |
| 10 Dashboard | Prometheus menarik metrik? | Buka http://localhost:9090/targets | Target `pasarkita` berstatus `UP` |

Di Windows PowerShell 5.1 gunakan `curl.exe`. Tanda `\|` pada tabel hanyalah pipa `|`.

### 6.1.4 Teknik lokalisasi

- **Bagi dua (bisect).** Rantai panjang? Uji titik tengahnya. Jika lapisan 5 sehat, masalah
  ada di lapisan 6 ke atas; jika tidak, di lapisan 5 ke bawah. Dua atau tiga uji sudah
  mempersempit banyak.
- **Isolasi.** Jalankan satu komponen tanpa yang lain. Panggil WireMock langsung dengan
  `curl` tanpa aplikasi. Buka `psql` tanpa aplikasi. Jika komponen itu sehat sendirian,
  masalahnya ada di sambungan antar komponen: alamat, port, kredensial, atau urutan start.
- **Bandingkan yang jalan dan yang tidak.** Bandingkan keluaran `task doctor` laptopmu dengan
  teman yang berhasil. Daftar setiap perbedaan, sekecil apa pun: versi, sistem operasi,
  folder, variabel lingkungan. Satu per satu samakan.
- **Lacak mundur.** Pada stack trace Java, jangan berhenti di baris pertama. Cari baris
  `Caused by:` **yang paling bawah**; itu penyebab terdalam. Lalu tanyakan: dari mana nilai
  atau alamat itu berasal (konfigurasi, variabel lingkungan, argumen)?
- **Lihat apa yang berubah.** Kapan terakhir kali berhasil, dan apa yang berbeda sejak itu?
  `git status`, `git diff`, `git log -5`; update Docker, JDK, atau Windows; mengubah
  `group.env`, `compose.yaml`, atau `application.yml`.
- **Pasang alat ukur di perbatasan komponen.** Jika data masuk lapisan A tetapi tidak
  keluar dari lapisan B, masalahnya di antara keduanya. Log, trace, dan metrik (bagian 4.1)
  adalah alat ukur itu.
- **Reproduksi minimal.** Kecilkan masalah sampai tersisa satu perintah yang gagal. Misalnya
  bukan "lab 0 gagal", tetapi "`task seed SCALE=S` berhenti di migrasi".
- **Jelaskan dengan kata-kata.** Menuliskan masalah secara runtut sering langsung
  memperlihatkan langkah yang terlewat.

### 6.1.5 Dari gejala ke lapisan

| Pesan atau gejala | Artinya | Lapisan | Cek berikutnya |
| --- | --- | --- | --- |
| `command not found` atau `... is not recognized` | Alat tidak ada di PATH | 1 | Tutup lalu buka terminal baru setelah instalasi; `where <alat>` (Windows) atau `which <alat>` |
| `invalid target release: 21`, `UnsupportedClassVersionError` | JDK lebih lama dari 21 | 1 | `java -version` **dan** `javac -version`; periksa `JAVA_HOME` |
| `Cannot connect to the Docker daemon` | Docker Desktop mati | 2 | Buka Docker Desktop, tunggu ikon stabil, ulangi `docker info` |
| Container `Restarting` atau `Exited (137)` | Dimatikan karena kehabisan memori | 3 | `docker logs <nama> --tail 50`; naikkan memori Docker |
| `port is already allocated`, `address already in use` | Port dipakai program lain | 4 | Cari pemiliknya (perintah lapisan 4), hentikan, ulangi `task up` |
| `Connection refused` | Tidak ada yang mendengarkan di alamat itu: proses mati, belum siap, atau port salah | 3, 4, atau 7 | Siapa yang seharusnya mendengarkan? Cek lapisan itu |
| `password authentication failed` | Kredensial tidak cocok | 5 | Bandingkan `application.yml` dengan `compose.yaml` |
| `relation "orders" does not exist` | Migrasi belum berjalan | 6 | `task migrate` atau `task seed` |
| `Validate failed` atau `checksum mismatch` (Flyway) | File migrasi diubah setelah dijalankan | 6 | `git diff` pada folder `db/migration`; kembalikan filenya |
| Banyak `404` di hasil k6 | Skala seed tidak cocok dengan script | 6 dan 9 | `select count(*) from product;` bandingkan dengan nilai default script |
| Banyak `402` atau `409` | Saldo atau stok sudah terpakai | 6 | Ini data, bukan bug; `task reset` lalu seed ulang |
| `/actuator/health` berstatus `DOWN` | Salah satu dependensi aplikasi, biasanya database, tidak sehat | 5 | Ulangi cek lapisan 5 |
| Target Prometheus `DOWN` | Prometheus tidak bisa menjangkau aplikasi | 7 atau 10 | Aplikasi hidup? Jalankan `task app` |
| Grafana kosong | Rentang waktu (UTC) salah, atau tidak ada data | 10 | Ubah rentang waktu; periksa lapisan 10 |
| `task test` gagal pada Testcontainers | Test tidak menemukan Docker | 2 | `docker info` |
| `Filename too long` (Windows) | Path terlalu panjang | 1 | `git config --global core.longpaths true`; pindahkan repo ke path pendek |
| Unduhan Maven atau `docker pull` macet | Jaringan, proxy, atau DNS | 1 | `curl -I https://repo.maven.apache.org`; tanyakan pengaturan proxy kampus |

### 6.1.6 Masalah khusus platform

- **PATH tidak diperbarui.** Terminal yang dibuka sebelum instalasi tidak mengenal alat baru.
  Tutup dan buka lagi sebelum menyimpulkan instalasi gagal.
- **Beberapa versi Java.** `java -version` dan `javac -version` bisa berbeda. Jalankan
  keduanya. Cek juga `JAVA_HOME`; Maven memakainya.
- **Docker Desktop di Windows (WSL2).** Pastikan virtualisasi aktif di BIOS, `wsl --status`
  menunjukkan WSL2, dan Docker Desktop memakai backend WSL2. Memori diatur di `.wslconfig`.
- **Akhir baris (CRLF/LF).** Skrip yang rusak setelah di-clone di Windows sering karena
  konversi akhir baris. Repo memiliki `.gitattributes` untuk itu; jangan dinonaktifkan.
- **Antivirus.** Pemindaian folder repo dan `.m2` memperlambat build secara drastis.
- **Ruang disk.** Image Docker dan data database besar (skala M). `docker system df`
  menunjukkan pemakaiannya.

### 6.1.7 "Restart" dan "reset" bukan diagnosa

Menyalakan ulang sering berhasil, tetapi tidak memberi tahu *kenapa*. Perlakukan sebagai
**uji hipotesis**, bukan kebiasaan:

- Tulis dugaannya dulu ("container postgres macet").
- `task down` lalu `task up` aman; data database tetap ada.
- `task reset` **menghapus semua data**. Pakai hanya jika kamu tahu data memang perlu
  dibuang, misalnya setelah run k6 yang menghabiskan stok. Jangan pakai sebagai pertolongan
  pertama.
- Sebelum menyalakan ulang, simpan bukti dulu (`docker logs`, tangkapan layar). Setelah
  restart, bukti itu hilang.

### 6.1.8 Mendiagnosa performa dengan cara yang sama

Pertanyaan "kenapa lambat?" dijawab dengan menyempitkan dari luar ke dalam. Berhenti di
lapisan pertama yang menunjukkan di mana waktu habis.

| Pertanyaan | Alat | Yang dilihat |
| --- | --- | --- |
| Seberapa lambat menurut klien? | k6 | p50, p95, p99, error, `dropped_iterations` |
| Seberapa lambat menurut server? | Grafana atau Prometheus (`http.server.requests`) | Selisih dengan klien menunjukkan antrean atau jaringan |
| Langkah mana yang makan waktu? | Trace di Tempo | Span terpanjang dalam satu request |
| Query mana yang lambat atau terlalu sering? | `pg_stat_statements` | `calls`, `total_exec_time`, `mean_exec_time` |
| Kenapa query itu lambat? | `EXPLAIN (ANALYZE, BUFFERS)` | `Seq Scan`, baris yang dibaca, waktu aktual |
| Apakah sumber daya penuh? | Grafana (CPU, memori, pool koneksi) | Saturasi |

Aturan yang sama berlaku: satu hipotesis, satu perubahan, ukur lagi dengan kondisi yang
sama. Perubahan yang tidak diukur ulang bukan perbaikan.

### 6.1.9 Catatan diagnosa dan cara meminta bantuan

Tulis catatan ringkas selagi bekerja. Catatan yang sama menjadi bahan laporan masalah
yang baik, karena orang yang membantu tidak perlu menebak.

```text
Gejala        : pesan error persis (salin, jangan diparafrasekan)
Lingkungan    : sistem operasi, keluaran `task doctor`, versi alat terkait
Langkah       : perintah yang menimbulkan masalah, berurutan
Terjadi       : selalu / kadang-kadang
Yang berubah  : apa yang berbeda sejak terakhir berhasil
Lapisan sehat : lapisan 1..N yang sudah dibuktikan, dengan perintah dan keluarannya
Hipotesis     : satu kalimat
Uji           : perubahan yang dicoba dan hasilnya
Kesimpulan    : penyebab dan perbaikan
```

Contoh pertanyaan yang buruk: "Aplikasinya error, tolong." Contoh yang baik: "`task app`
gagal dengan `Connection refused ... localhost:5432`. `docker ps` menampilkan
`pasarkita-postgres-1 Up (healthy)`, tetapi `lsof -i :5432` menunjukkan proses lain
memegang port itu. Saya mengira bentrok port dan akan menghentikan proses tersebut."

### 6.1.10 Latihan

Lakukan di lingkungan lokalmu sendiri, satu per satu, lalu pulihkan. Untuk setiap
skenario, catat gejala, lapisan, dan bukti (bukan hanya "sudah beres"). Bagian 6.2
memperluas latihan ini menjadi eksperimen yang lengkap dengan hipotesis.

1. Matikan Docker Desktop, lalu jalankan `task seed`. Pesan apa yang muncul, dan di
   lapisan mana?
2. Dengan aplikasi berjalan, hentikan container PostgreSQL. Bagaimana `/actuator/health`
   dan satu panggilan `GET /api/products/1` berubah?
3. Jalankan `task app` sebelum `task up`. Baca stack trace, temukan `Caused by` terdalam.
4. Jalankan program lain di port 8080 (atau 5432) lalu coba start. Cari pemilik port itu
   dengan perintah lapisan 4.
5. Hentikan container WireMock, lalu lakukan satu checkout lewat `curl`. Di mana kegagalan
   muncul, dan apa yang terjadi pada stok dan saldo?
6. Seed skala S, lalu jalankan k6 Lab 0 tanpa mengubah parameter. Cari penyebab error
   tinggi hanya dengan bukti (k6, log, dan `select count(*)`).

> Cek diri: sebuah teman bilang "k6 gagal terus". Sebutkan lima pertanyaan, berurutan, yang
> kamu ajukan untuk menemukan lapisan yang rusak sebelum menyentuh script k6.

---

## 6.2 Belajar dari error: sengaja membuat kesalahan

Cara tercepat mengenali sebuah error adalah pernah melihatnya terjadi, lengkap dengan
penyebab yang kamu ketahui. Bagian ini mengajarkan cara **merusak sistem dengan sengaja,
terkontrol, dan terukur**, lalu mendiagnosanya dengan metode bagian 6.1. Hasilnya adalah
"kamus error" milikmu sendiri.

### 6.2.1 Istilah yang lazim

Praktik ini punya beberapa nama, tergantung konteksnya. Dalam bahasa Inggris, istilah yang
paling umum di industri adalah **fault injection** dan **chaos engineering**.

| Istilah | Arti | Kegunaan di sini |
| --- | --- | --- |
| **Fault injection** (injeksi kesalahan) | Sengaja memasukkan kesalahan ke sistem untuk mengamati perilakunya. Istilah payung | Seluruh bagian ini |
| **Chaos engineering** | Eksperimen terkontrol pada sistem untuk menguji ketahanannya. Selalu diawali hipotesis tentang kondisi normal (*steady state*). Dipopulerkan Netflix lewat *Chaos Monkey* | Siklus eksperimen di 6.2.3 |
| **Chaos experiment** | Satu percobaan: hipotesis, kesalahan yang disuntik, hasil pengamatan | Satu baris latihan |
| **Game day** | Sesi latihan terjadwal: tim menyimulasikan kegagalan lalu menanganinya bersama | Latihan berpasangan di 6.2.8 |
| **Break/fix exercise** | Istilah pendidikan: satu orang merusak, yang lain memperbaiki | Latihan berpasangan di 6.2.8 |
| **Negative testing** / error-path testing | Menguji dengan input atau kondisi yang tidak valid | Bagian "aplikasi" di 6.2.4 |
| **Resilience testing** / failure testing | Menguji apakah sistem bertahan atau pulih saat komponen gagal | Pemakaian umum fault injection |
| **FMEA** (Failure Mode and Effects Analysis) | Daftar tertulis: komponen mana bisa gagal, bagaimana, dampaknya, dan cara mendeteksinya | Matriks kegagalan di 6.2.5 |
| **Pre-mortem** | Membayangkan sistem sudah gagal, lalu menuliskan penyebabnya sebelum mulai | Langkah awal sebelum eksperimen |
| **Fuzzing** | Mengirim input acak atau cacat secara otomatis untuk mencari kegagalan | Gagasan lanjutan (6.2.7) |

Jangan tertukar dengan dua istilah yang mirip tetapi berbeda: **mutation testing** (mengubah
kode sengaja untuk menguji apakah test-nya menangkapnya) dan **bug seeding** (menanam
cacat untuk memperkirakan sisa cacat). Keduanya menyasar kualitas test, bukan perilaku
sistem saat gagal.

### 6.2.2 Aturan aman

1. **Hanya di lingkungan lokalmu sendiri.** Tidak pernah di komputer, server, atau layanan
   milik orang lain.
2. **Bukan saat mengukur Lab 0.** Kesalahan yang disuntik merusak baseline. Selesaikan
   pengukuran dulu, atau kerjakan di cabang terpisah (misalnya `latihan-error`).
3. **Satu kesalahan pada satu waktu.** Kalau dua dipasang bersamaan, kamu tidak tahu mana
   yang menimbulkan gejala.
4. **Tulis hipotesis sebelum menyuntik.** Menebak setelah melihat hasil bukan eksperimen.
5. **Batasi dampak (*blast radius*).** Mulai dari kesalahan kecil dan mudah dipulihkan.
6. **Siapkan jalan pulang.** Sebelum mulai: `git status` bersih (atau perubahan sudah
   di-commit) dan kamu tahu persis perintah pemulihannya.
7. **Pulihkan, lalu verifikasi.** Sistem harus kembali ke kondisi normal yang dibuktikan
   dengan perintah (lapisan 1 sampai 10 di 6.1.3), bukan dianggap pulih.
8. **Jangan membawa kesalahan ke bukti.** `task evidence` menandai bukti tidak sah bila ada
   perubahan yang belum di-commit. Kembalikan semua file yang kamu ubah (`git restore`).

### 6.2.3 Siklus satu eksperimen

| Langkah | Yang dilakukan | Contoh |
| --- | --- | --- |
| 1. Kondisi normal | Ukur dulu keadaan sehat | `/actuator/health` = `UP`, `GET /api/products/1` = 200 dalam waktu wajar |
| 2. Hipotesis | Tulis "Jika X, maka Y" | "Jika PostgreSQL dihentikan, `GET /api/products/1` langsung 500 dan health `DOWN`" |
| 3. Suntik | Lakukan **satu** kesalahan | `docker stop` container PostgreSQL |
| 4. Amati | Lihat gejala di tiap lapisan: respons, log, health, Grafana, trace | Catat apa yang berubah dan kapan |
| 5. Bandingkan | Cocok dengan hipotesis? Jelaskan selisihnya | "Saya kira langsung gagal, ternyata menggantung 30 detik. Kenapa?" |
| 6. Pulihkan | Kembalikan, lalu uji ulang kondisi normal | `docker start`, ulangi langkah 1 |
| 7. Catat | Masukkan ke kamus error (6.2.6) | Pesan, lapisan, penyebab, cara memastikan |

Setelah mengamati, **diagnosa seolah-olah kamu tidak tahu penyebabnya**. Ikuti peta lapisan
6.1.3 dan lihat apakah metode itu benar-benar membawamu ke penyebab yang kamu pasang.
Kalau tidak, ada langkah diagnosa yang perlu diperbaiki.

### 6.2.4 Katalog kesalahan

Nama container didapat dari `docker ps` (biasanya berbentuk `pasarkita-<layanan>-1`).
Di bash, variabel lingkungan ditulis `NAMA=nilai perintah`. Di PowerShell:
`$env:NAMA="nilai"; perintah`, lalu hapus dengan `Remove-Item Env:NAMA`.

**Lapisan 1–2: alat dan Docker**

| Kesalahan | Cara menyuntik | Pulihkan | Pertanyaan |
| --- | --- | --- | --- |
| Versi Java salah | Arahkan `JAVA_HOME` ke JDK lama di satu terminal, lalu `task app` | Buka terminal baru | Pesan apa yang muncul dan di tahap mana (kompilasi atau berjalan)? |
| Docker mati | Tutup Docker Desktop, lalu `task up` | Buka Docker Desktop, tunggu siap | Bedakan pesan ini dengan "container mati" |

**Lapisan 3–4: container dan port**

| Kesalahan | Cara menyuntik | Pulihkan | Pertanyaan |
| --- | --- | --- | --- |
| Container berhenti | `docker stop <postgres>` | `docker start <postgres>` | Pesan apa di aplikasi? Berapa lama sampai gagal? |
| Container **membeku** | `docker pause <postgres>` | `docker unpause <postgres>` | Apa bedanya dengan `stop`? Mana yang lebih cepat terdeteksi, dan kenapa? |
| Port bentrok | Jalankan `task app` di dua terminal | Hentikan yang kedua | Baris mana di log yang menyebut penyebabnya? |
| Memori terlalu kecil | Turunkan memori Docker/WSL, jalankan stack | Kembalikan memori | Container mana yang mati lebih dulu dan apa kode keluarnya? |

**Lapisan 5–6: database**

| Kesalahan | Cara menyuntik | Pulihkan | Pertanyaan |
| --- | --- | --- | --- |
| Password salah | `SPRING_DATASOURCE_PASSWORD=salah task app` | Hapus variabel | Pesan error dan `Caused by` terdalam? |
| Alamat salah | `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5999/pasarkita task app` | Hapus variabel | Beda dengan password salah? |
| Database kosong | `task reset`, `task up`, lalu `task app` | `task seed SCALE=S` | **Prediksi dulu**: akankah ada error? (Petunjuk: aplikasi membawa Flyway sendiri.) Apa respons `GET /api/products`? |
| Migrasi diubah | Tambahkan spasi di `V1__skema_awal.sql` setelah pernah dijalankan, lalu `task app` | `git restore` file itu | Apa arti *checksum mismatch*? Kenapa aturannya begitu? |
| Koneksi diputus paksa | Saat k6 berjalan: di `task psql`, jalankan `select pg_terminate_backend(pid) from pg_stat_activity where datname='pasarkita' and pid <> pg_backend_pid();` | Tidak perlu | Apa yang terjadi pada request yang sedang jalan? Apakah pool pulih sendiri? |
| Tabel terkunci | Di `task psql`: `begin; lock table stock in access exclusive mode;` (jangan tutup sesi), lalu lakukan checkout dengan `curl` | Ketik `rollback;` | Apa yang terlihat dari sisi `curl`? Cari pelakunya: `select pid, state, wait_event_type, wait_event, left(query,60) from pg_stat_activity where datname='pasarkita';` |

**Lapisan 7: aplikasi (negative testing)**

| Kesalahan | Cara menyuntik | Pertanyaan |
| --- | --- | --- |
| Data tidak ada | `GET /api/products/99999999` | Status code? Pesan? |
| Tipe salah | `GET /api/products/abc` | 400 atau 500? Apa bedanya buat klien? |
| JSON rusak | `POST /api/auth/login` dengan body `{` | Status code dan isi respons? |
| Field kosong | `POST /api/cart/items` dengan body `{}` | Apakah ditolak? Apa yang tersimpan? |
| Angka tidak masuk akal | `POST /api/cart/items` dengan `quantity` `0` atau `-1` | Apakah ada yang menolak? Apa dampaknya pada checkout? |
| Header hilang | `POST /api/checkout` tanpa `X-Customer-Id` | Status code? |
| Keranjang kosong | `POST /api/checkout` untuk pelanggan tanpa isi keranjang | Status code dan pesan? |
| Identitas lain | Ganti `X-Customer-Id` ke pelanggan lain | Apa yang mencegah atau tidak mencegahnya? (OWASP A01, bagian 5.3) |

**Lapisan 8: layanan tiruan (WireMock)**

Pemetaan ada di `infra/compose/wiremock/mappings/*.json` dan dimuat saat WireMock menyala.
Ubah file di host, lalu `docker restart <wiremock>`. Pulihkan dengan `git restore` pada file
itu dan restart lagi.

| Kesalahan | Cara menyuntik | Pertanyaan |
| --- | --- | --- |
| Layanan mati | `docker stop <wiremock>` lalu checkout | Di langkah mana checkout gagal? Apa yang terjadi pada stok dan saldo? |
| Layanan lambat | Tambahkan `"fixedDelayMilliseconds": 3000` pada `response` di `payment.json` | Bagaimana latensi checkout berubah? Apa yang terjadi dengan beban yang naik? |
| Koneksi diputus | Tambahkan `"fault": "CONNECTION_RESET_BY_PEER"` pada `response` | Pesan di aplikasi? Beda dengan layanan mati? |
| Respons kosong | Gunakan `"fault": "EMPTY_RESPONSE"` | Apakah aplikasi menganggapnya gagal atau sukses? |
| Respons tak terduga | Ubah isi `tax.json` menjadi `{}` | Apakah ada error? Apakah hasilnya masih benar? (ISO 25010: *functional correctness*) |

**Lapisan 9: k6**

| Kesalahan | Cara menyuntik | Pertanyaan |
| --- | --- | --- |
| Alamat salah | `-e BASE_URL=http://localhost:9999` | Berapa persen gagal, dan bagaimana k6 menandainya? |
| Skala tidak cocok | Seed skala S, jalankan script Lab 0 tanpa parameter | Dari mana asal sebagian besar error? Bagaimana membuktikannya? |
| Aplikasi tidak jalan | Jalankan k6 tanpa `task app` | Beda dengan alamat salah? |

**Lapisan 10: observability**

| Kesalahan | Cara menyuntik | Pertanyaan |
| --- | --- | --- |
| Tempo mati | `docker stop <tempo>`, lalu panggil API | **Prediksi dulu**: apakah API terpengaruh? Apa yang muncul di log? Prinsip apa yang kamu harapkan? |
| Prometheus mati | `docker stop <prometheus>` | Apa yang hilang di Grafana, dan apa yang tetap jalan? |
| Aplikasi mati | Hentikan `task app` | Bagaimana target Prometheus berubah, dan berapa lama sampai terlihat? |

### 6.2.5 Matriks kegagalan (versi ringkas FMEA)

Setelah beberapa eksperimen, rangkum dalam tabel. Ini juga bahan untuk `BASELINE.md`.

| Komponen | Mode kegagalan | Dampak ke pengguna | Cara mendeteksi | Cara pulih |
| --- | --- | --- | --- | --- |
| PostgreSQL | Berhenti | Semua endpoint 500 | `/actuator/health` `DOWN`, log `Connection refused` | `docker start`, cek health |
| (isi sendiri) | | | | |

Dari tabel ini kamu bisa melihat kegagalan mana yang **tidak terdeteksi** oleh alat ukur
yang ada. Itulah celah observability.

### 6.2.6 Kamus error pribadi

Setiap eksperimen menghasilkan satu entri. Dalam beberapa minggu, kamu punya daftar yang
lebih berguna daripada mesin pencari.

| Pesan atau gejala | Lapisan | Penyebab yang dipastikan | Cara memastikan | Pemulihan |
| --- | --- | --- | --- | --- |
| `Connection to localhost:5432 refused` | 3 atau 5 | Container PostgreSQL berhenti | `docker ps` | `docker start` |

Catatan penting: error hasil simulasi **bukan** salinan penuh dari error nyata. Penyebab
nyata sering lebih berantakan dan bercampur. Tujuan latihan ini melatih pengenalan pola
dan disiplin diagnosa, bukan menghafal jawaban.

### 6.2.7 Gagasan lanjutan (tidak dipasang di repo ini)

- **Toxiproxy**: proxy TCP untuk menambah latensi, memutus koneksi, atau membatasi
  bandwidth secara terprogram tanpa menyentuh komponen aslinya.
- **Chaos Monkey for Spring Boot**: menyuntik latensi dan exception pada bean Spring.
- **Pumba**, **Chaos Mesh**, **LitmusChaos**: alat chaos untuk container dan Kubernetes.
- **Fuzzing** endpoint dengan input acak untuk mencari respons 500.

Semua ini baru bermanfaat bila kamu sudah nyaman dengan eksperimen manual di atas.

### 6.2.8 Latihan berpasangan (break/fix atau game day)

Waktu yang disarankan: sekitar 45 menit per sesi.

1. **Pembuat masalah** memilih satu kesalahan dari 6.2.4 **tanpa memberi tahu** rekannya,
   menyuntiknya, lalu hanya mengatakan gejala yang terlihat pengguna ("checkout gagal").
2. **Pendiagnosa** memakai metode bagian 6.1 dan menulis catatan diagnosa (6.1.9). Boleh
   bertanya ya/tidak kepada pembuat masalah, tetapi tidak boleh meminta petunjuk langsung.
3. Setelah penyebab ditemukan, keduanya membandingkan: lapisan mana yang paling lama
   dicurigai? Bukti apa yang menyesatkan?
4. Pulihkan, verifikasi 10 lapisan, tukar peran.

> Cek diri: kenapa `docker pause` pada database bisa lebih berbahaya bagi aplikasi daripada
> `docker stop`? Hubungkan jawabanmu dengan timeout dan connection pool (bagian 3.1).

---

*Sebelumnya: [Bagian 5](05-mutu-dan-desain.md) · [Daftar isi](../MATERI-DASAR.md) · Berikutnya: [Bagian 7](07-menyerahkan-hasil.md)*
