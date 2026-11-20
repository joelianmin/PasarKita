// PK-206: pencarian produk. Dijalankan lewat: task reproduce LAB=pk-206
// Data skala L (sekitar 500 ribu produk). Kata kunci mengikuti sebaran analitik pencarian pada paket konteks:
// sekitar 58% potongan kode produk, 31% potongan nama produk, sisanya kata lain.
import http from 'k6/http';
import { check } from 'k6';
import { ambangPerEndpoint, skenarioLajuTetap, ringkasan } from '../_bersama/ringkasan.js';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const OUT = __ENV.OUT || 'labs/pk-206/out';
const DURATION = __ENV.DURATION || '3m';
const RATE = Number(__ENV.RATE || 3);

const ROWS = [{ key: 'search', label: 'GET /api/products/search' }];

const KODE = ['0077', '0123', '00123', 'SKU-001', 'SKU-0042'];
const NAMA = ['kopi', 'batik', 'sambal', 'madu', 'keripik'];
const LAIN = ['premium', 'asli', 'spesial'];
const pilih = (a) => a[Math.floor(Math.random() * a.length)];

export const options = {
  scenarios: { search: skenarioLajuTetap('search', RATE, DURATION) },
  thresholds: ambangPerEndpoint(ROWS.map((r) => r.key)),
  summaryTrendStats: ['avg', 'med', 'p(95)', 'p(99)', 'max'],
};

export function search() {
  const r = Math.random();
  const q = r < 0.58 ? pilih(KODE) : r < 0.89 ? pilih(NAMA) : pilih(LAIN);
  const res = http.get(`${BASE_URL}/api/products/search?q=${encodeURIComponent(q)}`, { tags: { endpoint: 'search' } });
  check(res, { 'search 200': (r2) => r2.status === 200 });
}

export function handleSummary(data) {
  return ringkasan(data, OUT, ROWS);
}
