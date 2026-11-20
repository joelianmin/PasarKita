// PK-202: keranjang. Dijalankan lewat: task reproduce LAB=pk-202
// Mengirim beberapa jenis permintaan ke POST /api/cart/items dan mencatat kode status yang diterima.
// Skrip ini menambah baris ke tabel cart_item di database labmu. Jalankan `task reset` bila ingin mulai bersih.
// Kolom "Diharapkan" adalah patokan dari tiket dan paket konteks, bukan hasil sebelum perbaikanmu.
import http from 'k6/http';
import { siapkan, catat, ringkasanStatus } from '../_bersama/status.js';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const OUT = __ENV.OUT || 'labs/pk-202/out';
const ULANG = Number(__ENV.ULANG || 20);
const PRODUCTS = Number(__ENV.PRODUCTS || 50000);
const CUSTOMERS = Number(__ENV.CUSTOMERS || 200000);

const KODE = [201, 400, 404, 500];
const KASUS = [
  { key: 'sah', label: 'Produk ada, jumlah 1', diharapkan: '201' },
  { key: 'jumlah_nol', label: 'Jumlah 0', diharapkan: '400' },
  { key: 'jumlah_negatif', label: 'Jumlah -5', diharapkan: '400' },
  { key: 'jumlah_100', label: 'Jumlah 100', diharapkan: '400' },
  { key: 'produk_tidak_ada', label: 'Produk 99999999', diharapkan: '404' },
  { key: 'tambah_dua_kali', label: 'Produk sama dua kali, jumlah 1 (cek jumlah baris di database)', diharapkan: '201 dua kali' },
];
const alat = siapkan(KASUS, KODE);

http.setResponseCallback(http.expectedStatuses({ min: 200, max: 599 }));

export const options = {
  scenarios: {
    kasus: { executor: 'shared-iterations', vus: 1, iterations: ULANG, maxDuration: '5m' },
  },
};

const rand = (n) => 1 + Math.floor(Math.random() * n);

function tambah(customer, productId, quantity) {
  const headers = { 'Content-Type': 'application/json', 'X-Customer-Id': String(customer) };
  return http.post(`${BASE_URL}/api/cart/items`, JSON.stringify({ productId, quantity }), { headers });
}

export default function () {
  const c = rand(CUSTOMERS);
  catat(alat, 'sah', tambah(c, rand(PRODUCTS), 1));
  catat(alat, 'jumlah_nol', tambah(c, rand(PRODUCTS), 0));
  catat(alat, 'jumlah_negatif', tambah(c, rand(PRODUCTS), -5));
  catat(alat, 'jumlah_100', tambah(c, rand(PRODUCTS), 100));
  catat(alat, 'produk_tidak_ada', tambah(c, 99999999, 1));
  const p = rand(PRODUCTS);
  catat(alat, 'tambah_dua_kali', tambah(c, p, 1));
  catat(alat, 'tambah_dua_kali', tambah(c, p, 1));
}

export function handleSummary(data) {
  return ringkasanStatus(data, OUT, KASUS, KODE);
}
