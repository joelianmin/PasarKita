package id.pasarkita.catalog;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Membaca feature flag dari tabel feature_flag, dengan cache singkat.
 * Diubah lewat: task flag NAME=... ON=true|false
 */
@Component
public class FeatureFlags {

    private static final Duration TTL = Duration.ofSeconds(5);

    private final JdbcTemplate jdbc;
    private final Map<String, Cached> cache = new ConcurrentHashMap<>();

    public FeatureFlags(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean isEnabled(String name) {
        Cached cached = cache.get(name);
        if (cached != null && cached.loadedAt().plus(TTL).isAfter(Instant.now())) {
            return cached.enabled();
        }
        List<Boolean> rows = jdbc.queryForList("select enabled from feature_flag where name = ?", Boolean.class, name);
        boolean enabled = !rows.isEmpty() && Boolean.TRUE.equals(rows.get(0));
        cache.put(name, new Cached(enabled, Instant.now()));
        return enabled;
    }

    private record Cached(boolean enabled, Instant loadedAt) {
    }
}
