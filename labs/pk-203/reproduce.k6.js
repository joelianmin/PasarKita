// PK-203: katalog produk. Dijalankan lewat: task reproduce LAB=pk-203
// Laju dan parameter sama dengan skenario catalog di Lab 0, supaya hasilnya sebanding dengan baseline Lab 0 kelompokmu.
import http from 'k6/http';
import { check } from 'k6';
import { ambangPerEndpoint, skenarioLajuTetap, ringkasan } from '../_bersama/ringkasan.js';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const OUT = __ENV.OUT || 'labs/pk-203/out';
const DURATION = __ENV.DURATION || '3m';
const RATE = Number(__ENV.RATE || 10);

const ROWS = [{ key: 'catalog', label: 'GET /api/products' }];

export const options = {
  scenarios: { catalog: skenarioLajuTetap('catalog', RATE, DURATION) },
  thresholds: ambangPerEndpoint(ROWS.map((r) => r.key)),
  summaryTrendStats: ['avg', 'med', 'p(95)', 'p(99)', 'max'],
};

const rand = (n) => 1 + Math.floor(Math.random() * n);

export function catalog() {
  const res = http.get(`${BASE_URL}/api/products?page=${rand(50) - 1}&size=20`, { tags: { endpoint: 'catalog' } });
  check(res, { 'catalog 200': (r) => r.status === 200 });
}

export function handleSummary(data) {
  return ringkasan(data, OUT, ROWS);
}
