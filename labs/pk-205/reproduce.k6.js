// PK-205: daftar invoice penjual. Dijalankan lewat: task reproduce LAB=pk-205
// Laju dan sebaran sama dengan skenario invoices di Lab 0 (sekitar seperempat request ke penjual 1),
// tetapi hasilnya dipisah: penjual 1 (Toko Raksasa) dan penjual lain.
import http from 'k6/http';
import { check } from 'k6';
import { ambangPerEndpoint, skenarioLajuTetap, ringkasan } from '../_bersama/ringkasan.js';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const OUT = __ENV.OUT || 'labs/pk-205/out';
const DURATION = __ENV.DURATION || '3m';
const RATE = Number(__ENV.RATE || 2);
const SELLERS = Number(__ENV.SELLERS || 2000);

const ROWS = [
  { key: 'invoicesPenjual1', label: 'GET /api/sellers/1/invoices (penjual 1)' },
  { key: 'invoicesLain', label: 'GET /api/sellers/{id}/invoices (penjual lain)' },
];

export const options = {
  scenarios: { invoices: skenarioLajuTetap('invoices', RATE, DURATION) },
  thresholds: ambangPerEndpoint(ROWS.map((r) => r.key)),
  summaryTrendStats: ['avg', 'med', 'p(95)', 'p(99)', 'max'],
};

const rand = (n) => 1 + Math.floor(Math.random() * n);

export function invoices() {
  const besar = Math.random() < 0.25;
  const seller = besar ? 1 : rand(SELLERS);
  const res = http.get(`${BASE_URL}/api/sellers/${seller}/invoices?page=0&size=20`,
    { tags: { endpoint: besar ? 'invoicesPenjual1' : 'invoicesLain' } });
  check(res, { 'invoices 200': (r) => r.status === 200 });
}

export function handleSummary(data) {
  return ringkasan(data, OUT, ROWS);
}
