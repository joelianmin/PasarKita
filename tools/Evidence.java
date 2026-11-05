import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.List;

/**
 * Membuat file bukti dari hasil `task reproduce` terakhir, diikat ke commit dan seed kelompok
 * lewat hash SHA-256. Dijalankan lewat: task evidence LAB=0
 */
public class Evidence {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Pemakaian: task evidence LAB=<nomor>");
            System.exit(2);
        }
        String lab = args[0];
        Path out = Path.of("labs", lab, "out");
        Path summary = out.resolve("summary.json");
        Path table = out.resolve("baseline-table.md");
        if (!Files.exists(summary)) {
            System.err.println("Belum ada hasil k6 untuk lab " + lab + ". Jalankan dulu: task reproduce LAB=" + lab);
            System.exit(1);
        }

        String summaryJson = Files.readString(summary, StandardCharsets.UTF_8);
        String tableText = Files.exists(table) ? Files.readString(table, StandardCharsets.UTF_8) : "";
        String commit = git(List.of("git", "rev-parse", "HEAD"));
        boolean dirty = !git(List.of("git", "status", "--porcelain")).isBlank();
        String seed = readSeed();
        String time = OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        String material = String.join("\n", lab, commit, seed, time, summaryJson);
        String hash = HexFormat.of().formatHex(sha.digest(material.getBytes(StandardCharsets.UTF_8)));

        String fileName = "evidence-" + time.replace(":", "-") + ".md";
        Path evidence = out.resolve(fileName);
        String content = """
                # Bukti Lab %s

                | Data | Nilai |
                | --- | --- |
                | Commit | %s |
                | Perubahan belum di-commit | %s |
                | Seed kelompok | %s |
                | Waktu (UTC) | %s |
                | Hash SHA-256 | `%s` |

                ## Hasil pengukuran

                %s
                """.formatted(lab, commit, dirty ? "ADA (bukti tidak sah untuk penilaian)" : "tidak ada",
                seed, time, hash, tableText.isBlank() ? "(tidak ada tabel ringkasan)" : tableText);
        Files.writeString(evidence, content, StandardCharsets.UTF_8);

        System.out.println("Bukti dibuat: " + evidence);
        System.out.println("Hash: " + hash);
        if (dirty) {
            System.out.println("Peringatan: ada perubahan yang belum di-commit. Commit dulu, lalu ulangi reproduce dan evidence.");
        }
    }

    private static String readSeed() throws Exception {
        Path env = Path.of("group.env");
        if (!Files.exists(env)) {
            return "tidak diketahui";
        }
        for (String line : Files.readAllLines(env, StandardCharsets.UTF_8)) {
            String trimmed = line.strip();
            if (trimmed.startsWith("GROUP_SEED=")) {
                return trimmed.substring("GROUP_SEED=".length());
            }
        }
        return "tidak diketahui";
    }

    private static String git(List<String> cmd) {
        try {
            Process process = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            StringBuilder out = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line).append('\n');
                }
            }
            process.waitFor();
            return process.exitValue() == 0 ? out.toString().strip() : "tidak diketahui";
        } catch (Exception e) {
            return "tidak diketahui";
        }
    }
}
