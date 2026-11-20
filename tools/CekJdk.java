/**
 * Memastikan JDK 21 atau lebih baru sebelum menjalankan alat yang memakai sintaks Java 21 (mis. Evidence.java).
 * Sengaja ditulis dengan sintaks lama supaya pesan ini tetap tampil walau JDK yang aktif lebih lama.
 * Dijalankan lewat: java tools/CekJdk.java
 */
public class CekJdk {

    public static void main(String[] args) {
        String spec = System.getProperty("java.specification.version");
        int major;
        try {
            major = spec.startsWith("1.") ? Integer.parseInt(spec.substring(2)) : Integer.parseInt(spec);
        } catch (NumberFormatException e) {
            return;
        }
        if (major < 21) {
            System.err.println("Butuh JDK 21 atau lebih baru (terdeteksi: Java " + spec + ").");
            System.err.println("macOS: jalankan use-jdk21. Windows: periksa JAVA_HOME dan PATH. Lihat docs/materi/02-lingkungan-kerja.md.");
            System.exit(1);
        }
    }
}
