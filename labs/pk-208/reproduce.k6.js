// PK-208: parameter halaman. Dijalankan lewat: task reproduce LAB=pk-208
// Mengirim parameter halaman yang sah dan yang salah ke tiap endpoint berhalaman, lalu mencatat kode status dan waktunya.
// "size besar" memakai 5000 (bukan angka yang sangat besar) supaya laptopmu tetap bisa menyelesaikan run sebelum perbaikan.
// Kolom "Diharapkan" adalah patokan dari tiket dan paket konteks, bukan hasil sebelum perbaikanmu.
import http from 'k6/http';
import { siapkan, catat, ringkasanStatus } from '../_bersama/status.js';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const OUT = __ENV.OUT || 'labs/pk-208/out';
const ULANG = Number(__ENV.ULANG || 3);

const ENDPOINT = [
  { key: 'pesanan', nama: 'GET /api/admin/orders', path: '/api/admin/orders', tambahan: '' },
  { key: 'katalog', nama: 'GET /api/products', path: '/api/products', tambahan: '' },
  { key: 'pencarian', nama: 'GET /api/products/search', path: '/api/products/search', tambahan: '&q=kopi' },
  { key: 'invoice', nama: 'GET /api/sellers/1/invoices', path: '/api/sellers/1/invoices', tambahan: '' },
];
const JENIS = [
  { key: 'size20', label: 'size=20', page: 0, size: 20, diharapkan: '200' },
  { key: 'size200', label: 'size=200', page: 0, size: 200, diharapkan: '200' },
  { key: 'size0', label: 'size=0', page: 0, size: 0, diharapkan: '400' },
  { key: 'page_negatif', label: 'page=-1', page: -1, size: 20, diharapkan: '400' },
  { key: 'size5000', label: 'size=5000', page: 0, size: 5000, diharapkan: '400' },
];

const KODE = [200, 400, 500];
const KASUS = [];
for (const e of ENDPOINT) {
  for (const j of JENIS) {
    KASUS.push({ key: `${e.key}_${j.key}`, label: `${e.nama}, ${j.label}`, diharapkan: j.diharapkan, e, j });
  }
}
const alat = siapkan(KASUS, KODE);

http.setResponseCallback(http.expectedStatuses({ min: 200, max: 599 }));

export const options = {
  scenarios: {
    kasus: { executor: 'shared-iterations', vus: 1, iterations: ULANG, maxDuration: '10m' },
  },
};

export default function () {
  for (const k of KASUS) {
    const url = `${BASE_URL}${k.e.path}?page=${k.j.page}&size=${k.j.size}${k.e.tambahan}`;
    const res = http.get(url, { timeout: '120s' });
    catat(alat, k.key, res);
  }
}

export function handleSummary(data) {
  return ringkasanStatus(data, OUT, KASUS, KODE);
}
