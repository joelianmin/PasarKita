# Bagian 5: Mutu dan desain

*SOLID, ISO/IEC 25010, OWASP*

> **Tujuan.** Memiliki kerangka untuk menilai kode, merumuskan target mutu yang terukur, dan mengenali risiko keamanan.  
> **Prasyarat.** Bagian 3. Bagian 4 membantu untuk target yang terukur.  
> **Setelah bagian ini kamu bisa:**
> - menilai sebuah kelas dengan lima prinsip SOLID
> - menyatakan keluhan sebagai pernyataan mutu terukur (karakteristik, ukuran, target, kondisi)
> - mengenali risiko OWASP yang relevan dan batas lab ini

---

## 5.1 Prinsip desain SOLID

SOLID adalah lima pedoman desain kelas agar kode mudah dipahami dan diubah. Lab minggu 2–6
meminta kamu mengubah kode tanpa merusak bagian lain, jadi kamu perlu bisa menilai
desainnya. SOLID itu pedoman, bukan hukum: jangan membuat abstraksi tanpa alasan nyata.

| Prinsip | Artinya | Contoh di PasarKita | Telaah |
| --- | --- | --- | --- |
| **S** – Single Responsibility | Satu kelas punya satu alasan untuk berubah | `LoyaltyService` hanya menghitung poin; `InventoryService` hanya mengurus stok | Buka `CheckoutService`. Sebutkan alasan-alasan berbeda yang membuatnya harus diubah |
| **O** – Open/Closed | Terbuka untuk perluasan, tertutup untuk modifikasi | Menambah feature flag baru tidak mengubah `FeatureFlags`, cukup menambah baris di tabel `feature_flag` | Bagaimana menambah kanal notifikasi baru tanpa mengubah alur checkout? |
| **L** – Liskov Substitution | Pengganti harus memenuhi kontrak aslinya | WireMock menggantikan layanan pajak sungguhan, dan aplikasi tidak tahu bedanya karena kontrak HTTP sama | Apa kontrak implisit `TaxClient.rateFor`? Apakah pengganti yang melempar exception melanggarnya? |
| **I** – Interface Segregation | Klien tidak dipaksa bergantung pada hal yang tidak ia pakai | `CartController` hanya bergantung pada `CartItemRepository`; modul lain hanya melihat kelas `public` | Kenapa `TaxClient` dibuat package-private? |
| **D** – Dependency Inversion | Bergantung pada abstraksi, bukan implementasi konkret | Constructor injection; repository berupa interface yang implementasinya disediakan Spring; test mengganti database dengan Testcontainers tanpa mengubah kode | Jalankan `task modules` dan baca pelanggaran yang dilaporkan. Arah dependensi mana yang bocor? |

**Kaitan dengan topik lain**

- **Testability** (ISO 25010, bagian 5.2): kelas dengan satu tanggung jawab dan dependensi
  yang disuntikkan mudah diuji sendiri.
- **Observability**: langkah yang dipisah rapi mudah dilihat sebagai span terpisah di trace.
- **Spring Modulith** memeriksa arah dependensi antarmodul (`ModularityTests`). Test ini
  sengaja dipisah dari `task test` karena struktur base code belum sepenuhnya bersih.

> Cek diri: pilih satu kelas dan jelaskan dalam satu kalimat apa tanggung jawabnya. Kalau
> kalimatnya butuh kata "dan" lebih dari sekali, prinsip mana yang patut dicurigai?

---

## 5.2 Model mutu ISO/IEC 25010

ISO/IEC 25010 adalah standar yang membagi mutu perangkat lunak menjadi delapan karakteristik.
Gunanya sebagai daftar periksa: "mutu" jadi bisa dibicarakan dan diukur, tidak berhenti
di "sistemnya lambat".

