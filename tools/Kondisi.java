import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Menyalakan atau mematikan kondisi latihan membedah tiket di database lokal.
 * Dipakai lewat: task latihan:mulai dan task latihan:selesai
 * Mencoba jalur Docker dulu, lalu jalur tanpa Docker (infra/native/native.sh).
 */
public class Kondisi {

    private static final String NAMA = "seller.dynamic-thumbnail";
    private static final DateTimeFormatter JAM = DateTimeFormatter.ofPattern("HH.mm.ss");
    private static final DateTimeFormatter JAM_MENIT = DateTimeFormatter.ofPattern("HH.mm");

    public static void main(String[] args) throws Exception {
        if (args.length != 1 || !(args[0].equals("mulai") || args[0].equals("selesai"))) {
            System.err.println("Pemakaian: java tools/Kondisi.java mulai|selesai");
            System.exit(2);
        }
        boolean nyala = args[0].equals("mulai");
        String sql = "UPDATE feature_flag SET enabled = " + nyala + " WHERE name = '" + NAMA + "'";

        String jalur = null;
        String keluaran = jalankan(List.of("docker", "compose", "-f", "infra/compose/compose.yaml",
                "exec", "-T", "postgres", "psql", "-U", "pasarkita", "-d", "pasarkita", "-c", sql));
        if (keluaran != null && keluaran.contains("UPDATE 1")) {
            jalur = "Docker";
        } else if (new File("infra/native/native.sh").exists()) {
            keluaran = jalankan(List.of("infra/native/native.sh", "psql", "-c", sql));
            if (keluaran != null && keluaran.contains("UPDATE 1")) {
                jalur = "tanpa Docker";
            }
        }
        if (jalur == null) {
            System.err.println("Gagal mengubah kondisi latihan. Pastikan database berjalan (task up atau task native:up),"
                    + " lalu coba lagi. Kalau masih gagal, kirim pesan di atas ke pengajar.");
            if (keluaran != null) {
                System.err.println(keluaran.strip());
            }
            System.exit(1);
        }

        ZonedDateTime sekarang = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
        if (nyala) {
            System.out.println("Kondisi latihan DINYALAKAN (jalur " + jalur + ") pukul " + sekarang.format(JAM) + " WIB.");
            System.out.println("Tiketmu melaporkan masalah sekitar pukul " + sekarang.plusMinutes(10).format(JAM_MENIT)
                    + " WIB (10 menit sesudah ini). Catat kedua jam itu.");
            System.out.println("Berikutnya: jalankan 'task traffic DURATION=3h' di terminal terpisah, lalu tunggu sekitar 10 menit");
            System.out.println("agar grafik di Grafana menunjukkan perubahan.");
        } else {
            System.out.println("Kondisi latihan DIMATIKAN (jalur " + jalur + ") pukul " + sekarang.format(JAM) + " WIB.");
        }
    }

    private static String jalankan(List<String> perintah) {
        try {
            Process p = new ProcessBuilder(perintah).redirectErrorStream(true).start();
            String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (!p.waitFor(60, TimeUnit.SECONDS)) {
                p.destroyForcibly();
                return null;
            }
            return out;
        } catch (Exception e) {
            return null;
        }
    }
}
