// PK-207: checkout dan koneksi database. Dijalankan lewat: task reproduce LAB=pk-207
// Laju dan alur sama dengan skenario checkout di Lab 0 (tambah ke keranjang, lalu checkout), 2 req/detik.
// Skrip juga membaca metrik pemakaian koneksi database dari aplikasi sebelum dan sesudah run,
// lalu mencetak total dan rata-rata waktu koneksi dipakai selama run. Salin angkanya ke laporanmu.
// Checkout mengubah stok dan saldo. Untuk perbandingan bersih, jalankan `task reset` dan `task lab LAB=pk-207` sebelum tiap run.
import http from 'k6/http';
import { check } from 'k6';
import { ambangPerEndpoint, skenarioLajuTetap, ringkasan } from '../_bersama/ringkasan.js';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const OUT = __ENV.OUT || 'labs/pk-207/out';
const DURATION = __ENV.DURATION || '3m';
const RATE = Number(__ENV.RATE || 2);
const PRODUCTS = Number(__ENV.PRODUCTS || 50000);
const CUSTOMERS = Number(__ENV.CUSTOMERS || 200000);

const ROWS = [
  { key: 'cart', label: 'POST /api/cart/items' },
  { key: 'checkout', label: 'POST /api/checkout' },
];

http.setResponseCallback(http.expectedStatuses({ min: 200, max: 399 }, 402, 409));

export const options = {
  scenarios: { checkout: skenarioLajuTetap('checkout', RATE, DURATION) },
  thresholds: ambangPerEndpoint(ROWS.map((r) => r.key)),
  summaryTrendStats: ['avg', 'med', 'p(95)', 'p(99)', 'max'],
};

const rand = (n) => 1 + Math.floor(Math.random() * n);

function pemakaianKoneksi() {
  const res = http.get(`${BASE_URL}/actuator/metrics/hikaricp.connections.usage`);
  if (res.status !== 200) return null;
  const nilai = {};
  for (const m of res.json('measurements')) nilai[m.statistic] = m.value;
  return nilai;
}

export function setup() {
  return { awal: pemakaianKoneksi() };
}

export function checkout() {
  const customer = rand(CUSTOMERS);
  const headers = { 'Content-Type': 'application/json', 'X-Customer-Id': String(customer) };
  http.post(`${BASE_URL}/api/cart/items`, JSON.stringify({ productId: rand(PRODUCTS), quantity: 1 }),
    { headers, tags: { endpoint: 'cart' } });
  const res = http.post(`${BASE_URL}/api/checkout`, null, { headers, tags: { endpoint: 'checkout' } });
  check(res, { 'checkout selesai': (r) => r.status === 200 || r.status === 409 || r.status === 402 });
}

export function teardown(data) {
  const akhir = pemakaianKoneksi();
  if (!data.awal || !akhir) {
    console.log('Metrik hikaricp.connections.usage tidak terbaca. Periksa /actuator/metrics.');
    return;
  }
  const n = akhir.COUNT - data.awal.COUNT;
  const total = akhir.TOTAL_TIME - data.awal.TOTAL_TIME;
  console.log(`Pemakaian koneksi selama run: ${n} pemakaian, total ${total.toFixed(2)} detik, rata-rata ${n > 0 ? ((total / n) * 1000).toFixed(1) : '-'} ms per pemakaian`);
  console.log('Waktu koneksi per checkout = total detik di atas dibagi jumlah request POST /api/checkout pada ringkasan (tambah keranjang ikut terhitung di total).');
}

export function handleSummary(data) {
  return ringkasan(data, OUT, ROWS);
}
