// PK-201: daftar order di back-office. Dijalankan lewat: task reproduce LAB=pk-201
// Laju dan parameter sama dengan skenario adminOrders di Lab 0, supaya hasilnya sebanding dengan baseline Lab 0 kelompokmu.
import http from 'k6/http';
import { check } from 'k6';
import { ambangPerEndpoint, skenarioLajuTetap, ringkasan } from '../_bersama/ringkasan.js';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const OUT = __ENV.OUT || 'labs/pk-201/out';
const DURATION = __ENV.DURATION || '3m';
const RATE = Number(__ENV.RATE || 2);

const ROWS = [{ key: 'adminOrders', label: 'GET /api/admin/orders' }];

export const options = {
  scenarios: { adminOrders: skenarioLajuTetap('adminOrders', RATE, DURATION) },
  thresholds: ambangPerEndpoint(ROWS.map((r) => r.key)),
  summaryTrendStats: ['avg', 'med', 'p(95)', 'p(99)', 'max'],
};

const rand = (n) => 1 + Math.floor(Math.random() * n);

export function adminOrders() {
  const res = http.get(`${BASE_URL}/api/admin/orders?page=${rand(20) - 1}&size=20`, { tags: { endpoint: 'adminOrders' } });
  check(res, { 'admin orders 200': (r) => r.status === 200 });
}

export function handleSummary(data) {
  return ringkasan(data, OUT, ROWS);
}
