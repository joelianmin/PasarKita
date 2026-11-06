// Latihan membedah tiket (minggu 1, sesi 5): beban ke halaman profil penjual.
// Jalankan: k6 run labs/latihan/profile.k6.js
import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const SELLERS = Number(__ENV.SELLERS || 200);

export const options = {
  scenarios: {
    profile: { executor: 'constant-arrival-rate', rate: Number(__ENV.RATE || 2), timeUnit: '1s', duration: __ENV.DURATION || '3m', preAllocatedVUs: 20, maxVUs: 200 },
  },
  summaryTrendStats: ['avg', 'med', 'p(95)', 'p(99)', 'max'],
};

export default function () {
  const seller = Math.random() < 0.3 ? 1 : 1 + Math.floor(Math.random() * SELLERS);
  const res = http.get(`${BASE_URL}/api/sellers/${seller}/profile`);
  check(res, { 'profil 200': (r) => r.status === 200 });
}
