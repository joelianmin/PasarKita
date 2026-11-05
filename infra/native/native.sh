#!/usr/bin/env bash
# Menjalankan stack pendukung PasarKita TANPA Docker (untuk Mac yang tidak bisa memakai Docker Desktop).
# Komponen: PostgreSQL 16, WireMock, Prometheus, Grafana, Tempo. Flyway CLI dipakai untuk migrasi.
# Pemakaian: infra/native/native.sh {install-check|up|down|status|migrate|seed|psql|reset} [argumen]
# Biner dipasang di $PASARKITA_NATIVE (default ~/pasarkita-native). Aplikasi tetap dijalankan dengan `task app`.
set -eu

HERE="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$HERE/../.." && pwd)"
N="${PASARKITA_NATIVE:-$HOME/pasarkita-native}"
DATA="$N/data"; RUN="$N/run"; LOGS="$N/logs"
PG="$N/pg16/bin"
mkdir -p "$DATA" "$RUN" "$LOGS"

export PGPASSWORD=pasarkita
PGHOST_ARGS="-h localhost -p 5432 -U pasarkita"

# Pilih JDK 21 untuk WireMock.
find_java() {
  if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ] && "$JAVA_HOME/bin/java" -version 2>&1 | grep -q '"2[1-9]'; then
    echo "$JAVA_HOME/bin/java"; return
  fi
  local h; h="$(/usr/libexec/java_home -v 21 2>/dev/null || true)"
  if [ -n "$h" ]; then echo "$h/bin/java"; return; fi
  echo "JDK 21 tidak ditemukan. Aktifkan dengan: use-jdk21" >&2; exit 1
}

need() { [ -e "$1" ] || { echo "Tidak ditemukan: $1 (komponen belum dipasang di $N)" >&2; exit 1; }; }

wait_http() { # url nama detik
  local i=0
  while [ $i -lt "$3" ]; do
    if curl -s -o /dev/null --max-time 2 "$1"; then echo "  siap: $2"; return 0; fi
    i=$((i + 1)); sleep 1
  done
  echo "  GAGAL siap dalam $3 detik: $2 (lihat $LOGS)" >&2; return 1
}

alive() { [ -f "$RUN/$1.pid" ] && kill -0 "$(cat "$RUN/$1.pid")" 2>/dev/null; }

start_bg() { # nama perintah...
  local name="$1"; shift
  if alive "$name"; then echo "  sudah berjalan: $name"; return 0; fi
  nohup "$@" > "$LOGS/$name.log" 2>&1 &
  echo $! > "$RUN/$name.pid"
}

stop_bg() {
  local name="$1"
  if alive "$name"; then
    kill "$(cat "$RUN/$name.pid")" 2>/dev/null || true
    local i=0
    while alive "$name" && [ $i -lt 15 ]; do sleep 1; i=$((i + 1)); done
    alive "$name" && kill -9 "$(cat "$RUN/$name.pid")" 2>/dev/null || true
    echo "  berhenti: $name"
  fi
  rm -f "$RUN/$name.pid"
}

pg_init() {
  if [ ! -f "$DATA/pg/PG_VERSION" ]; then
    echo "  menyiapkan klaster PostgreSQL baru"
    echo pasarkita > "$RUN/pgpass.tmp"
    "$PG/initdb" -D "$DATA/pg" -U pasarkita --pwfile="$RUN/pgpass.tmp" -A scram-sha-256 -E UTF8 --locale=C >/dev/null
    rm -f "$RUN/pgpass.tmp"
    cat >> "$DATA/pg/postgresql.conf" <<EOF
# --- PasarKita (menyamai infra/compose/compose.yaml)
listen_addresses = 'localhost'
port = 5432
unix_socket_directories = '$RUN'
shared_preload_libraries = 'pg_stat_statements'
pg_stat_statements.track = all
EOF
    PG_FRESH=1
  fi
}

pg_up() {
  need "$PG/pg_ctl"
  pg_init
  if "$PG/pg_ctl" -D "$DATA/pg" status >/dev/null 2>&1; then
    echo "  sudah berjalan: postgres"
  else
    "$PG/pg_ctl" -D "$DATA/pg" -l "$LOGS/postgres.log" -w -t 60 start >/dev/null
    echo "  siap: postgres :5432"
  fi
  if ! "$PG/psql" $PGHOST_ARGS -d postgres -Atc "select 1 from pg_database where datname='pasarkita'" | grep -q 1; then
    "$PG/psql" $PGHOST_ARGS -d postgres -qc "CREATE DATABASE pasarkita" >/dev/null
    "$PG/psql" $PGHOST_ARGS -d pasarkita -q -f "$ROOT/infra/compose/postgres/init.sql" >/dev/null
    echo "  database pasarkita dibuat (ekstensi pg_stat_statements, pgcrypto)"
  fi
}