| Karakteristik | Subkarakteristik utama | Contoh di PasarKita | Alat ukur |
| --- | --- | --- | --- |
| **Functional suitability** | completeness, correctness, appropriateness | Total dan pajak checkout benar; stok tidak boleh negatif | `SmokeTests`, `check()` di k6 |
| **Performance efficiency** | time behaviour, resource utilization, capacity | p95 endpoint, CPU dan memori JVM, beban maksimum sebelum error | k6, Grafana, `pg_stat_statements` |
| **Compatibility** | co-existence, interoperability | Kontrak HTTP/JSON dengan layanan pajak, payment, notifikasi; port tidak bentrok | WireMock, `task doctor` |
| **Usability** (2023: *interaction capability*) | operability, user error protection | Status code tepat dan pesan error jelas untuk pemakai API | Uji manual dengan `curl` |
| **Reliability** | maturity, availability, fault tolerance, recoverability | Apa yang terjadi bila layanan pajak mati? Data bertahan di volume | `/actuator/health`, uji matikan WireMock |
| **Security** | confidentiality, integrity, accountability, authenticity | Hash password, akses data pelanggan lain | Bagian 5.3 |
| **Maintainability** | modularity, analysability, modifiability, testability | Spring Modulith, SOLID, test, trace | `task modules`, `task test` |
| **Portability** (2023: *flexibility*) | installability, replaceability | Docker Compose dan `task doctor` agar jalan di Windows, macOS, Linux | `task doctor` |

Revisi 2023 mengganti nama dua karakteristik (tertulis di tabel) dan menambah *safety*.
Pembagian di tabel mengikuti edisi 2011 yang masih paling banyak dipakai.

**Cara memakainya di lab.** Ubah keluhan menjadi pernyataan terukur dengan empat bagian:
karakteristik, ukuran, target, dan kondisi.

> Performance efficiency, *time behaviour*: p95 latensi `GET /api/products/{id}` kurang
> dari 200 ms, dengan error di bawah 1%, pada 10 req/detik dan data skala M.

Pernyataan seperti ini bisa menjadi `threshold` k6 (bagian 4.2) dan menjadi tolok ukur sebelum
dan sesudah perbaikan.

**Trade-off antar karakteristik.** Memperbaiki satu bisa merusak yang lain:

- Cache mempercepat respons (*performance*) tetapi bisa menampilkan data basi (*functional
  correctness*). Cache feature flag 5 detik adalah contohnya.
- BCrypt sengaja lambat (*security*) sehingga membebani CPU saat login (*performance*).
- Indeks mempercepat baca tetapi memperlambat tulis dan memakai ruang.

> Cek diri: tulis tiga pernyataan mutu untuk checkout, masing-masing satu untuk performance
> efficiency, reliability, dan security. Mana yang paling sulit diukur di laptop?

---

## 5.3 Keamanan: OWASP

OWASP adalah komunitas nirlaba yang menerbitkan daftar risiko keamanan paling umum. Dua
yang relevan: **OWASP Top 10** (aplikasi web, edisi 2021) dan **OWASP API Security Top 10**
(edisi 2023). Cek edisi terbaru di owasp.org.

**Batas lab ini.** PasarKita sengaja disederhanakan (tanpa autentikasi sungguhan) agar
fokus pada performa. Lab tidak meminta kamu memperbaiki hal-hal di bawah, tetapi kamu
harus bisa mengenalinya dan jangan menirunya di produksi.

