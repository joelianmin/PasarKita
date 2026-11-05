# Bagian 4: Mengukur dan mengamati

*Observability dan k6*

> **Tujuan.** Mengumpulkan angka yang bisa dipercaya dan membacanya dari sisi klien, server, dan database.  
> **Prasyarat.** Bagian 1, 2, dan 3.  
> **Setelah bagian ini kamu bisa:**
> - membedakan metrik, trace, dan log serta memakai Grafana, Prometheus, dan Tempo
> - membaca script k6 dan hasilnya, termasuk `dropped_iterations` dan threshold
> - menjalankan pengukuran berulang dengan kondisi yang sama

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
  rentang waktunya.
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

*Sebelumnya: [Bagian 3](03-memahami-sistem.md) · [Daftar isi](../MATERI-DASAR.md) · Berikutnya: [Bagian 5](05-mutu-dan-desain.md)*