render_grafana() {
  local p="$RUN/grafana-provisioning"
  rm -rf "$p"; mkdir -p "$p/datasources" "$p/dashboards"
  cp "$HERE/grafana/provisioning/datasources/datasources.yaml" "$p/datasources/"
  sed "s#__DASHBOARDS__#$ROOT/infra/compose/grafana/dashboards#" \
    "$HERE/grafana/provisioning/dashboards/dashboards.yaml" > "$p/dashboards/dashboards.yaml"
}

cmd_up() {
  local profile="${1:-core,observability}"
  echo "Menyalakan komponen (profil: $profile)"
  pg_up
  need "$N/wiremock/wiremock-standalone-3.9.1.jar"
  start_bg wiremock "$(find_java)" -jar "$N/wiremock/wiremock-standalone-3.9.1.jar" \
    --port 8089 --root-dir "$ROOT/infra/compose/wiremock" --disable-banner --disable-http2-plain
  need "$N/prometheus/prometheus"
  mkdir -p "$DATA/prometheus"
  start_bg prometheus "$N/prometheus/prometheus" --config.file="$HERE/prometheus.yml" \
    --storage.tsdb.path="$DATA/prometheus" --web.listen-address=:9090
  need "$N/grafana/bin/grafana"
  render_grafana
  mkdir -p "$DATA/grafana" "$LOGS/grafana"
  GF_PATHS_DATA="$DATA/grafana" GF_PATHS_LOGS="$LOGS/grafana" \
  GF_PATHS_PROVISIONING="$RUN/grafana-provisioning" GF_PATHS_PLUGINS="$DATA/grafana/plugins" \
  GF_SERVER_HTTP_PORT=3000 GF_AUTH_ANONYMOUS_ENABLED=true GF_AUTH_ANONYMOUS_ORG_ROLE=Admin \
  GF_AUTH_DISABLE_LOGIN_FORM=true \
    start_bg grafana "$N/grafana/bin/grafana" server --homepath "$N/grafana"
  case ",$profile," in
    *,observability,*|*,all,*)
      need "$N/tempo/tempo"
      mkdir -p "$DATA/tempo"
      sed "s#__DATA__#$DATA#g" "$HERE/tempo.yaml" > "$RUN/tempo.yaml"
      start_bg tempo "$N/tempo/tempo" -config.file="$RUN/tempo.yaml"
      ;;
  esac
  wait_http http://localhost:8089/__admin/mappings wiremock 30
  wait_http http://localhost:9090/-/ready prometheus 30
  wait_http http://localhost:3000/api/health grafana 60
  case ",$profile," in *,observability,*|*,all,*) wait_http http://localhost:3200/ready tempo 60 || true;; esac
}

cmd_down() {
  echo "Mematikan komponen"
  for n in grafana tempo prometheus wiremock; do stop_bg "$n"; done
  if [ -f "$DATA/pg/PG_VERSION" ] && "$PG/pg_ctl" -D "$DATA/pg" status >/dev/null 2>&1; then
    "$PG/pg_ctl" -D "$DATA/pg" -m fast -w stop >/dev/null && echo "  berhenti: postgres"
  fi
}

cmd_status() {
  for n in wiremock prometheus grafana tempo; do
    if alive "$n"; then echo "  jalan   $n (pid $(cat "$RUN/$n.pid"))"; else echo "  mati    $n"; fi
  done
  if [ -f "$DATA/pg/PG_VERSION" ] && "$PG/pg_ctl" -D "$DATA/pg" status >/dev/null 2>&1; then echo "  jalan   postgres"; else echo "  mati    postgres"; fi
}

cmd_migrate() {
  need "$N/flyway/flyway"
  "$N/flyway/flyway" -url=jdbc:postgresql://localhost:5432/pasarkita -user=pasarkita -password=pasarkita \
    -locations="filesystem:$ROOT/src/main/resources/db/migration" migrate
}

cmd_seed() {
  local scale="${1:-S}" s p c o
  if [ "$scale" = "M" ]; then s=2000; p=50000; c=200000; o=1000000; else s=200; p=5000; c=10000; o=50000; fi
  # shellcheck disable=SC1091
  . "$ROOT/group.env"
  cmd_migrate
  "$PG/psql" $PGHOST_ARGS -d pasarkita -v ON_ERROR_STOP=1 -v seed="$GROUP_SEED" -v sellers=$s \
    -v products=$p -v customers=$c -v orders=$o -f "$ROOT/infra/seed/seed.sql"
}

cmd_psql() { exec "$PG/psql" $PGHOST_ARGS -d pasarkita "$@"; }

cmd_reset() {
  cmd_down
  rm -rf "$DATA/pg" "$DATA/prometheus" "$DATA/grafana" "$DATA/tempo"
  echo "Semua data dihapus."
}

case "${1:-}" in
  up) shift; cmd_up "$@";;
  down) cmd_down;;
  status) cmd_status;;
  migrate) cmd_migrate;;
  seed) shift; cmd_seed "$@";;
  psql) shift; cmd_psql "$@";;
  reset) cmd_reset;;
  *) echo "Pemakaian: $0 {up [profil]|down|status|migrate|seed [S|M]|psql|reset}" >&2; exit 2;;
esac