| Risiko | Artinya | Keadaan di PasarKita | Praktik yang benar |
| --- | --- | --- | --- |
| **A01 Broken Access Control**, API1 (BOLA), API5 (BFLA) | Pengguna mengakses data atau fungsi yang bukan haknya | Identitas diambil dari header `X-Customer-Id` yang bisa diisi siapa saja. Tidak ada Spring Security (hanya `spring-security-crypto`), jadi `/api/admin/orders` dan invoice penjual terbuka | Autentikasi (sesi atau token), cek kepemilikan per objek, cek peran per fungsi |
| **A02 Cryptographic Failures** | Data sensitif tidak dilindungi | Password di-hash BCrypt (baik). Kredensial database dan password demo ada di konfigurasi dan README | Secret dari environment atau secret manager, TLS, jangan commit kredensial |
| **A03 Injection** | Input diperlakukan sebagai perintah | Query memakai parameter terikat (`:q`, `?`), jadi aman | Jangan menyambung teks SQL dari input |
| **A04 Insecure Design**, API6 | Kelemahan ada di rancangan, bukan kode | Alur checkout memindahkan uang dan stok. Tidak ada pembatasan atau pengaman terhadap permintaan ganda dan otomasi | Threat modeling, batas laju, idempotency key |
| **A05 Security Misconfiguration** | Konfigurasi bawaan yang tidak aman | Grafana tanpa login, endpoint Actuator (`metrics`, `prometheus`) terbuka, database terbuka ke host | Matikan default, buka seperlunya |
| **A06 Vulnerable and Outdated Components** | Memakai library yang sudah punya celah | Versi dikunci (Spring Boot 3.5.0, image bertag) | Perbarui berkala, pindai dengan OWASP Dependency-Check atau Dependabot |
| **A07 Identification and Authentication Failures**, API2 | Login lemah | Login tidak membatasi jumlah percobaan. Respons 401 sama untuk email tak dikenal dan password salah (baik, tidak membocorkan akun) | Batas percobaan, MFA, pesan error seragam |
| **A08 Software and Data Integrity Failures** | Kode atau data tidak diverifikasi | Image dari registry resmi dengan versi tetap. Hash SHA-256 di file bukti adalah contoh pemeriksaan integritas | Verifikasi sumber dan checksum |
| **A09 Security Logging and Monitoring Failures** | Kejadian penting tidak tercatat atau log membocorkan data | Log SQL profil `local` memuat parameter. Modul `audit` masih kosong, jadi belum ada jejak audit | Catat login gagal dan akses ditolak tanpa data sensitif, beri alert |
| **A10 SSRF**, API7 | Server dipaksa memanggil URL pilihan penyerang | URL layanan luar berasal dari konfigurasi, bukan input pengguna, jadi tidak berlaku saat ini | Jangan pernah mengambil URL dari input tanpa daftar putih |
| **API3 Broken Object Property Level Authorization** | Respons membuka field berlebih | `passwordHash` ditandai `@JsonIgnore` (baik), tetapi endpoint admin mengembalikan entity `Order` beserta data pelanggannya | Kembalikan DTO dengan field yang dipilih |
| **API4 Unrestricted Resource Consumption** | Satu klien bisa menghabiskan sumber daya | Parameter `size` halaman tidak punya batas atas. Tidak ada rate limiting | Batasi `size`, batas laju, timeout |
| **API10 Unsafe Consumption of APIs** | Mempercayai respons layanan luar | Respons layanan pajak dipakai apa adanya, dan tarif kosong dianggap nol | Validasi respons, timeout, putuskan perilaku saat gagal |

**Kebiasaan praktis**

1. Validasi semua input di tepi (`@Valid`, batas panjang, batas angka).
2. Jangan percaya input pengguna maupun respons layanan luar.
3. Beri hak akses sekecil mungkin (akun database, peran pengguna).
4. Simpan secret di luar repo.
5. Log kejadian keamanan, tetapi jangan log data sensitif.
6. Perbarui dependensi secara rutin.

**Keamanan dan performa saling terkait.** Tanpa batas laju dan batas ukuran, endpoint yang
mahal bisa dipakai untuk melumpuhkan layanan (*denial of service*). Sebaliknya,
pengaman seperti hashing BCrypt memakan CPU. Itu sebabnya karakteristik mutu perlu
dilihat bersama (bagian 5.2).

> Cek diri: seorang pelanggan mengganti `X-Customer-Id` dari 5 menjadi 6 lalu memanggil
> checkout. Risiko OWASP mana yang terjadi, dan apa yang harus dicek server?

---

*Sebelumnya: [Bagian 4](04-mengukur-dan-mengamati.md) · [Daftar isi](../MATERI-DASAR.md) · Berikutnya: [Bagian 6](06-diagnosa-dan-latihan.md)*
