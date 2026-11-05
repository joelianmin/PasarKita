# Menjalankan PasarKita tanpa Docker

Untuk komputer yang tidak bisa memakai Docker Desktop (misalnya Mac Intel dengan macOS 12).
Komponen pendukung dijalankan langsung sebagai proses biasa. Aplikasi dan k6 tetap sama.

| Komponen | Versi | Sumber |
| --- | --- | --- |
| PostgreSQL | 16 (Postgres.app 2.9.6) | postgresapp.com, biner disalin dari DMG |
| WireMock | 3.9.1 standalone | Maven Central |
| Prometheus | 2.55.1 | GitHub releases |
| Grafana | 11.3.0 | dl.grafana.com |
| Tempo | 2.6.1 | GitHub releases |
| Flyway CLI | 10.20.1 | Maven Central |

Versinya sama dengan `infra/compose/compose.yaml`. Semuanya dipasang di `~/pasarkita-native`
(ubah dengan variabel `PASARKITA_NATIVE`), data di `~/pasarkita-native/data`, log di
`~/pasarkita-native/logs`. Tidak ada yang dipasang di folder sistem.

## Pemakaian

```bash
use-jdk21                 # fungsi shell lokal; intinya: set JAVA_HOME ke JDK 21 dan tambahkan ke PATH
task native:up            # PostgreSQL, WireMock, Prometheus, Grafana, Tempo
task native:seed SCALE=S  # migrasi + data contoh (SCALE=M untuk Lab 0)
task app                  # aplikasi di http://localhost:8080
task native:status
task native:down          # data tetap tersimpan
task native:reset         # HAPUS semua data
task native:psql          # psql ke database pasarkita
```

Alamat sama dengan versi Docker: Grafana `:3000`, Prometheus `:9090`, WireMock `:8089`,
PostgreSQL `:5432`, Tempo `:3200` dan `:4318`. `task reproduce`, `task traffic`, dan
`task flag` bekerja seperti biasa, kecuali `task flag` yang masih memakai Docker. Sebagai
gantinya: `task native:psql`, lalu `UPDATE feature_flag SET enabled = true WHERE name = '...';`.

## Perbedaan dengan versi Docker

- **`task test` (Testcontainers) tidak bisa dijalankan**, karena test membutuhkan Docker.
- **Prometheus menarik `localhost:8080`**, bukan `host.docker.internal:8080`
  (`infra/native/prometheus.yml`).
- **Tempo, Grafana, dan WireMock memakai path lokal** yang dirender `native.sh` saat start.
- **Flyway** dijalankan dari CLI (`task native:seed` otomatis memigrasi dulu). Aplikasi tetap
  memigrasi sendiri saat start, jadi hasilnya sama.
- **WireMock dijalankan dengan `--disable-http2-plain`.** Tanpa opsi ini, klien HTTP bawaan
  JDK yang dipakai `RestClient` mencoba naik ke HTTP/2 (h2c) dan gagal dengan
  `Received RST_STREAM: Stream cancelled` untuk request POST tanpa panjang body yang
  diketahui, sehingga `POST /api/checkout` selalu 500 meski WireMock menjawab 200.
  Perilaku ini sudah diuji dengan program kecil di luar aplikasi. Apakah image Docker
  WireMock juga terpengaruh belum diperiksa.
- **Port harus bebas**: 8080, 3000, 9090, 5432, 8089, 3200, 4318.

## Pemecahan masalah

| Gejala | Tindakan |
| --- | --- |
| `Tidak ditemukan: ... (komponen belum dipasang)` | Biner belum ada di `~/pasarkita-native`; pasang ulang dari tabel di atas |
| `JDK 21 tidak ditemukan` | Jalankan `use-jdk21`, atau set `JAVA_HOME` ke JDK 21 |
| Port sudah dipakai | `lsof -nP -iTCP:<port> -sTCP:LISTEN`, hentikan prosesnya |
| Komponen gagal siap | Baca log di `~/pasarkita-native/logs/<nama>.log` |
| Ingin mulai bersih | `task native:reset`, lalu `task native:up` dan `task native:seed` |
