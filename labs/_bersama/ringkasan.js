// Pembantu bersama untuk script reproduce.k6.js lab PK-2xx (laju tetap, model terbuka).
// Menulis summary.json dan baseline-table.md ke folder out/ lab, dibaca oleh `task evidence`.

// Threshold longgar ini hanya agar k6 menghitung statistik per endpoint. Run ini mengukur, bukan menguji.
export function ambangPerEndpoint(keys) {
  const t = {};
  for (const k of keys) {
    t[`http_req_duration{endpoint:${k}}`] = ['max>=0'];
    t[`http_req_failed{endpoint:${k}}`] = ['rate>=0'];
    t[`http_reqs{endpoint:${k}}`] = ['count>=0'];
  }
  return t;
}

export function skenarioLajuTetap(exec, rate, duration) {
  return {
    executor: 'constant-arrival-rate',
    exec,
    rate,
    timeUnit: '1s',
    duration,
    preAllocatedVUs: 20,
    maxVUs: 200,
  };
}

function ms(v) {
  return v === undefined ? '-' : v.toFixed(0);
}

// rows: [{ key, label }], key sama dengan tag `endpoint` pada request.
export function ringkasan(data, out, rows) {
  const header = '| Endpoint | Laju (req/detik) | p50 (ms) | p95 (ms) | p99 (ms) | Error % | Query/request |\n'
    + '| --- | --- | --- | --- | --- | --- | --- |\n';
  const baris = rows.map((e) => {
    const d = data.metrics[`http_req_duration{endpoint:${e.key}}`];
    const f = data.metrics[`http_req_failed{endpoint:${e.key}}`];
    const r = data.metrics[`http_reqs{endpoint:${e.key}}`];
    const v = d ? d.values : {};
    const rate = r ? r.values.rate.toFixed(1) : '-';
    const err = f ? (f.values.rate * 100).toFixed(2) : '-';
    return `| ${e.label} | ${rate} | ${ms(v.med)} | ${ms(v['p(95)'])} | ${ms(v['p(99)'])} | ${err} | (isi manual) |`;
  });
  const table = header + baris.join('\n') + '\n';
  return {
    [`${out}/summary.json`]: JSON.stringify(data, null, 2),
    [`${out}/baseline-table.md`]: table,
    stdout: '\nRingkasan per endpoint\n\n' + table,
  };
}
