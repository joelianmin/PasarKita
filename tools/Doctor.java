import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Memeriksa kesiapan lingkungan PasarKita. Dijalankan lewat: task doctor
 */
public class Doctor {

    private static int failures = 0;

    public static void main(String[] args) {
        System.out.println("Pemeriksaan lingkungan PasarKita");
        System.out.println("================================");

        int javaVersion = Runtime.version().feature();
        check("Java 21 atau lebih baru", javaVersion >= 21, "terdeteksi " + Runtime.version());

        command("Docker", List.of("docker", "--version"));
        command("Docker Compose", List.of("docker", "compose", "version"));
        command("Git", List.of("git", "--version"));
        command("k6", List.of("k6", "version"));
        dockerMemory();

        boolean wrapper = Files.exists(Path.of("mvnw")) && Files.exists(Path.of("mvnw.cmd"));
        check("Maven Wrapper (mvnw dan mvnw.cmd)", wrapper,
                wrapper ? "ada" : "belum ada; minta pengajar menambahkannya ke repo");

        System.out.println();
        System.out.println("Port (boleh terpakai jika `task up` atau `task app` sudah berjalan):");
        for (int port : new int[]{8080, 5432, 3000, 9090, 3200, 4318, 8089}) {
            System.out.printf("  %-5d %s%n", port, isFree(port) ? "bebas" : "terpakai");
        }

        System.out.println();
        if (failures == 0) {
            System.out.println("Semua pemeriksaan lulus.");
        } else {
            System.out.println(failures + " pemeriksaan gagal. Perbaiki sebelum melanjutkan.");
        }
        System.exit(failures == 0 ? 0 : 1);
    }

    private static void check(String name, boolean ok, String detail) {
        System.out.printf("[%s] %-38s %s%n", ok ? " OK " : "GAGAL", name, detail);
        if (!ok) {
            failures++;
        }
    }

    private static void command(String name, List<String> cmd) {
        Result result = run(cmd);
        check(name, result.ok(), result.ok() ? firstLine(result.output()) : "tidak ditemukan di PATH");
    }

    private static void dockerMemory() {
        Result result = run(List.of("docker", "info", "--format", "{{.MemTotal}}"));
        if (!result.ok()) {
            check("Memori Docker", false, "Docker belum berjalan? Buka Docker Desktop dulu");
            return;
        }
        try {
            long bytes = Long.parseLong(result.output().trim());
            double gib = bytes / (1024.0 * 1024 * 1024);
            check("Memori Docker minimal 6 GB", gib >= 5.5,
                    String.format("%.1f GB%s", gib, gib >= 5.5 ? "" : " (naikkan lewat .wslconfig)"));
        } catch (NumberFormatException e) {
            check("Memori Docker", false, "tidak bisa dibaca: " + firstLine(result.output()));
        }
    }

    private static boolean isFree(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            socket.setReuseAddress(true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Result run(List<String> cmd) {
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
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            return new Result(finished && process.exitValue() == 0, out.toString());
        } catch (Exception e) {
            return new Result(false, e.getMessage() == null ? "" : e.getMessage());
        }
    }

    private static String firstLine(String text) {
        String trimmed = text.strip();
        int newline = trimmed.indexOf('\n');
        return newline < 0 ? trimmed : trimmed.substring(0, newline);
    }

    private record Result(boolean ok, String output) {
    }
}
