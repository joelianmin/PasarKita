// Simulator traffic lokal, pengganti production simulasi jika server lab belum tersedia.
// Dijalankan lewat: task traffic   (default 8 jam, ubah dengan DURATION=30m)
import http from 'k6/http';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const DURATION = __ENV.DURATION || '8h';
const PRODUCTS = Number(__ENV.PRODUCTS || 5000);
const CUSTOMERS = Number(__ENV.CUSTOMERS || 10000);
const SELLERS = Number(__ENV.SELLERS || 200);

http.setResponseCallback(http.expectedStatuses({ min: 200, max: 399 }, 402, 409));

function scenario(exec, rate) {
  return { executor: 'constant-arrival-rate', exec, rate, timeUnit: '1s', duration: DURATION, preAllocatedVUs: 5, maxVUs: 50 };
}

export const options = {
  scenarios: {
    browse: scenario('browse', 4),
    profile: scenario('profile', 1),
    shop: scenario('shop', 1),
    backoffice: scenario('backoffice', 1),
  },
  summaryTrendStats: ['med', 'p(95)', 'p(99)'],
};

const rand = (n) => 1 + Math.floor(Math.random() * n);

export function browse() {
  if (Math.random() < 0.5) {
    http.get(`${BASE_URL}/api/products?page=${rand(20) - 1}&size=20`);
  } else {
    http.get(`${BASE_URL}/api/products/${rand(PRODUCTS)}`);
  }
}

export function profile() {
  const seller = Math.random() < 0.3 ? 1 : rand(SELLERS);
  http.get(`${BASE_URL}/api/sellers/${seller}/profile`);
}

export function shop() {
  const headers = { 'Content-Type': 'application/json', 'X-Customer-Id': String(rand(CUSTOMERS)) };
  http.post(`${BASE_URL}/api/cart/items`, JSON.stringify({ productId: rand(PRODUCTS), quantity: 1 }), { headers });
  http.post(`${BASE_URL}/api/checkout`, null, { headers });
}

export function backoffice() {
  http.get(`${BASE_URL}/api/admin/orders?page=0&size=20`);
}
