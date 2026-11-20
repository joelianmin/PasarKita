# Thread #mobile-dev

**Dewi (Tim Mobile), 9 Nov 2026, 14.05**
Untuk jaga-jaga kalau ada perubahan di `POST /api/cart/items`, ini perilaku aplikasi sekarang:

- Validasi jumlah (1 sampai 99) di sisi aplikasi baru ada di **versi 3.4**. Versi 3.2 dan 3.3 tidak memeriksa apa pun sebelum mengirim.
- Respons **201** dianggap sukses.
- Respons **400** dan **404**: aplikasi menampilkan isi field `message` dari badan respons kepada pelanggan.
- Respons **5xx**: aplikasi menampilkan "Terjadi kesalahan, coba lagi" dan **mengulang otomatis sampai tiga kali**.
- Kode lain (misalnya 409 atau 422) tidak dikenali dan ditampilkan sebagai kesalahan umum.

**Lina (QA), 14.12**
Jadi kalau backend kirim 500 untuk input yang salah, request yang sama dikirim empat kali?

**Dewi, 14.14**
Betul. Itu sebabnya kami minta input yang salah dijawab 400 atau 404, bukan 500.

**Bayu (Product), 14.30**
Dicatat. Jangan ubah kode 201 untuk yang berhasil, karena kontrak dengan aplikasi mobile dan mitra reseller sudah berjalan.
