// Pembantu bersama untuk lab yang memeriksa kode status (PK-202, PK-208), bukan beban berlaju tetap.
// Tiap kasus dicatat per kode status, lalu ditulis sebagai summary.json dan baseline-table.md di folder out/.
import { Counter, Trend } from 'k6/metrics';

export function siapkan(kasus, kodeStatus) {
  const hitung = {};
  const durasi = {};
  for (const k of kasus) {
    durasi[k.key] = new Trend(`ms_${k.key}`, true);
    for (const s of [...kodeStatus, 'lain']) {
      hitung[`${k.key}_${s}`] = new Counter(`hasil_${k.key}_${s}`);
    }
  }
  return { hitung, durasi, kodeStatus };
}

export function catat(alat, key, res) {
  const s = alat.kodeStatus.includes(res.status) ? res.status : 'lain';
  alat.hitung[`${key}_${s}`].add(1);
  alat.durasi[key].add(res.timings.duration);
}

function angka(data, nama) {
  const m = data.metrics[nama];
  return m && m.values.count !== undefined ? m.values.count : 0;
}

export function ringkasanStatus(data, out, kasus, kodeStatus) {
  const kolom = [...kodeStatus, 'lain'];
  const header = `| Kasus | Diharapkan | ${kolom.join(' | ')} | Rata-rata (ms) |\n`
    + `| --- | --- | ${kolom.map(() => '---').join(' | ')} | --- |\n`;
  const baris = kasus.map((k) => {
    const sel = kolom.map((s) => angka(data, `hasil_${k.key}_${s}`));
    const d = data.metrics[`ms_${k.key}`];
    const avg = d && d.values.avg !== undefined ? d.values.avg.toFixed(0) : '-';
    return `| ${k.label} | ${k.diharapkan} | ${sel.join(' | ')} | ${avg} |`;
  });
  const table = header + baris.join('\n') + '\n';
  return {
    [`${out}/summary.json`]: JSON.stringify(data, null, 2),
    [`${out}/baseline-table.md`]: table,
    stdout: '\nRingkasan per kasus\n\n' + table,
  };
}
