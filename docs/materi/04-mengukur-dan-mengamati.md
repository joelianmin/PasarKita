# Bagian 4: Mengukur dan mengamati

*Observability dan k6*

> **Tujuan.** Mengumpulkan angka yang bisa dipercaya dan membacanya dari sisi klien, server, dan database.  
> **Prasyarat.** Bagian 1, 2, dan 3.  
> **Setelah bagian ini kamu bisa:**
> - membedakan metrik, trace, dan log serta memakai Grafana, Prometheus, dan Tempo
> - membaca script k6 dan hasilnya, termasuk `dropped_iterations` dan threshold
> - menjalankan pengukuran berulang dengan kondisi yang sama
> - mengenali dan menyesuaikan zona waktu di berbagai alat (UTC dan WIB)

---

## 4.1 Observability: metrik, trace, log

Tiga sumber data, tiga pertanyaan:

| Sumber | Pertanyaan | Alat di repo |
| --- | --- | --- |
| **Metrik** (angka dari waktu ke waktu) | Seberapa sering, seberapa lambat, seberapa penuh? | Actuator, Micrometer, Prometheus, Grafana |
| **Trace** (jejak satu request lintas langkah) | Waktu habis di langkah mana? | OpenTelemetry, Tempo, Grafana |
| **Log** (catatan kejadian) | Apa yang terjadi, dan kenapa? | Log aplikasi, log SQL (profil `local`) |

**Alur data**

- Aplikasi mengekspos metrik di `/actuator/prometheus`. Prometheus menariknya tiap 5 detik.
- Aplikasi mengirim trace ke Tempo lewat OTLP (port 4318). Sampling 100%, jadi semua
  request tercatat.
- Grafana membaca Prometheus dan Tempo. Dashboard "PasarKita Overview" ada di
  http://localhost:3000.

**Cara membaca dashboard.** Kerangka sederhana "RED" untuk layanan: **R**ate (laju
request), **E**rrors (error), **D**uration (latensi). Untuk sumber daya, kerangka "USE":
**U**tilization, **S**aturation, **E**rrors (CPU, memori, pool koneksi).

**Kebiasaan penting.**

- Samakan rentang waktu grafik dengan waktu run k6.
- Dashboard memakai zona waktu **UTC**. Kalau grafik terlihat kosong, periksa dulu
  rentang waktunya dan baca 4.3 tentang zona waktu.
- Log yang sama berulang (misalnya peringatan lama) belum tentu penyebab masalah. Belajar
  menyaring *noise* termasuk keterampilan yang dinilai.
- Log SQL pada profil `local` mencetak nilai parameter yang bisa memuat data pribadi.
  Nyalakan hanya saat mengukur dan jangan pernah di produksi (OWASP A09). Metrik dan
  trace tidak boleh memuat email, token, atau password.

> Cek diri: grafik menunjukkan latensi naik pukul 10.05. Metrik, trace, dan log masing-
> masing bisa menjawab apa dari kejadian itu?

---

## 4.2 k6

k6 adalah alat uji beban yang script-nya ditulis dalam JavaScript.

**Anatomi script** (`labs/0/reproduce.k6.js`)

- `options.scenarios`: berapa laju beban dan berapa lama. `constant-arrival-rate` dengan
  `rate: 10` dan `timeUnit: '1s'` berarti 10 iterasi per detik.
- `preAllocatedVUs` dan `maxVUs`: jumlah "pengguna virtual" yang disiapkan. Kalau k6
  melaporkan *dropped iterations*, berarti `maxVUs` kurang atau server terlalu lambat.
- Fungsi `export function catalog()` dan sejenisnya adalah isi tiap skenario.
- `tags: { endpoint: 'catalog' }` memberi label agar hasil bisa dipisah per endpoint.
- `check(...)` memeriksa isi respons; kegagalannya dilaporkan terpisah dari status HTTP.
- `handleSummary` menulis `labs/0/out/baseline-table.md` dan `summary.json`.

**Membaca hasil**

- `http_req_duration`: latensi. Lihat `med` (p50), `p(95)`, `p(99)`.
- `http_req_failed`: persentase request gagal.
- `dropped_iterations`: beban yang tidak sempat dikirim karena server tidak mengimbangi.
- `iterations` dan `http_reqs`: jumlah yang sebenarnya terkirim.
- `thresholds`: syarat lulus atau gagal, misalnya `'http_req_duration': ['p(95)<500']`.
  Inilah cara menyatakan target mutu (bagian 5.2) agar bisa diperiksa mesin. Script Lab 0
  sengaja memakai threshold longgar karena tugasnya mengukur, bukan menguji.
- Arahkan beban k6 hanya ke sistem milikmu atau yang diizinkan secara tertulis. Beban besar
  ke sistem orang lain bisa dianggap serangan DoS.

**Tiga script**

| Script | Dipakai untuk |
| --- | --- |
| `labs/0/reproduce.k6.js` | Baseline 8 endpoint (Lab 0) |
| `labs/latihan/profile.k6.js` | Beban untuk investigasi tiket |
| `traffic/simulator.k6.js` | Traffic latar belakang, berjalan lama |

**Jebakan umum**

- Skala data harus cocok dengan script. Seed `S` dengan script bawaan skala `M` membuat
  banyak 404 (lihat `labs/0/LAB.md`).
- Checkout mengubah stok dan saldo; hasil run berikutnya bisa bergeser.
- Jalankan k6 dari terminal terpisah dan **jangan menjalankan hal berat lain** saat
  mengukur, karena laptop yang sibuk ikut memengaruhi hasil.

