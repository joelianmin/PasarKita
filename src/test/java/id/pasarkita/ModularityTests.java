package id.pasarkita;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Dijalankan terpisah lewat `task modules`, karena struktur modul di base code
 * memang belum sepenuhnya bersih.
 */
@Tag("structure")
class ModularityTests {

    private final ApplicationModules modules = ApplicationModules.of(PasarKitaApplication.class);

    @Test
    void writesDocumentation() {
        new Documenter(modules).writeDocumentation();
    }

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }
}
