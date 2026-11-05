// Lab 0: baseline delapan endpoint utama dengan laju tetap (model terbuka).
// Dijalankan lewat: task reproduce LAB=0
import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const OUT = __ENV.OUT || 'labs/0/out';
const DURATION = __ENV.DURATION || '3m';
const PRODUCTS = Number(__ENV.PRODUCTS || 50000);
const CUSTOMERS = Number(__ENV.CUSTOMERS || 200000);
const SELLERS = Number(__ENV.SELLERS || 2000);

const ENDPOINTS = [
  { key: 'catalog', label: 'GET /api/products', rate: 10 },
  { key: 'search', label: 'GET /api/products/search', rate: 3 },
  { key: 'detail', label: 'GET /api/products/{id}', rate: 10 },
  { key: 'login', label: 'POST /api/auth/login', rate: 2 },
  { key: 'cart', label: 'POST /api/cart/items', rate: 2 },
  { key: 'checkout', label: 'POST /api/checkout', rate: 2 },
  { key: 'adminOrders', label: 'GET /api/admin/orders', rate: 2 },
  { key: 'invoices', label: 'GET /api/sellers/{id}/invoices', rate: 2 },
];
const WORDS = ['kopi', 'teh', 'sambal', 'keripik', 'batik', 'madu', 'SKU-00012', 'premium'];

http.setResponseCallback(http.expectedStatuses({ min: 200, max: 399 }, 402, 409));

function scenario(exec, rate) {
  return {
    executor: 'constant-arrival-rate',
    exec,
    rate,
    timeUnit: '1s',
    duration: DURATION,
    preAllocatedVUs: 20,
    maxVUs: 200,
  };
}

const thresholds = {};
for (const e of ENDPOINTS) {
  // Threshold longgar ini hanya agar k6 menghitung statistik per endpoint.
  thresholds[`http_req_duration{endpoint:${e.key}}`] = ['max>=0'];
  thresholds[`http_req_failed{endpoint:${e.key}}`] = ['rate>=0'];
  thresholds[`http_reqs{endpoint:${e.key}}`] = ['count>=0'];
}

export const options = {
  scenarios: {
    catalog: scenario('catalog', 10),
    search: scenario('search', 3),
    detail: scenario('detail', 10),
    login: scenario('login', 2),
    checkout: scenario('checkout', 2),
    adminOrders: scenario('adminOrders', 2),
    invoices: scenario('invoices', 2),
  },
  thresholds,
  summaryTrendStats: ['avg', 'med', 'p(95)', 'p(99)', 'max'],
};

const rand = (n) => 1 + Math.floor(Math.random() * n);
const json = { headers: { 'Content-Type': 'application/json' } };

export function catalog() {
  const res = http.get(`${BASE_URL}/api/products?page=${rand(50) - 1}&size=20`, { tags: { endpoint: 'catalog' } });
  check(res, { 'catalog 200': (r) => r.status === 200 });
}

export function search() {
  const q = WORDS[Math.floor(Math.random() * WORDS.length)];
  const res = http.get(`${BASE_URL}/api/products/search?q=${encodeURIComponent(q)}`, { tags: { endpoint: 'search' } });
  check(res, { 'search 200': (r) => r.status === 200 });
}

export function detail() {
  const res = http.get(`${BASE_URL}/api/products/${rand(PRODUCTS)}`, { tags: { endpoint: 'detail' } });
  check(res, { 'detail 200': (r) => r.status === 200 });
}

export function login() {
  const body = JSON.stringify({ email: `user${rand(CUSTOMERS)}@pasarkita.test`, password: 'rahasia123' });
  const res = http.post(`${BASE_URL}/api/auth/login`, body, { ...json, tags: { endpoint: 'login' } });
  check(res, { 'login 200': (r) => r.status === 200 });
}

export function checkout() {
  const customer = rand(CUSTOMERS);
  const headers = { 'Content-Type': 'application/json', 'X-Customer-Id': String(customer) };
  http.post(`${BASE_URL}/api/cart/items`, JSON.stringify({ productId: rand(PRODUCTS), quantity: 1 }),
    { headers, tags: { endpoint: 'cart' } });
  const res = http.post(`${BASE_URL}/api/checkout`, null, { headers, tags: { endpoint: 'checkout' } });
  check(res, { 'checkout selesai': (r) => r.status === 200 || r.status === 409 || r.status === 402 });
}

export function adminOrders() {
  const res = http.get(`${BASE_URL}/api/admin/orders?page=${rand(20) - 1}&size=20`, { tags: { endpoint: 'adminOrders' } });
  check(res, { 'admin orders 200': (r) => r.status === 200 });
}

export function invoices() {
  // Sekitar seperempat request mengarah ke penjual raksasa (id 1).
  const seller = Math.random() < 0.25 ? 1 : rand(SELLERS);
  const res = http.get(`${BASE_URL}/api/sellers/${seller}/invoices?page=0&size=20`, { tags: { endpoint: 'invoices' } });
  check(res, { 'invoices 200': (r) => r.status === 200 });
}

function ms(v) {
  return v === undefined ? '-' : v.toFixed(0);
}

export function handleSummary(data) {
  const header = '| Endpoint | Laju (req/detik) | p50 (ms) | p95 (ms) | p99 (ms) | Error % | Query/request |\n'
    + '| --- | --- | --- | --- | --- | --- | --- |\n';
  const rows = ENDPOINTS.map((e) => {
    const d = data.metrics[`http_req_duration{endpoint:${e.key}}`];
    const f = data.metrics[`http_req_failed{endpoint:${e.key}}`];
    const r = data.metrics[`http_reqs{endpoint:${e.key}}`];
    const v = d ? d.values : {};
    const rate = r ? r.values.rate.toFixed(1) : '-';
    const err = f ? (f.values.rate * 100).toFixed(2) : '-';
    return `| ${e.label} | ${rate} | ${ms(v.med)} | ${ms(v['p(95)'])} | ${ms(v['p(99)'])} | ${err} | (isi manual) |`;
  });
  const table = header + rows.join('\n') + '\n';
  return {
    [`${OUT}/summary.json`]: JSON.stringify(data, null, 2),
    [`${OUT}/baseline-table.md`]: table,
    stdout: '\nRingkasan per endpoint\n\n' + table,
  };
}
