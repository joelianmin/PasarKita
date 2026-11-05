# Bagian 1: Fondasi

*Cara berpikir tentang performa dan HTTP*

> **Tujuan.** Memahami cara berpikir dan bahasa dasar yang dipakai di semua lab: bagaimana performa diukur dan bagaimana klien dan server saling bicara.  
> **Prasyarat.** Tidak ada.  
> **Setelah bagian ini kamu bisa:**
> - menjelaskan latensi, throughput, persentil (p50, p95, p99), saturasi, dan beda beban terbuka dengan tertutup
> - membaca status code dan memanggil API dengan `curl`
> - menyebut delapan endpoint yang diukur di Lab 0

---

## 1.1 Cara berpikir tentang performa

Tujuan lab ini adalah **mengukur sebelum mengubah**. Tanpa angka pembanding, "lebih cepat"
hanya perasaan.

**Istilah wajib**

- **Latensi**: waktu dari request dikirim sampai respons selesai diterima, dalam milidetik.
- **Throughput**: jumlah request yang selesai per detik (req/detik).
- **Error rate**: persentase request yang gagal (status 5xx, timeout, koneksi putus).
- **Persentil**: p50 berarti separuh request lebih cepat dari angka itu. p95 berarti 95%
  lebih cepat dan 5% lebih lambat. p99 berarti 1% terburuk. Rata-rata menyembunyikan
  request lambat, jadi lihat p95 dan p99, bukan rata-rata.
- **Saturasi**: komponen yang sudah penuh (CPU, memori, koneksi database, thread). Latensi
  biasanya melonjak tajam setelah saturasi.
- **Kaitan dengan ISO/IEC 25010.** Latensi adalah *time behaviour*, pemakaian CPU, memori,
  dan koneksi adalah *resource utilization*, dan beban maksimum sebelum sistem rusak adalah
  *capacity*. Ketiganya subkarakteristik *performance efficiency* (bagian 5.2).

**Model beban: terbuka dan tertutup**

- *Tertutup*: sejumlah pengguna virtual mengirim request, menunggu respons, lalu mengirim
  lagi. Kalau server melambat, beban ikut turun sendiri, sehingga masalah tersamar.
- *Terbuka*: request tiba dengan laju tetap tanpa peduli server sudah selesai atau belum.
  Ini meniru dunia nyata, dan antrean terlihat jelas saat server lambat.

Script k6 di repo ini memakai model terbuka (`constant-arrival-rate`).

**Kenapa pengukuran harus berulang.** Satu run dipengaruhi JIT Java, cache database, dan
aplikasi lain di laptopmu. Karena itu: satu run pemanasan (dibuang), lalu tiga run
pengukuran, dan bandingkan hasilnya.

> Cek diri: kalau p50 sebuah endpoint 20 ms tetapi p99 2.000 ms, apa artinya untuk
> pengguna? Mana yang lebih berguna untuk dilaporkan?

---

## 1.2 HTTP dan REST

- Request punya **metode** (`GET` membaca, `POST` membuat atau memproses), **path**,
  **header**, dan kadang **body** (di sini JSON).
- Respons punya **status code**:
  - `2xx` berhasil, `4xx` salah dari sisi klien, `5xx` salah dari sisi server.
  - Dalam proyek ini: `200` OK, `201` dibuat, `401` login salah, `404` tidak ditemukan,
    `402` saldo kurang, `409` stok habis.
- `402` dan `409` pada checkout dianggap **hasil normal**, bukan error, karena itu bagian
  dari aturan bisnis. Script k6 mengaturnya lewat `expectedStatuses`.
- **Header `X-Customer-Id`** menggantikan autentikasi sungguhan. Server percaya begitu saja
  isinya. Cukup untuk lab, tidak aman untuk produksi.
- **401 dan 403 berbeda.** `401` berarti server tidak tahu siapa kamu (belum login atau
  kredensial salah). `403` berarti server tahu siapa kamu, tetapi kamu tidak boleh
  mengakses. Risiko kontrol akses (OWASP A01, API1, API5) dibahas di bagian 5.3.

Mencoba manual (di Windows PowerShell 5.1 pakai `curl.exe`):

```bash
curl http://localhost:8080/actuator/health
curl "http://localhost:8080/api/products?page=0&size=5"
curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"user1@pasarkita.test","password":"rahasia123"}'
```

**Delapan endpoint yang diukur di Lab 0**

| Endpoint | Fungsi |
| --- | --- |
| `GET /api/products` | Daftar produk berhalaman |
| `GET /api/products/search?q=` | Cari produk |
| `GET /api/products/{id}` | Detail produk |
| `POST /api/auth/login` | Login |
| `POST /api/cart/items` | Tambah ke keranjang |
| `POST /api/checkout` | Checkout (paling kompleks) |
| `GET /api/admin/orders` | Daftar order untuk admin |
| `GET /api/sellers/{id}/invoices` | Invoice penjual |

> Cek diri: kenapa checkout yang gagal karena stok habis (409) tidak dihitung sebagai
> error di k6?

---

*Sebelumnya: - · [Daftar isi](../MATERI-DASAR.md) · Berikutnya: [Bagian 2](02-lingkungan-kerja.md)*