> Cek diri: apa bedanya `rate: 10` pada `constant-arrival-rate` dengan 10 VU yang
> berulang-ulang pada executor tertutup, ketika server mendadak 5 kali lebih lambat?

---

## 4.3 Zona waktu: kesadaran dan penyesuaian

Waktu yang sama bisa tertulis dengan jam berbeda di tiap alat. Di Indonesia barat (WIB),
selisihnya **+7 jam dari UTC**: 09.00 WIB sama dengan 02.00 UTC, pada hari yang sama. Antara
00.00 dan 06.59 WIB, **tanggal UTC lebih awal satu hari**. Ini sumber salah baca yang paling
sering saat mencocokkan grafik, log, dan hasil k6.

**Kesadaran: tiap alat punya aturan sendiri**

| Tempat | Zona yang biasa tampil | Catatan |
| --- | --- | --- |
| Dashboard Grafana "PasarKita Overview" | UTC (disetel di JSON dashboard) | Disengaja, supaya semua orang melihat jam yang sama |
| Prometheus, Tempo | UTC | Simpan dan tampilkan waktu dalam UTC |
| Log aplikasi dan JVM | Zona sistem tempat proses jalan | Di container biasanya UTC, di laptop biasanya WIB |
| PostgreSQL | Zona sesi (`SHOW TimeZone;`) | `timestamptz` ditampilkan sesuai zona sesi |
| GitHub (commit, PR, komentar) | Zona browser kamu | Riwayat `git log` memuat offset zona penulisnya |
| Hasil k6 dan berkas bukti | Periksa cap waktunya | Cari akhiran `Z` (UTC) atau `+07:00` |

**Aturan pegangan.** Setiap kali menulis atau membaca sebuah jam, tanyakan zonanya. Di
laporan, tulis keduanya: `09.15 WIB (02.15 UTC)`.

### Cara menyesuaikan

| Tujuan | Cara |
| --- | --- |
| Melihat dashboard dalam WIB, hanya untukmu | Di Grafana, pilih zona waktu di panel pemilih waktu (kanan atas), atau di profil pengguna |
| Mengubah bawaan dashboard | Di `infra/compose/grafana/dashboards/pasarkita-overview.json`, ubah `"timezone": "utc"` menjadi `"browser"` atau `"Asia/Jakarta"`, lalu buat ulang Grafana |
| Melihat jam WIB di PostgreSQL | `SET TIME ZONE 'Asia/Jakarta';` atau `SELECT now() AT TIME ZONE 'Asia/Jakarta';` |
| Menyamakan zona log aplikasi | Opsi JVM `-Duser.timezone=UTC` (atau `Asia/Jakarta`), atau variabel `TZ` pada container |
| Memeriksa jam sistemmu | `date` dan `date -u` (bandingkan), `SHOW TimeZone;` untuk database |
| Mengubah cepat di terminal | `date -u -d '2026-11-06 09:15 +0700'` di Linux; di macOS `date -u -j -f '%Y-%m-%d %H:%M %z' '2026-11-06 09:15 +0700' '+%H:%M'` |

Setelah mengubah setelan Grafana atau JVM, catat di `BASELINE.md` apa yang kamu ubah.
Itu perubahan lingkungan pengukuran.

### Pilihan zona: untung dan ruginya

| Pilihan | Keuntungan | Kerugian |
| --- | --- | --- |
| **Semua UTC** (bawaan lab) | Tidak ambigu, tidak ada waktu musim panas, sama di tiap laptop dan server, perbandingan antarkelompok mudah | Perlu menghitung +7 jam di kepala, dan tanggal bisa terlihat mundur satu hari |
| **Semua WIB** | Cocok dengan jam dinding kita, mudah dihubungkan dengan "tadi pagi" | Tak berlaku bagi anggota di zona lain, serta berbeda dari Prometheus, Tempo, dan banyak log server; mudah tercampur dengan UTC tanpa terasa |
| **Mengikuti browser** | Tiap orang melihat jamnya sendiri | Dua orang melihat dasbor yang sama dengan jam berbeda, tangkapan layar menjadi ambigu |
| **Campuran (UTC di sistem, WIB di tampilan)** | Sistem tetap konsisten, manusia tetap nyaman | Mudah lupa mengonversi, harus selalu menulis zona di laporan |

**Saran untuk lab.** Biarkan sistem dan dashboard di UTC, lihat dan tulis jam dengan kedua
zona di laporan. Alasannya: tiket dan bukti diperiksa orang lain, dan zona yang tidak
ditulis adalah sumber perselisihan yang sulit dilacak. Melatih diri mengonversi juga berguna
saat bekerja dengan server atau tim di zona lain.

**Kesalahan yang sering terjadi**

- Membandingkan jam log (WIB) dengan jam dashboard (UTC) tanpa mengonversi, lalu menyimpulkan
  "tidak ada lonjakan".
- Menulis `02.15` di laporan tanpa zona.
- Mengubah zona dashboard di satu laptop lalu mengirim tangkapan layar tanpa menyebutnya.
- Menyetel zona sesi PostgreSQL dan lupa bahwa nilai yang tersimpan tidak berubah, hanya
  tampilannya.

> Cek diri: tiket menyebut gejala terjadi "Jumat pukul 10.05". Bagaimana kamu memastikan
> zona yang dimaksud, dan rentang waktu apa yang kamu pasang di Grafana agar tidak
> terlewat?

---

*Sebelumnya: [Bagian 3](03-memahami-sistem.md) · [Daftar isi](../MATERI-DASAR.md) · Berikutnya: [Bagian 5](05-mutu-dan-desain.md)*
