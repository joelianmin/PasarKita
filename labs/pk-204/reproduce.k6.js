// PK-204: login. Dijalankan lewat: task reproduce LAB=pk-204
// Data skala L (sekitar 2 juta pelanggan). Laju 3 req/detik mendekati puncak sekitar 12 ribu login per jam.
// Ubah dengan RATE=n bila laptopmu tidak kuat, dan catat di laporan.
import http from 'k6/http';
import { check } from 'k6';
import { ambangPerEndpoint, skenarioLajuTetap, ringkasan } from '../_bersama/ringkasan.js';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const OUT = __ENV.OUT || 'labs/pk-204/out';
const DURATION = __ENV.DURATION || '3m';
const RATE = Number(__ENV.RATE || 3);
const CUSTOMERS = Number(__ENV.CUSTOMERS || 2000000);

const ROWS = [{ key: 'login', label: 'POST /api/auth/login' }];

export const options = {
  scenarios: { login: skenarioLajuTetap('login', RATE, DURATION) },
  thresholds: ambangPerEndpoint(ROWS.map((r) => r.key)),
  summaryTrendStats: ['avg', 'med', 'p(95)', 'p(99)', 'max'],
};

const rand = (n) => 1 + Math.floor(Math.random() * n);
const json = { headers: { 'Content-Type': 'application/json' } };

export function login() {
  const body = JSON.stringify({ email: `user${rand(CUSTOMERS)}@pasarkita.test`, password: 'rahasia123' });
  const res = http.post(`${BASE_URL}/api/auth/login`, body, { ...json, tags: { endpoint: 'login' } });
  check(res, { 'login 200': (r) => r.status === 200 });
}

export function handleSummary(data) {
  return ringkasan(data, OUT, ROWS);
}
