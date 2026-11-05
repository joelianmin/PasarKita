# Referensi: peta kode, kosakata, ringkasan perintah

> Dipakai untuk mencari, bukan dibaca berurutan.

---

## R.1 Peta kode

| Mau memahami | Buka |
| --- | --- |
| Alur utama bisnis | `order/CheckoutService.java` |
| Cara menghitung harga dan pajak | `pricing/PricingService.java`, `TaxClient.java` |
| Stok dan saldo | `inventory/InventoryService.java`, `payment/PaymentService.java` |
| Halaman profil penjual | `catalog/SellerProfileController.java` |
| Feature flag | `catalog/FeatureFlags.java` |
| Skema database | `src/main/resources/db/migration/V1__skema_awal.sql` |
| Data contoh | `infra/seed/seed.sql` |
| Konfigurasi aplikasi | `src/main/resources/application.yml`, `application-local.yml` |
| Endpoint Actuator yang dibuka | `application.yml`, bagian `management` |
| Hash password dan login | `customer/PasswordConfig.java`, `customer/AuthController.java` |
| Aturan dependensi antarmodul | `src/test/java/id/pasarkita/ModularityTests.java` (`task modules`) |
| Layanan tiruan | `infra/compose/wiremock/mappings/*.json` |
| Semua perintah `task` | `Taskfile.yml` |
| Versi Maven dan dependensi proyek | `pom.xml`, `.mvn/wrapper/maven-wrapper.properties` (bila ada) |
| Template `BASELINE.md` | Bagian 7.4 (atau materi minggu 1) |
| Pemeriksaan lingkungan otomatis | `tools/Doctor.java` (`task doctor`) |
| Layanan Docker, port, volume, profil | `infra/compose/compose.yaml` |
| Diagram deployment lengkap | `docs/DEPLOYMENT.md` |
| Menjalankan tanpa Docker | `infra/native/README.md`, `infra/native/native.sh` |
| Riwayat migrasi yang sudah berjalan | tabel `flyway_schema_history` di database |
| Dashboard Grafana | `infra/compose/grafana/dashboards/pasarkita-overview.json` |

---

## R.2 Kosakata singkat

| Istilah | Arti |
| --- | --- |
| Baseline | Kondisi awal yang diukur sebagai pembanding |
| Hot spot | Satu bagian (penjual, query, endpoint) yang menanggung porsi beban jauh lebih besar |
| JIT | Kompilasi Java saat berjalan; membuat run pertama lebih lambat |
| N+1 | Satu query induk memicu N query anak |
| Sequential scan | PostgreSQL membaca seluruh tabel |
| Saturasi | Sumber daya sudah penuh dan permintaan mulai antre |
| Seed | Data awal yang diisi ke database |
| SLO | Target mutu yang disepakati, misalnya p95 di bawah 500 ms |
| Trace / span | Jejak satu request / satu langkah di dalamnya |
| BOLA | Mengakses objek milik orang lain dengan mengganti ID di request |
| DTO | Objek khusus untuk data yang dikirim lewat API, terpisah dari entity database |
| ISO/IEC 25010 | Standar delapan karakteristik mutu perangkat lunak |
| OWASP | Komunitas yang menerbitkan daftar risiko keamanan paling umum |
| SOLID | Lima pedoman desain kelas: SRP, OCP, LSP, ISP, DIP |
| Akar masalah | Penyebab terdalam yang, jika diperbaiki, membuat gejalanya hilang |
| Bisect | Mempersempit masalah dengan menguji titik tengah rantai berulang kali |
| Hipotesis | Dugaan tertulis yang bisa dibuktikan benar atau salah lewat satu uji |
| Lokalisasi | Menentukan lapisan atau komponen tempat masalah terjadi |
| Reproduksi | Langkah pasti yang selalu memunculkan masalah |
| Blast radius | Seberapa luas dampak sebuah eksperimen atau kegagalan |
| Commit | Satu titik tersimpan dalam riwayat Git |
| Maven Wrapper | Skrip `mvnw` yang menjalankan Maven dengan versi yang ditetapkan proyek |
| Pull request (PR) | Usulan menggabungkan cabang ke cabang utama, tempat peninjauan dilakukan |
| Staging | Area Git tempat perubahan dikumpulkan sebelum di-commit |
| Chaos engineering | Eksperimen terkontrol untuk menguji ketahanan sistem, diawali hipotesis |
| Fault injection | Sengaja memasukkan kesalahan ke sistem untuk mengamati perilakunya |
| FMEA | Analisis tertulis mode kegagalan komponen, dampak, dan cara mendeteksinya |
| Game day | Sesi latihan terjadwal menyimulasikan kegagalan dan menanganinya bersama |
| Negative testing | Menguji dengan input atau kondisi yang tidak valid |
| Steady state | Kondisi normal terukur yang menjadi pembanding eksperimen |

---

## R.3 Ringkasan perintah

Perintah `task` dari `Taskfile.yml`. Kolom terakhir adalah padanan tanpa Docker (lihat bagian 2.4).

| Tujuan | Dengan Docker | Tanpa Docker |
| --- | --- | --- |
| Periksa lingkungan | `task doctor` | `task doctor` (Docker akan dilaporkan gagal) |
| Unduh dependensi dan image | `task warmup` | `./mvnw -q -DskipTests dependency:go-offline` |
| Nyalakan infrastruktur | `task up PROFILE=core,observability` | `task native:up` |
| Matikan (data tetap) | `task down` | `task native:down` |
| Matikan dan hapus data | `task reset` | `task native:reset` |
| Migrasi dan isi data | `task seed SCALE=S` atau `M` | `task native:seed SCALE=S` atau `M` |
| Siapkan satu lab | `task lab LAB=0` | `task native:up`, lalu `task native:seed SCALE=M` |
| Jalankan aplikasi | `task app` | `task app` |
| Buka psql | `task psql` | `task native:psql` |
| Ubah feature flag | `task flag NAME=... ON=true` | `task native:psql`, lalu `UPDATE feature_flag ...` |
| Test fungsional | `task test` | tidak tersedia (butuh Docker) |
| Struktur modul | `task modules` | `task modules` |
| Beban lab | `task reproduce LAB=0` | `task reproduce LAB=0` |
| Traffic latar belakang | `task traffic DURATION=30m` | `task traffic DURATION=30m` |
| File bukti | `task evidence LAB=0` | `task evidence LAB=0` |

---

*[Daftar isi](../MATERI-DASAR.md) · Sebelumnya: [Bagian 7](07-menyerahkan-hasil.md)*
