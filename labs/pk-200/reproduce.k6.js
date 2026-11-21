// PK-200: profil toko (tiket contoh, lanjutan #4821). Dijalankan lewat: task reproduce LAB=pk-200
// Laju dan sebaran sama dengan labs/latihan/profile.k6.js (2 req/detik, 30% ke penjual 1),
// tetapi hasilnya dipisah: penjual 1 (produk terbanyak) dan penjual lain, dan ditulis ke out/ untuk task evidence.
// Nyalakan kondisi dengan `task latihan:mulai`, matikan dengan `task latihan:selesai`.
import http from 'k6/http';
import { check } from 'k6';
import { ambangPerEndpoint, skenarioLajuTetap, ringkasan } from '../_bersama/ringkasan.js';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const OUT = __ENV.OUT || 'labs/pk-200/out';
const DURATION = __ENV.DURATION || '3m';
const RATE = Number(__ENV.RATE || 2);
const SELLERS = Number(__ENV.SELLERS || 200);

const ROWS = [
  { key: 'profilPenjual1', label: 'GET /api/sellers/1/profile (penjual 1)' },
  { key: 'profilLain', label: 'GET /api/sellers/{id}/profile (penjual lain)' },
];

export const options = {
  scenarios: { profil: skenarioLajuTetap('profil', RATE, DURATION) },
  thresholds: ambangPerEndpoint(ROWS.map((r) => r.key)),
  summaryTrendStats: ['avg', 'med', 'p(95)', 'p(99)', 'max'],
};

export function profil() {
  const besar = Math.random() < 0.3;
  const seller = besar ? 1 : 1 + Math.floor(Math.random() * SELLERS);
  const res = http.get(`${BASE_URL}/api/sellers/${seller}/profile`,
    { tags: { endpoint: besar ? 'profilPenjual1' : 'profilLain' } });
  check(res, { 'profil 200': (r) => r.status === 200 });
}

export function handleSummary(data) {
  return ringkasan(data, OUT, ROWS);
}
