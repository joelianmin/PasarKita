package id.pasarkita.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Peringatan konfigurasi lama yang muncul berkala. Sengaja dibiarkan: ini noise kronis
 * yang dipakai di latihan membedah tiket.
 */
@Component
class LegacyConfigCheck {

    private static final Logger log = LoggerFactory.getLogger(LegacyConfigCheck.class);

    @Scheduled(fixedRate = 30_000)
    void warnDeprecatedProperty() {
        log.warn("Property 'pasarkita.legacy.timeout' is deprecated");
    }
}
